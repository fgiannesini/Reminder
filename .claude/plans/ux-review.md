# Revue UX/UI — index.html

## Contexte

Fichier unique : `src/main/resources/static/index.html` (411 lignes, HTML + CSS + JS inline).
SPA vanilla JS. Flux : afficher mot → utilisateur tape traduction → soumettre → feedback → suivant.

Décisions de design à NE PAS remettre en cause :
- Soumettre vide est intentionnel
- Stats figées (pas temps réel) n'est pas un bug
- Compatibilité lecteur d'écran non requise

---

## Findings priorisés

### Critical

1. **L.286 — `#error-span` n'est jamais ré-effacé.** Une fois affiché, il reste visible même après une requête suivante réussie. L'utilisateur voit une erreur obsolète. Fix : créer une fonction `clearError()` qui cache le span + vide son textContent, appelée au début de `getNextWord`, `submitTranslation`, `setRemainingWordsCount`.

2. **L.394-404 — Handler Enter global contient du code mort.** Après une réponse, le focus est déjà mis programmatiquement sur Next (l.383), donc l'Enter natif du bouton se déclenche — le handler global ne sert jamais sur cette branche. La condition `if (active === document.getElementById("next-button")) return` est inutile. Fix : simplifier — ignorer si focus dans input, sinon déclencher Next dès que feedback est visible et bouton non disabled.

### Major

3. **L.378 — Le feedback mélange emoji + couleur + symbole CSS.** L'emoji `✅/❌/👌` est redondant avec la couleur du fond ET avec le pseudo-élément `::before` (`✓/✗/–`) déjà présent l.220/229/238. Triple redondance visuelle, charge cognitive inutile. Fix : retirer `matchingSmiley` de la concaténation ; conserver uniquement le pseudo-élément CSS.

4. **L.378 — Le mot "learned !" est concaténé en string libre, pas stylé, pas mis en valeur.** C'est pourtant l'événement majeur de l'app (objectif atteint). Fix : extraire dans un `<span id="learned-badge">` séparé dans le bloc feedback ; styler en badge (background vert foncé, texte blanc, padding, border-radius) ; piloter visibilité depuis `submitTranslation`.

5. **L.276 — Bouton Submit n'a pas d'`id`, sélectionné via `.input-block button` (l.331).** Fragile au refactor. Fix : ajouter `id="submit-button"`.

6. **L.274 — Input sans `type` ni anti-autocorrection.** Pour une app d'apprentissage de langue, l'autocorrection mobile peut transformer la saisie et fausser le test. Fix : ajouter `type="text" autocapitalize="off" autocorrect="off" spellcheck="false"`.

7. **L.299-300 — Pluralisation absente.** `${stats.countToLearn} words to learn.` produit "1 words to learn" au singulier. Fix : `count === 1 ? 'word' : 'words'`.

8. **L.379 — `feedback.scrollIntoView` appelé systématiquement,** même quand le bloc est déjà visible. Provoque un scroll involontaire sur desktop. Fix : vérifier `getBoundingClientRect()` ; ne scroller que si partiellement hors viewport.

9. **L.326 — `wordSpan.style.opacity = '0'` est appliqué AVANT le fetch (l.307).** Pendant tout le chargement réseau, l'utilisateur voit un blanc à la place du mot. Fix : ne masquer qu'après réception de la réponse (juste avant d'écrire le nouveau textContent), ou afficher le mot précédent jusqu'au remplacement.

10. **L.193 vs L.278 — Layout shift à l'apparition du feedback.** `min-height: 3.2rem` sur `#result-feedback` mais le bloc est `display: none` initialement → ~50px qui apparaissent d'un coup. Fix : remplacer `display:none` par `visibility:hidden` (l.278, 320, 369) pour réserver l'espace.

### Minor

11. **L.275, 283 — `onclick` inline.** Mélange structure et comportement. Fix : `addEventListener` dans le script.

12. **L.73, 204 — Contraste `#888` sur `#fff` = ~3.5:1,** sous le seuil WCAG AA (4.5:1). Texte des labels difficile à lire. Fix : passer à `#595959` ou plus foncé.

13. **L.378 — Apostrophes simples autour de `realTranslation` : `'${check.realTranslation}'`.** Si la traduction contient une apostrophe ("l'eau"), rendu cassé visuellement. Fix : utiliser des guillemets typographiques `«»` ou retirer les quotes (le contraste visuel suffit).

14. **L.123-126 + L.160 — Spinner Submit invisible quand bouton disabled.** `button:disabled` applique `opacity: 0.45`, le spinner blanc à 35% d'opacité devient quasi invisible sur fond bleu à 45%. Fix : ne pas appliquer `disabled` quand `loading` (utiliser `pointer-events:none` + classe loading), OU monter l'opacité disabled à 0.65.

15. **L.94 — Input height 2.5rem (~40px),** sous le minimum de 44px pour tap target mobile. Fix : `height: 2.75rem`.

16. **L.110 — Boutons sans `min-height: 44px`,** hauteur tactile insuffisante. Fix : ajouter `min-height: 2.75rem` sur la règle `button`.

17. **L.139 — `#next-button` background `#e8e8e8` quasi identique au body `#f5f5f5`.** Le bouton se fond dans le fond. Fix : ajouter `border: 1px solid #c0c0c0` ou foncer le background à `#d8d8d8`.

18. **L.210 — `#real-translation-span` `font-size: 1.05rem`,** à peine plus grand que le label (0.75rem). Hiérarchie typographique faible pour l'info la plus importante. Fix : `font-size: 1.25rem; font-weight: 600`.

19. **L.286 — `#error-span` en bas du container.** Sur petit écran, une erreur peut être hors viewport au chargement. Fix : déplacer juste après `<h1>` (l.264).

20. **L.300 — "words to confirm"** : terme métier obscur pour l'utilisateur. Fix : reformuler ("words being mastered") ou ajouter `title="..."`.

21. **L.327 — `translation.focus()` après Next.** Sur mobile, ça ouvre le clavier virtuel à chaque clic, agressif si l'utilisateur veut lire le mot. Fix : gate derrière `matchMedia('(hover: hover)').matches` (focus desktop uniquement).

22. **L.265-268 — `.stats-block` vide au chargement,** apparaît d'un coup quand le fetch revient. Layout shift. Fix : placeholder `"…"` initial dans les deux divs.

23. **L.298, 318, 355 — `JSON.parse(await response.text())`** au lieu de `await response.json()`. Verbeux sans bénéfice. Fix : utiliser `.json()`.

24. **L.293-296, 309-316, 346-353 — Trois blocs `if (!response.ok)` quasi-identiques.** Duplication. Fix : extraire fonction `showError(text)`.

25. **L.155-157 — `@keyframes btn-spin`** ne respecte pas `prefers-reduced-motion`. Fix : règle globale `@media (prefers-reduced-motion: reduce) { *, *::before, *::after { animation-duration: 0.01ms !important; transition-duration: 0.01ms !important; } }`.

26. **L.3-6 — Aucun favicon déclaré.** Onglet navigateur générique, app non identifiable dans une liste d'onglets/bookmarks. Génère aussi un 404 systématique sur `/favicon.ico` côté backend. Fix : ajouter un favicon (SVG inline pour rester en single-file, ou fichier `.ico`/`.svg` dans `static/`) et déclarer `<link rel="icon">` dans le `<head>`.

---

## Tâches atomiques (ordre = priorité décroissante)

1. [x] **Effacer l'erreur avant chaque requête** — créer fonction `clearError()` qui cache `#error-span` (display=none) et vide textContent ; l'appeler en début de `setRemainingWordsCount`, `getNextWord`, `submitTranslation`.

2. [x] **Désactiver l'autocorrection sur l'input** — ajouter `type="text" autocapitalize="off" autocorrect="off" spellcheck="false"` sur `#translation-input` (l.274).

3. [x] **Supprimer la redondance emoji + symbole CSS** — retirer la variable `matchingSmiley` et son switch (l.356-367) ; retirer `${matchingSmiley}` de la concaténation l.378.

4. [x] **Mettre en valeur l'état "learned"** — ajouter `<span id="learned-badge"></span>` dans `#result-feedback` (après `#real-translation-span`) ; styler en badge vert foncé (background `#2e7d32`, color blanc, padding `0.2rem 0.6rem`, border-radius `999px`, font-size `0.8rem`, font-weight 600) ; masquer par défaut (`display:none`) ; afficher dans `submitTranslation` quand `check.learned` est true, masquer sinon ; retirer la concaténation `learned` du textContent l.378.

5. [x] **Donner un id au bouton Submit** — `id="submit-button"` sur l.275 ; remplacer le sélecteur `.input-block button` par `getElementById('submit-button')` (l.331).

6. [x] **Pluralisation des stats** — dans `setRemainingWordsCount` (l.299-300), gérer `count === 1 ? 'word' : 'words'` ; appliquer aux deux compteurs.

7. [x] **Conditionner scrollIntoView** — vérifier `getBoundingClientRect().bottom <= window.innerHeight` avant d'appeler `scrollIntoView` (l.379) ; ne scroller que si le bloc dépasse.

8. [x] **Réduire le flash du mot pendant le chargement** — déplacer `wordSpan.style.opacity = '0'` (l.307) APRÈS le fetch, juste avant d'écrire `wordSpan.textContent` (l.318).

9. [x] **Réserver l'espace du feedback** — remplacer `style="display: none"` (l.278) par `style="visibility: hidden"` ; adapter `feedback.style.display = ''` en `feedback.style.visibility = 'visible'` (l.369) et `display='none'` en `visibility='hidden'` (l.320).

10. [x] **Corriger contraste `#888`** — remplacer `color: #888` (l.73) par `color: #595959` ; vérifier également `color: #666` (l.204) — OK à 5.7:1, ne pas changer.

11. [x] **Augmenter hauteur tactile** — `#translation-input` height `2.75rem` (l.94) ; ajouter `min-height: 2.75rem` sur la règle `button` (l.109).

12. [x] **Améliorer contraste bouton Next** — ajouter `border: 1px solid #c0c0c0` à `#next-button` (l.139).

13. [x] **Améliorer hiérarchie typo du feedback** — `#real-translation-span` (l.208-211) : `font-size: 1.25rem; font-weight: 600`.

14. [x] **Échapper les apostrophes dans la traduction affichée** — remplacer `'${check.realTranslation}'` (l.378) par `«${check.realTranslation}»`.

15. [x] **Fix spinner Submit invisible** — retirer `submitBtn.disabled = true` (l.334) ; ajouter `pointer-events: none` à `button.loading` dans le CSS (l.160) pour bloquer les clics sans déclencher `:disabled`. Garder `inputEl.disabled = true` pour bloquer la saisie.

16. [x] **Simplifier les `JSON.parse(await response.text())`** — remplacer par `await response.json()` aux lignes 298, 318, 355.

17. [x] **Factoriser le handling d'erreur** — extraire fonction `showError(text)` qui fait `el.style.display='block'; el.textContent=text` ; appeler à la place des trois blocs dupliqués (l.293-296, 309-316, 346-353).

18. [x] **Remplacer onclick inline par addEventListener** — retirer `onclick="submitTranslation()"` (l.275) et `onclick="getNextWord()"` (l.283) ; câbler via `getElementById('submit-button').addEventListener('click', submitTranslation)` et idem pour Next dans le bloc script.

19. [x] **Nettoyer le handler Enter global** — l.394-404 : supprimer la condition `if (active === document.getElementById("next-button")) return` ; conserver uniquement le retour si focus dans input ; déclencher Next dès que feedback visible et bouton non disabled.

20. [x] **Déplacer `#error-span` en haut du container** — déplacer le `<span id="error-span">` juste après `<h1>Reminder</h1>` (l.264) plutôt qu'à la fin (l.286).

21. [ ] **Clarifier "to confirm"** — remplacer le texte par "words being mastered" dans `setRemainingWordsCount` (l.300).

22. [ ] **Auto-focus desktop only** — entourer `translation.focus()` (l.327) par `if (matchMedia('(hover: hover)').matches) { ... }`.

23. [ ] **Placeholders stats au chargement** — initialiser `textContent = '…'` sur les deux divs stats au début de `setRemainingWordsCount` (avant le fetch).

24. [ ] **Respecter prefers-reduced-motion** — ajouter dans le CSS (après les autres règles globales `*`) : `@media (prefers-reduced-motion: reduce) { *, *::before, *::after { animation-duration: 0.01ms !important; transition-duration: 0.01ms !important; } }`.

25. [ ] **Ajouter un favicon** — créer `src/main/resources/static/favicon.svg` (icône simple, ex. un livre ou la lettre "R" sur fond coloré) ; ajouter `<link rel="icon" type="image/svg+xml" href="/favicon.svg">` dans le `<head>` (après la balise `<title>`, l.6). Vérifier que Spring sert bien le fichier statique et qu'aucun 404 n'apparaît dans la console.

---

## Vérification par tâche

Pas de tests automatisés côté front (vanilla JS, aucune infra de test JS). Vérification = checklist manuelle.

Après chaque tâche :
1. `./gradlew compileJava compileTestJava` — sanity check backend non cassé.
2. `./gradlew test` — vérifier que les tests backend passent toujours.
3. Lancer l'app, ouvrir Chrome desktop → vérifier visuellement le comportement modifié.
4. DevTools → device toolbar → tester mobile 375x667 + landscape 667x375.
5. DevTools → Rendering → simuler `prefers-reduced-motion: reduce` (tâche 24).

Smoke test complet après toutes les tâches :
- Charger la page → mot affiché, stats affichées (pluriel correct), pas d'erreur, pas de layout shift visible.
- Taper bonne réponse → feedback vert (symbole ✓ via CSS, pas d'emoji), badge "Learned" stylé si applicable, traduction réelle dans guillemets typographiques.
- Cliquer Next → nouveau mot sans flash blanc, input vidé, focus revient à l'input (desktop) ou pas (mobile).
- Taper mauvaise réponse → feedback rouge.
- Couper le backend → erreur visible en haut de page.
- Relancer backend, cliquer Next → erreur disparaît automatiquement.
- Cliquer Submit → spinner visible pendant le loading.
- Activer reduced-motion → pas d'animation de rotation, pas de transition.
