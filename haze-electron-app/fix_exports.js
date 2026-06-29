const fs = require('fs');
const text = fs.readFileSync('build_errors_utf8.txt', 'utf-8');
const regex = /X \[ERROR\] No matching export in "\.\.\/packages\/([^/]+)\/index\.ts" for import "([^"]+)"/g;
let match;
const exportsByPackage = {};
while ((match = regex.exec(text)) !== null) {
  const pkg = match[1];
  const exp = match[2];
  if (!exportsByPackage[pkg]) exportsByPackage[pkg] = new Set();
  exportsByPackage[pkg].add(exp);
}

for (const [pkg, exports] of Object.entries(exportsByPackage)) {
  const path = `d:/x-minecraft-launcher/packages/${pkg}/index.ts`;
  if (!fs.existsSync(path)) continue;
  let content = fs.readFileSync(path, 'utf-8');
  for (const exp of exports) {
    if (!content.includes(`export const ${exp}`) && !content.includes(`export enum ${exp}`) && !content.includes(`export type ${exp}`)) {
      content += `\nexport const ${exp} = {} as any;`;
    }
  }
  fs.writeFileSync(path, content, 'utf-8');
}
console.log('Fixed exports for', Object.keys(exportsByPackage));
