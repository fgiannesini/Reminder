// Copies the BeerCSS files we serve from node_modules to static/vendor.
// Run `npm run vendor` after bumping beercss in package.json, then commit the result.
import {copyFileSync, mkdirSync} from 'node:fs';
import {dirname, join} from 'node:path';
import {fileURLToPath} from 'node:url';

const root = join(dirname(fileURLToPath(import.meta.url)), '..');
const source = join(root, 'node_modules', 'beercss', 'dist', 'cdn');
const target = join(root, 'src', 'main', 'resources', 'static', 'vendor');
const files = ['beer.min.css'];

mkdirSync(target, {recursive: true});
for (const file of files) {
    copyFileSync(join(source, file), join(target, file));
    console.log(`vendored ${file}`);
}
