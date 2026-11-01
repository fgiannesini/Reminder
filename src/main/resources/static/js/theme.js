// BeerCSS switches themes with a `light` / `dark` class on <body>; we follow the OS preference.
export const applySystemTheme = (doc = document, win = window) => {
    const query = win.matchMedia('(prefers-color-scheme: dark)');
    const apply = () => {
        doc.body.classList.toggle('dark', query.matches);
        doc.body.classList.toggle('light', !query.matches);
    };
    apply();
    query.addEventListener('change', apply);
};
