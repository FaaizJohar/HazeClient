import fs from 'fs/promises'
import path from 'path'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const ROOT = path.join(__dirname, '..')

const dirsToScan = [
  path.join(ROOT, 'haze-keystone-ui', 'locales'),
  path.join(ROOT, 'haze-electron-app', 'main', 'locales'),
  ROOT, // for docs
]

const extAllowed = ['.yaml', '.yml', '.json', '.md']

async function scan(dir) {
  let results = []
  try {
    const list = await fs.readdir(dir, { withFileTypes: true })
    for (const dirent of list) {
      if (dirent.name === 'node_modules' || dirent.name === '.git' || dirent.name === 'dist' || dirent.name === 'build' || dirent.name.startsWith('.')) continue
      
      const res = path.resolve(dir, dirent.name)
      if (dirent.isDirectory()) {
        if (dir === ROOT) continue // only scan root shallowly for md files
        results = results.concat(await scan(res))
      } else {
        if (extAllowed.includes(path.extname(res))) {
          results.push(res)
        }
      }
    }
  } catch (e) {
    // skip
  }
  return results
}

async function run() {
  let files = []
  for (const d of dirsToScan) {
    files = files.concat(await scan(d))
  }
  
  // also add some specific files
  files.push(path.join(ROOT, 'README.md'))
  files.push(path.join(ROOT, 'CONTRIBUTING.md'))
  files.push(path.join(ROOT, 'AGENTS.md'))

  // unique
  files = [...new Set(files)]

  let changedCount = 0
  for (const file of files) {
    if (!file.endsWith('.md') && !file.includes('locales')) continue // safety

    try {
      const content = await fs.readFile(file, 'utf8')
      let newContent = content
        .replace(/Haze Client/g, 'Haze Client')
        .replace(/\bXMCL\b/g, 'Haze Client')
        
      if (content !== newContent) {
        await fs.writeFile(file, newContent, 'utf8')
        changedCount++
        console.log(`Updated ${path.relative(ROOT, file)}`)
      }
    } catch (e) {
      console.error(`Failed ${file}: ${e.message}`)
    }
  }
  console.log(`Updated ${changedCount} files.`)
}

run()
