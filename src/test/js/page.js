import {readFileSync} from 'node:fs';
import {resolve} from 'node:path';

// Vitest runs from the project root (where package.json lives).
const indexHtml = resolve(process.cwd(), 'src/main/resources/static/index.html');

// Loads the real index.html markup (without its <script>) into the current document.
export const loadPage = () => {
    const html = readFileSync(indexHtml, 'utf8');
    const body = html.match(/<body[^>]*>([\s\S]*)<\/body>/)[1].replace(/<script[\s\S]*?<\/script>/g, '');
    document.body.className = '';
    document.body.innerHTML = body;
};
