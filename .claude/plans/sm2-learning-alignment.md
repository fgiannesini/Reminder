# Plan : Aligner l'algorithme sur la science de l'apprentissage (SM-2)

## Problèmes identifiés (par criticité)

| # | Problème | Cause dans le code |
|---|----------|-------------------|
| P1 | 1 bonne réponse = mot appris (au lieu de 3) | `new Word(String, String)` initialise `checkedCount = repetitionLimitToLearn = 3` |
| P2 | Intervalle fixe 1 semaine pour toutes les révisions | Query `learnt_moment < now - 1 week` ; aucun intervalle adaptatif |
| P3 | Reset total sur erreur en phase révision | `reset()` efface `checkedCount`, `learntCount`, `learnedMoment` même si le mot était maîtrisé |
| P4 | Seulement 2 confirmations pour maîtrise | `learntCount < 2` en dur — insuffisant pour mémoire long terme |
| P5 | CLOSED traité identique à MATCHED | Les deux appellent `checked()` sans distinction |
| P6 | Sélection aléatoire parmi 20 éligibles | `randomProvider.nextInt()` ignore urgence/retard |

---

## Algorithme SM-2 (référence)

```
Entrées: q (qualité réponse), n (répétitions), EF (ease factor), I (intervalle jours)

Si q >= 3 (succès):
  I₁ = 1 jour
  I₂ = 6 jours
  Iₙ₊₁ = round(Iₙ × EF)
  EF_new = max(1.3, EF + 0.1 - (5-q)×(0.08 + (5-q)×0.02))
  n++

Si q < 3 (échec en révision):
  n = 0
  I = 1 jour
  EF_new = max(1.3, EF - 0.2)
  (NE PAS retourner en phase apprentissage)

Qualité: MATCHED=5, CLOSED=3, NOT_MATCHED=0
```

---

## Phases d'implémentation

---

### Phase A — Corriger le bug critique de l'apprentissage initial

**Impact :** Actuellement 1 réponse suffit. Après : 3 réponses correctes en session avant la 1ère révision espacée.

**Fichiers :** `Word.java` uniquement

**Changement :**
```java
// AVANT
public Word(String word, String translation) {
    this(word, translation, repetitionLimitToLearn, null, 0);
}

// APRÈS
public Word(String word, String translation) {
    this(word, translation, 0, null, 0);
}
```

**Logique résultante avec le `checked()` existant :**
- checkedCount 0→1→2→3 : 3 bonnes réponses en session → `shouldBeMarkedAsLearnt()` true → `learnedMoment` set → mot entre en révision
- Erreur en phase apprentissage : `reset()` → checkedCount=0 (comportement actuel, correct ici)

**Tests à mettre à jour :** `DictionaryTest` — les `new Word("x", "y")` dans `expected` listes ont maintenant checkedCount=0 au lieu de 3.

---

### Phase B — Modèle de données SM-2

**Impact :** Ajoute les champs nécessaires à l'espacement adaptatif.

**Fichier `Word.java` :**
```java
// Nouveaux champs dans le record
public record Word(
    String wordToLearn,
    String translation,
    int checkedCount,         // 0→3 : phase apprentissage session
    LocalDateTime nextReview, // remplace learnedMoment sémantiquement
    int smRepetitions,        // remplace learntCount : compteur SM-2
    float easeFactor,         // SM-2, défaut 2.5
    int intervalDays          // intervalle courant en jours
) {
    private static final float DEFAULT_EASE_FACTOR = 2.5f;
    private static final int MASTERY_REPETITIONS = 8; // ~6 mois d'espacement
    
    public Word(String word, String translation) {
        this(word, translation, 0, null, 0, DEFAULT_EASE_FACTOR, 1);
    }
    
    public boolean isLearningPhase() {
        return checkedCount < repetitionLimitToLearn;
    }
    
    public boolean isMastered() {
        return smRepetitions >= MASTERY_REPETITIONS;
    }
}
```

**Fichier `WordDao.java` :**
```java
// Ajouter colonnes
private float easeFactor = 2.5f;
private int intervalDays = 1;
// learnt_count → sm_repetitions (renommer ou garder l'ancien nom)
// learnt_moment → next_review (renommer ou garder l'ancien nom)
```
Spring DDL auto-update ajoute les colonnes manquantes sans migration manuelle.

**Mise à jour `WordDao.fromWord()` / `toWord()` :** mapper les 2 nouveaux champs.

**Rétrocompatibilité :** anciens enregistrements auront `easeFactor=0` (NULL en DB) — ajouter valeur par défaut dans le constructeur WordDao.

---

### Phase C — Algorithme SM-2 dans `Word.java`

**Remplacer `checked()` et `reset()` :**

```java
public Word checked(int quality, LocalDateTime now) {
    if (isLearningPhase()) {
        // Phase apprentissage : compter jusqu'à repetitionLimitToLearn
        int newCount = checkedCount + 1;
        if (newCount == repetitionLimitToLearn) {
            // Graduation : 1er intervalle = 1 jour
            return new Word(wordToLearn, translation, newCount,
                now.plusDays(1), 1, easeFactor, 1);
        }
        return new Word(wordToLearn, translation, newCount,
            null, 0, easeFactor, 1);
    }
    
    // Phase révision : SM-2
    if (quality >= 3) {
        int newRep = smRepetitions + 1;
        int newInterval = switch (smRepetitions) {
            case 0 -> 1;
            case 1 -> 6;
            default -> Math.round(intervalDays * easeFactor);
        };
        float newEF = Math.max(1.3f,
            easeFactor + 0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f));
        return new Word(wordToLearn, translation, checkedCount,
            now.plusDays(newInterval), newRep, newEF, newInterval);
    } else {
        // Échec en révision : pénaliser sans retour en apprentissage
        float newEF = Math.max(1.3f, easeFactor - 0.2f);
        return new Word(wordToLearn, translation, checkedCount,
            now.plusDays(1), 0, newEF, 1);
    }
}

public Word reset() {
    if (isLearningPhase()) {
        // Apprentissage : reset session uniquement
        return new Word(wordToLearn, translation, 0, null, 0, easeFactor, 1);
    }
    // Ne devrait pas être appelé en phase révision
    // (le contrôleur appelle checked(0) à la place)
    return this;
}
```

**Supprimer `shouldBeMarkedAsLearnt()`** — remplacer par `isLearningPhase()` et `isMastered()`.

---

### Phase D — Contrôleur : passer la qualité

**Fichier `ReminderController.java` :**

```java
@PostMapping("/check")
public TranslationResponseDto check(@RequestBody TranslationDto translation) {
    Word wordToLearn = dictionary.find(translation.wordToLearn());
    Matching matching = wordToLearn.getMatching(translation.proposedTranslation());

    Word newWord = switch (matching) {
        case MATCHED -> wordToLearn.checked(5, LocalDateTime.now());
        case CLOSED  -> wordToLearn.checked(3, LocalDateTime.now());
        case NOT_MATCHED -> wordToLearn.isLearningPhase()
            ? wordToLearn.reset()
            : wordToLearn.checked(0, LocalDateTime.now());
    };
    dictionary.update(newWord);
    return new TranslationResponseDto(matching, newWord.translation(), newWord.isMastered());
}
```

**DTO `TranslationResponseDto` :** champ `shouldBeMarkedAsLearnt` → renommer `mastered` (optionnel, breaking change frontend si renommé).

---

### Phase E — Requête SQL adaptative

**Fichier `WordRepository.java` :**

```sql
-- AVANT
WHERE w.learnt_count < 2
  AND (w.learnt_moment is null or w.learnt_moment < :aWeekAgo)
ORDER BY w.learnt_moment IS NULL DESC, w.checked_count, w.learnt_moment

-- APRÈS
WHERE w.sm_repetitions < 8
  AND (w.next_review is null or w.next_review <= :now)
ORDER BY w.next_review IS NULL DESC, w.next_review ASC
```

**`DatabaseStorageHandler.getNextWords()` :** passer `LocalDateTime.now()` au lieu de `now.minusWeeks(1)`.

**`StorageHandler` interface :** mettre à jour signature si nécessaire.

**`getRemainingWordsCountToLearn()` :** `countByNextReviewIsNull()` (mots pas encore en révision)

**`getRemainingWordsCountToConfirm()` :** `countBySmRepetitionsLessThan(8)` (mots pas encore maîtrisés)

---

### Phase F — Sélection : priorité aux mots en retard

**Fichier `Dictionary.next()` :**

La requête SM-2 ordonnant par `next_review ASC`, les mots les plus en retard arrivent en tête. Remplacer sélection aléatoire pure par : aléatoire parmi les 5 premiers (préserve interleaving, priorise urgence).

```java
public Word next(int limit) {
    var eligibleWords = storageHandler.getNextWords(limit, LocalDateTime.now());
    if (eligibleWords.isEmpty()) throw new NoSuchElementException("No eligible words available");
    int selectionPool = Math.min(5, eligibleWords.size());
    return eligibleWords.get(randomProvider.nextInt(selectionPool));
}
```

---

### Phase G — Exclure le mot inverse pendant N tours

**Problème :** "desligar → éteindre" suivi de "éteindre → desligar" dans les 10 tours suivants = mot encore en mémoire vive, aucune valeur d'apprentissage.

**Approche :** `Dictionary` maintient un `Deque<String>` des derniers mots montrés (fenêtre = 10). Pas de changement d'API ni de param client.

**Fichier `Dictionary.java` :**

```java
private final Deque<String> recentWords = new ArrayDeque<>();
private static final int RECENT_WINDOW = 10;

public Word next(int reviewLimit) {
    var candidates = ...; // logique Phase H

    // Filtrer les inverses des mots récents
    var filtered = candidates.stream()
        .filter(w -> recentWords.stream()
            .noneMatch(recent -> w.translation().equals(recent)))
        .toList();
    if (!filtered.isEmpty()) candidates = filtered; // fallback si pool trop petit

    var word = candidates.get(randomProvider.nextInt(Math.min(5, candidates.size())));

    recentWords.addLast(word.wordToLearn());
    if (recentWords.size() > RECENT_WINDOW) recentWords.removeFirst();

    return word;
}
```

**Pas de changement** dans `ReminderController`, `StorageHandler`, ni frontend.

**Limite :** état en mémoire — perdu au redémarrage. Acceptable pour app single-instance.

---

### Phase H — Limiter le débit de nouveaux mots (6000 mots)

**Problème :** sans limite, tous les nouveaux mots sont éligibles simultanément → file de révision explose à J+1.

**Principe :** révisions dues toujours en priorité. Nouveaux mots seulement si file de révisions vide ou sous seuil.

**Constante :** 20 nouveaux mots/session max (configurable).

**Fichier `WordRepository.java` — nouvelle requête :**

```sql
-- Révisions dues (next_review <= now, déjà en révision)
SELECT * FROM word w
WHERE w.sm_repetitions < 8
  AND w.next_review IS NOT NULL
  AND w.next_review <= :now
ORDER BY w.next_review ASC
LIMIT :limit

-- Nouveaux mots (pas encore en révision)
SELECT * FROM word w
WHERE w.next_review IS NULL
ORDER BY RANDOM()
LIMIT :newWordsLimit
```

**Fichier `StorageHandler.java` :** ajouter `getDueReviews(int limit, LocalDateTime now)` et `getNewWords(int limit)`.

**Fichier `Dictionary.next()` :**

```java
private static final int MAX_NEW_WORDS_PER_SESSION = 20;

public Word next(int reviewLimit, String lastWordToLearn) {
    var dueReviews = storageHandler.getDueReviews(reviewLimit, LocalDateTime.now());
    List<Word> candidates;
    
    if (dueReviews.isEmpty()) {
        // Pas de révisions dues → introduire nouveaux mots
        candidates = storageHandler.getNewWords(MAX_NEW_WORDS_PER_SESSION);
    } else {
        candidates = dueReviews;
    }
    
    if (lastWordToLearn != null) {
        var filtered = candidates.stream()
            .filter(w -> !w.translation().equals(lastWordToLearn))
            .toList();
        if (!filtered.isEmpty()) candidates = filtered;
    }
    
    if (candidates.isEmpty()) throw new NoSuchElementException("No eligible words available");
    int selectionPool = Math.min(5, candidates.size());
    return candidates.get(randomProvider.nextInt(selectionPool));
}
```

**Résultat avec 6000 mots :** ~20 nouveaux/jour → 300 jours pour couvrir tout le vocabulaire. File de révision journalière : ~50-100 cartes au régime de croisière.

---

## Ordre d'implémentation recommandé

```
Phase A → Phase B+C+D+E (couplées) → Phase H → Phase F+G
```

- **A** : isolée, livrable seule (bugfix critique, 1 ligne)
- **B+C+D+E** : couplées (data model + algo + query) — livrer ensemble
- **H** : dépend de E (nouvelles requêtes sur le nouveau schéma)
- **F+G** : raffinements indépendants, livrables ensemble

---

## Fichiers touchés

| Fichier | Phases |
|---------|--------|
| `Word.java` | A, B, C |
| `WordDao.java` | B |
| `WordRepository.java` | E, H |
| `DatabaseStorageHandler.java` | E, H |
| `StorageHandler.java` | H |
| `ReminderController.java` | D |
| `Dictionary.java` | F, G, H |
| `MemoryStorageHandler.java` | E, H (tests) |
| `DictionaryTest.java` | A, B, C, E, G, H |

---

## Ce qui n'est PAS dans ce plan (hors scope)

- Validation fuzzy des expressions / phrases — volontairement exclu
- Phrases d'exemple en contexte (nécessite données CSV supplémentaires)
- Rating explicite utilisateur (boutons facile/difficile) — qualité inférée de MATCHED/CLOSED/NOT_MATCHED
- Distinction réceptif/productif (déjà géré par bidirectionnel)
