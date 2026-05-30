# Améliorations de performance — Reminder

## Contexte

Application de flashcards Spring Boot 4 / Java 25 / PostgreSQL. Le dictionnaire source
(`dictionary.csv`) contient ~3142 lignes, dupliquées au chargement (`addDuplicates`), soit
~6284 lignes en base. Trois endpoints REST : `GET /next`, `POST /check`, `GET /remaining`.

Analyse menée sur : `Dictionary`, `DatabaseStorageHandler`, `WordRepository`, `WordDao`,
`Word`, `SmRepetition`, `RecentWordsWindow`, `ReminderController`, `SpringMain`, `index.html`.

### Bottlenecks identifiés (du plus impactant au moins impactant)

**P1 — `Dictionary.load()` est O(n²) et s'exécute à chaque démarrage (`SpringMain.dictionary`)**
Fichier : `src/main/java/com/fgiannesini/Dictionary.java:37-52`.
- Pour chaque mot original (~3142), `noneMatch` parcourt `existingWords` (~6284) →
  ~20 millions de comparaisons au boot, et autant pour `wordsToRemove`.
- Pire : `isWordOrDuplicate` (ligne 50-52) appelle `buildDuplicate(word2)` qui **alloue un
  nouvel objet `Word` à chaque comparaison** → ~40M allocations inutiles.
- Cause : comparaison par balayage linéaire au lieu d'une structure indexée (Set/Map par clé
  `(wordToLearn, translation)`).
Fix proposé : construire un `Set<String>` (ou `Set<record clé>`) des mots existants une seule
fois, puis tester l'appartenance en O(1). Calculer la clé canonique sans allouer de `Word`.

**P2 — Index manquants sur la table `word`**
Fichier : `src/main/java/com/fgiannesini/web/storage/WordRepository.java:14-26`, `WordDao.java`.
- `getTopOrderByNextReview` filtre/trie sur `sm_repetitions` et `next_review` → full scan + tri
  sur ~6284 lignes à chaque `GET /next`.
- `countByNextReviewIsNull` et `countBySmRepetitionsGreaterThanEqualAndSmRepetitionsLessThan`
  (utilisés par `GET /remaining`) → full scan à chaque appel, et `/remaining` est rappelé au
  chargement de page.
- Aucun index déclaré sur `WordDao` au-delà de la PK `word`.
Fix proposé : ajouter via `@Table(indexes = {...})` un index composite
`(sm_repetitions, next_review)` couvrant la requête `/next` et les deux comptages.
`ddl-auto: update` créera l'index automatiquement.

**P3 — `getMatching` recompile/normalise à chaque appel (`POST /check`)**
Fichier : `src/main/java/com/fgiannesini/Word.java:15-46`.
- `cleanPunctuationAndSpaces` appelle `String.replaceAll` (compile une `Pattern` à chaque
  invocation) ; `cleanAccents` idem avec `\\p{M}`. Avec produit cartésien
  traductions × inputs, plusieurs (re)compilations de regex par requête.
Fix proposé : extraire les `Pattern` en constantes `static final` compilées une fois et
réutiliser via `pattern.matcher(s).replaceAll("")`. Impact faible en volume (1 requête utilisateur)
mais correction triviale et sûre.

**P4 — `POST /check` fait un SELECT puis un UPDATE séparés + double appel API côté front**
Fichier : `ReminderController.java:30-37`, `DatabaseStorageHandler.java:33-42`, `index.html:426`.
- `find` (SELECT) puis `update` (`save` = SELECT d'existence + UPDATE sous Hibernate) →
  jusqu'à 3 allers-retours DB par check. Acceptable mais notable.
- Front : `/remaining` n'est appelé qu'au `DOMContentLoaded` (ligne 426), donc les stats
  ne se rafraîchissent jamais après un check → soit bug d'affichage, soit appel manquant.
  À clarifier (hors scope perf pur). Aucune action perf imposée ici, juste documenté.

**P5 — `RecentWordsWindow.containsTranslation` compare `translation` contre des `wordToLearn`**
Fichier : `Dictionary.java:60-64`, `RecentWordsWindow.java:10-17`.
- `next()` filtre les candidats avec `recentWordsWindow.containsTranslation(w.translation())`
  (ligne 60) mais `add` stocke `word.wordToLearn()` (ligne 64). On compare donc une traduction
  à des `wordToLearn` → le filtre ne matche probablement jamais : la fenêtre anti-répétition
  est inopérante. Bug fonctionnel, impact perf nul mais à signaler.
- `containsTranslation` utilise un stream `anyMatch` sur 10 éléments : négligeable.
Fix proposé : aligner clé stockée et clé testée (les deux sur `wordToLearn` ou les deux sur
`translation`). À traiter comme correctif, pas comme perf.

**P6 — `load()` charge toute la table en mémoire au boot**
Fichier : `DatabaseStorageHandler.java:20-22`.
- `wordRepository.findAll()` matérialise ~6284 entités + mapping vers `Word`. Couplé à P1,
  pic mémoire/CPU au démarrage. Le fix P1 le rend acceptable ; on peut aussi ne charger que
  les colonnes `word`+`translation` via une projection si besoin.
Fix proposé (optionnel) : projection `interface WordKey { String getWord(); String getTranslation(); }`
pour éviter de matérialiser les colonnes SM-2 inutiles au seeding.

### Points vérifiés SANS problème
- `SmRepetition.apply` : O(1), pas de souci.
- Pas de N+1 JPA classique (pas de relations `@OneToMany`).
- DTOs et mapping : allocations négligeables par requête.

## Tâches

1. [ ] **P2 — Index DB** — Ajouter sur `WordDao`
   `@Table(name = "word", indexes = @Index(name = "idx_word_sm_review", columnList = "smRepetitions, nextReview"))`.
   Test : `WordRepositoryTest` (TestContainers) vérifiant que `getTopOrderByNextReview`,
   `countByNextReviewIsNull` et `countBySmRepetitionsGreaterThanEqualAndSmRepetitionsLessThan`
   renvoient les bons résultats sur un jeu de données (non-régression fonctionnelle). Optionnel :
   assertion via `EXPLAIN` que l'index est utilisé.

2. [ ] **P1 — `Dictionary.load()` en O(n)** — Remplacer les `noneMatch` imbriqués par des
   `Set` de clés canoniques. Introduire une clé `(wordToLearn, translation)` calculée sans
   allouer de `Word` ; tenir compte du duplicata (clé inversée). Conserver le comportement
   exact (mêmes mots ajoutés/supprimés). Tests : `DictionaryTest`
   - `load_ajoute_les_mots_absents_et_leurs_duplicats`
   - `load_ne_reajoute_pas_un_mot_deja_present_ni_son_duplicat`
   - `load_supprime_les_mots_absents_du_csv`
   - cas vide des deux côtés.
   Vérifier que `StorageHandler.save`/`delete` reçoivent exactement les mêmes listes qu'avant
   (mock/fake storage avec capture d'arguments).

3. [ ] **P3 — Patterns compilées une fois dans `Word`** — Extraire deux `static final Pattern`
   (`NON_LETTER`, `ACCENT_MARK`) et les réutiliser dans `cleanPunctuationAndSpaces` /
   `cleanAccents`. Aucun changement de comportement. Tests : `WordTest`
   - `getMatching_retourne_MATCHED_sur_ponctuation_et_casse_differentes`
   - `getMatching_retourne_CLOSED_sur_accents_differents`
   - `getMatching_retourne_NOT_MATCHED_sinon`
   - multi-traductions séparées par virgule.

4. [ ] **P5 — Corriger la fenêtre anti-répétition** — Aligner la clé utilisée dans
   `Dictionary.next()` (`Dictionary.java:60` et `:64`) : filtrer et stocker sur la même
   propriété (`wordToLearn`). Renommer `containsTranslation` en conséquence si pertinent.
   Tests : `DictionaryTest`
   - `next_exclut_un_mot_present_dans_la_fenetre_recente`
   - `next_reautorise_un_mot_apres_10_selections`
   - `next_retombe_sur_les_candidats_si_tous_filtres` (cas `filtered.isEmpty()`).
   Utiliser un `RandomGenerator` déterministe (seed fixe) pour la sélection.

5. [ ] **P6 (optionnel) — Projection au seeding** — Ajouter une méthode de projection
   `List<WordKey> findAllKeys()` dans `WordRepository` et l'utiliser dans le chemin de seeding
   (`Dictionary.load`) pour ne pas matérialiser les colonnes SM-2. À ne faire qu'après P1/P2
   si le boot reste lent. Tests : non-régression `DictionaryTest` (déjà couverts en tâche 2).

## Vérification par tâche

Après chaque tâche :
- `./gradlew compileJava compileTestJava` — aucune erreur de compilation
- `./gradlew test --tests "com.fgiannesini.<ClasseTest>"` — tous tests passent
  - Tâche 1 : `com.fgiannesini.web.storage.WordRepositoryTest`
  - Tâche 2 : `com.fgiannesini.DictionaryTest`
  - Tâche 3 : `com.fgiannesini.WordTest`
  - Tâche 4 : `com.fgiannesini.DictionaryTest`
  - Tâche 5 : `com.fgiannesini.DictionaryTest`
- Avant de pousser : `./gradlew test` complet (TestContainers démarre PostgreSQL).
