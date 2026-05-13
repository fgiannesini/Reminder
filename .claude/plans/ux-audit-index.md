F# Audit UX/UI — index.html (Reminder / Flashcards)

Date : 2026-05-13  
Fichier analysé : `src/main/resources/static/index.html`  
Contexte : application PC + mobile, apprentissage de vocabulaire par flashcards.

---

## 1. Structure HTML et sémantique

### Points positifs
- `<!DOCTYPE html>` présent, `lang="fr"` correctement positionné.
- `<meta name="viewport">` avec `width=device-width, initial-scale=1.0` — support mobile de base.
- Hiérarchie des titres correcte (`<h1>` unique pour le titre de la page).
- `box-sizing: border-box` appliqué globalement, bonne pratique.
- `min-height: 100dvh` sur `body` — utilise l'unité moderne qui prend en compte les barres d'outils mobiles.
- La balise `<script>` est placée après `</body>`, évitant le blocage du rendu.

### Problèmes identifiés
- **`<label for="word-to-learn-span">` est invalide** : `for` doit pointer vers un élément de formulaire interactif (`input`, `select`, etc.). Un `<span>` n'est pas un contrôle associable ; la relation sémantique label/contrôle est donc brisée.
- **`result-feedback` contient un `<label>` flottant** (`Real Translation`) non associé à aucun contrôle. Il devrait être un `<p>`, un `<span>` ou un `<dt>` selon le contexte.
- **Absence de `<main>`** : le contenu principal n'est pas balisé par un élément landmark `<main>`, ce qui pénalise la navigation par landmark pour les lecteurs d'écran.
- **`<h1>` générique** : le titre "Reminder" n'informe pas l'utilisateur du contexte actif (quel mot, quel mode d'apprentissage).
- **`<span id="error-span">`** affiché via `style="display:none"` inline puis modifié en JS : fonctionne, mais l'attribut `role="alert"` (ou `aria-live="assertive"`) est absent — les messages d'erreur ne seront pas annoncés automatiquement par les lecteurs d'écran.
- Le bouton Submit n'a pas d'attribut `type="button"` (ni `type="submit"`). Dans un contexte sans `<form>`, l'absence de `type` ne cause pas de soumission involontaire, mais c'est une mauvaise pratique de robustesse.

---

## 2. Responsive design (mobile vs PC)

### Points positifs
- `max-width: 480px` sur `.container` : adapté à un usage en colonne unique, centré sur grands écrans.
- `flex: 1 1 0` sur l'input et `white-space: nowrap` sur le bouton Submit empêchent les débordements dans `.input-block`.
- `flex-wrap: wrap` sur `.stats-block` : les badges de statistiques passent à la ligne si nécessaire.

### Problèmes identifiés
- **Hauteur fixe des champs et boutons à `2.5rem` (40 px)** : conforme aux guidelines Android/iOS (44 px recommandé, 40 px accepté), mais juste en limite basse sur petits écrans.
- **Aucun breakpoint media query** : la mise en page ne s'adapte pas aux très petits écrans (< 320 px) ni aux grands écrans (> 1024 px). L'`padding: 1.5rem` de `body` reste fixe quelle que soit la taille.
- **Padding du container** : sur mobile, le padding horizontal de `1.5rem` (24 px) de part et d'autre laisse une zone utile étroite sur un iPhone SE (320 px → 272 px utiles).
- **Pas de gestion du mode paysage sur mobile** : en orientation paysage, la hauteur disponible est réduite ; le bloc résultat peut être partiellement masqué par le clavier virtuel sans scroll visible.
- **Font-size de 1rem (16 px) sur l'input** : correct pour éviter le zoom automatique sur iOS (seuil à 16 px). Si la font était inférieure, iOS zoomerait au focus — ce point est bien géré.

---

## 3. Ergonomie du flux d'apprentissage

### Points positifs
- Chargement du premier mot automatique au `DOMContentLoaded` : l'utilisateur n'a pas à cliquer pour démarrer.
- Après Submit, le focus est déplacé automatiquement sur le bouton "Next" via `document.getElementById('next-button').focus()`.
- Après "Next", le focus est replacé sur l'input : le cycle clavier est cohérent.
- Support de la touche `Enter` sur l'input pour soumettre : accélère considérablement le flux sur PC.
- Les boutons Submit et Next sont désactivés (`disabled`) pendant les requêtes réseau : prévient les doubles soumissions.

### Problèmes identifiés
- **La touche `Enter` n'est pas gérée sur le bouton "Next"** : l'élément reçoit le focus après la réponse, mais appuyer sur `Enter` ne déclenche pas `getNextWord()`. Le navigateur devrait déclencher `onclick` via `Enter` pour un `<button>` focalisé — à vérifier. En pratique, si `onclick` sur un `<button>` est standard, cela fonctionne ; mais l'absence de `type="button"` explicite et l'usage de `onclick` inline peuvent masquer des comportements inattendus.
- **Pas de raccourci clavier global** : il n'est pas possible de passer au mot suivant avec `Space` ou `Enter` sans que le focus soit déjà sur le bon bouton, ce qui nécessite un `Tab` supplémentaire si l'utilisateur a cliqué hors du champ.
- **Le bouton "Next" est toujours visible**, même avant que l'utilisateur ait soumis une réponse. Cliquer dessus sans avoir soumis charge un nouveau mot sans feedback, faisant perdre le mot courant silencieusement.
- **Aucune confirmation ni protection contre le "Skip"** : passer au mot suivant avant de répondre est possible sans avertissement.
- **L'enchaînement PC est fluide, mais l'enchaînement tactile est plus lent** : après avoir soumis sur mobile (tap sur Submit), le focus ne va pas vers un bouton facilement accessible du pouce. Le bouton "Next" est positionné sous le feedback, ce qui peut nécessiter un scroll vers le bas sur petits écrans.
- **Pas de gestion du cas "mot vide"** (`word-to-learn-span` vide au chargement initial pendant la requête) : si l'utilisateur clique immédiatement sur Submit avant que `getNextWord()` n'ait répondu, il envoie `wordToLearn = ""`.
- **Les statistiques ne sont chargées qu'au démarrage** (`setRemainingWordsCount()` appelé une seule fois), puis plus mises à jour lors des appels `check` ou `next`. L'utilisateur voit des compteurs figés qui ne reflètent pas la progression réelle.

---

## 4. Feedback visuel

### Points positifs
- Trois états distincts avec couleur de fond + bordure : vert (MATCHED), rouge (NOT_MATCHED), jaune/orange (CLOSED).
- Transition CSS sur `background` et `border-color` (0.2 s) : changement d'état progressif, non brutal.
- Spinner animé via `::after` sur les boutons en état `loading` : feedback réseau clair.
- Affichage de la vraie traduction dans tous les cas (MATCHED / NOT_MATCHED / CLOSED).
- Emoji ✅ / ❌ / 👌 préfixant la traduction réelle : signal visuel immédiat et mémorable.
- Mention "learned !" lorsque `check.learned` est vrai : récompense positive.

### Problèmes identifiés
- **Les emojis comme seuls signaux sémantiques** : ✅ ❌ 👌 ne sont pas accessibles sans texte alternatif. Un lecteur d'écran lira "white heavy check mark", "cross mark", "ok hand" — verbeux et peu intuitif. Ces emojis sont concaténés directement dans le `textContent`, sans aria-label ni `<abbr>`.
- **"learned !" en minuscule sans ponctuation homogène** : style textuel incohérent (`learned !` vs le reste de l'interface).
- **Le bloc `result-feedback` est toujours affiché** même avant toute soumission : il montre le label "Real Translation" avec un `<span>` vide, ce qui crée un espace vide visuellement déroutant au démarrage.
- **Pas de différenciation visuelle entre MATCHED et CLOSED suffisamment forte** : vert vs orange peut être insuffisant pour les utilisateurs daltoniens (deutéranopie : verts et oranges sont proches).
- **Absence d'animation ou de transition sur le mot** : quand un nouveau mot apparaît, il remplace l'ancien sans transition. Sur PC, l'effet est abrupt ; sur mobile, l'oeil peut manquer le changement.
- **Le spinner sur le bouton "Next" est peu visible** : le spinner dark-on-light (bordure grise sur fond gris clair #e8e8e8) a un contraste faible.

---

## 5. Accessibilité

### Points positifs
- `aria-label="Translation"` sur l'input : la zone de saisie est identifiée pour les lecteurs d'écran.
- Focus visible sur `#next-button` via `focus-visible` avec outline + box-shadow : la navigation clavier est partiellement prise en compte.
- Transitions courtes (0.15 s – 0.2 s) : respectueuses des utilisateurs sensibles aux animations (valeur < 500 ms, seuil WCAG).
- Couleur de texte `#1a1a1a` sur fond `#f5f5f5` : ratio de contraste approximatif ~12:1, très au-dessus du seuil WCAG AA (4.5:1).

### Problèmes identifiés
- **`#translation-input` n'a pas de `focus-visible`** explicite. L'outline est supprimé (`outline: none`) et remplacé par `border-color + box-shadow`, ce qui est acceptable visuellement mais peut être insuffisant selon les navigateurs/modes.
- **Aucun `aria-live` sur `#result-feedback`** : quand la réponse arrive (changement de classe + texte), aucun lecteur d'écran n'est notifié automatiquement. Il faudrait `aria-live="polite"` ou `role="status"`.
- **Aucun `aria-live` sur `#error-span`** : les erreurs réseau ne sont pas annoncées ; `role="alert"` ou `aria-live="assertive"` est manquant.
- **Bouton Submit sans `aria-label` distinct** : son texte "Submit" est acceptable, mais l'absence de référence à l'action contextuelle (quel mot ?) rend le bouton générique.
- **`label for="word-to-learn-span"` invalide** (voir §1) : pénalise la navigation par formulaire.
- **Taille de cibles tactiles** : le bouton "Next" n'a pas de hauteur fixe définie (contrairement au Submit qui a `height: 2.5rem`). Sa hauteur dépend du padding (`0.55rem` top + bottom + line-height ≈ ~38–40 px), ce qui est en limite basse. Sur les guidelines iOS HIG, 44×44 pt est recommandé.
- **Contrastes des états feedback** :
  - MATCHED : texte `#1a1a1a` sur fond `#e6f4ea` → ratio ~11:1, OK.
  - NOT_MATCHED : texte `#1a1a1a` sur fond `#fdecea` → ratio ~11:1, OK.
  - CLOSED : texte `#1a1a1a` sur fond `#fff8e1` → ratio ~13:1, OK.
  - Bordures colorées seules (4caf50, e53935, f59e0b) ne transmettent pas l'information à elles seules (la couleur ne doit pas être le seul vecteur d'information — WCAG 1.4.1).
- **Contraste du label "Real Translation"** : `#888` sur fond `#f0f0f0` (état initial) → ratio ≈ 2.7:1, **inférieur au seuil WCAG AA de 4.5:1** pour le texte de taille normale.
- **Contraste des badges stats** : texte `#555` sur fond `#fff` → ratio ≈ 7.4:1, OK. Sur fond `#f5f5f5` (body) → encore acceptable.
- **Pas de gestion du mode contraste élevé** (Windows High Contrast / `forced-colors`) : les couleurs de fond des états feedback pourraient disparaître, ne laissant que les bordures comme signal.

---

## 6. Points de friction identifiés (synthèse)

| # | Friction | Sévérité | Contexte |
|---|---|---|---|
| F1 | Bouton "Next" actif avant toute soumission → skip silencieux possible | ~~Haute~~ **Annulé** — comportement intentionnel : soumettre vide est un cas valide | PC + Mobile |
| F2 | Statistiques figées, non rafraîchies après chaque interaction | ~~Haute~~ **Annulé** — pas un besoin | PC + Mobile |
| F3 | Absence d'`aria-live` sur le feedback de résultat → inaccessible lecteur d'écran | Haute | Accessibilité |
| F4 | Soumission possible avec `wordToLearn` vide (race condition au chargement) | ~~Moyenne~~ **Annulé** — soumettre vide est intentionnel | PC + Mobile |
| F5 | Emojis seuls comme signal de résultat, non accessibles | Moyenne | Accessibilité |
| F6 | Contraste insuffisant du label "Real Translation" (#888 sur #f0f0f0) | Moyenne | Accessibilité |
| F7 | Bloc result-feedback visible et vide au démarrage | Faible | PC + Mobile |
| F8 | Pas d'animation de transition entre les mots | Faible | PC + Mobile |
| F9 | Spinner "Next" peu visible (faible contraste dark-on-light-grey) | Faible | PC + Mobile |
| F10 | Pas de raccourci clavier pour "Next" sans focus préalable | Faible | PC |
| F11 | `label for` invalide (pointe un `<span>`) | Faible | Sémantique/A11y |
| F12 | Absence de `<main>` landmark | Faible | Accessibilité |
| F13 | La couleur est le seul vecteur d'information pour les états (WCAG 1.4.1) | Moyenne | Accessibilité |
| F14 | Pas de gestion mode paysage mobile (clavier virtuel masque le feedback) | Moyenne | Mobile |
