const fs = require('fs/promises');
const path = require('path');

const ROOT = path.join(__dirname, '..');

const excludeDirs = ['node_modules', '.git', 'dist', 'build', 'out', 'artifacts', '.vscode', '.idea'];
const extensions = ['.ts', '.js', '.mjs', '.vue', '.json', '.md', '.java', '.yaml', '.yml', '.html', '.css'];

async function walk(dir, fileList = []) {
    const files = await fs.readdir(dir, { withFileTypes: true });
    for (const file of files) {
        if (excludeDirs.includes(file.name) || file.name.startsWith('.')) continue;
        
        const filepath = path.join(dir, file.name);
        if (file.isDirectory()) {
            await walk(filepath, fileList);
        } else {
            const ext = path.extname(file.name);
            if (extensions.includes(ext) || file.name === 'package.json') {
                fileList.push(filepath);
            }
        }
    }
    return fileList;
}

async function rebrand() {
    console.log('Starting global rebrand...');
    const files = await walk(ROOT);
    let changed = 0;
    
    for (const file of files) {
        try {
            const content = await fs.readFile(file, 'utf8');
            let newContent = content
                .replace(/Haze Client/gi, 'Haze Client')
                .replace(/haze-client/gi, 'haze-client')
                .replace(/\bxmcl\b/gi, 'haze')
                .replace(/\bxcml\b/gi, 'haze');
                
            if (content !== newContent) {
                await fs.writeFile(file, newContent, 'utf8');
                changed++;
                console.log(`Rebranded: ${path.relative(ROOT, file)}`);
            }
        } catch (e) {
            console.error(`Error processing ${file}: ${e.message}`);
        }
    }
    console.log(`\nRebrand complete! Modified ${changed} files.`);
}

rebrand().catch(console.error);
