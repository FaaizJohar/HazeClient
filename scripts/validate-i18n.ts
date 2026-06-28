import { readFileSync, readdirSync, statSync } from 'fs'
import { join } from 'path'
import { baseCompile } from '@intlify/message-compiler'
import yaml from 'js-yaml'

function walkDir(dir: string, callback: (path: string) => void) {
  if (!statSync(dir).isDirectory()) return
  const files = readdirSync(dir)
  for (const file of files) {
    const fullPath = join(dir, file)
    if (statSync(fullPath).isDirectory()) {
      walkDir(fullPath, callback)
    } else if (fullPath.endsWith('.json') || fullPath.endsWith('.yaml') || fullPath.endsWith('.yml')) {
      callback(fullPath)
    }
  }
}

function validateFile(file: string) {
  let hasError = false
  const content = readFileSync(file, 'utf-8')
  let data: any
  try {
    if (file.endsWith('.yaml') || file.endsWith('.yml')) {
      data = yaml.load(content)
    } else {
      data = JSON.parse(content)
    }
  } catch (e) {
    console.error(`\x1b[31m[ERROR] Invalid file format: ${file}\x1b[0m`)
    return true
  }

  function traverse(obj: any, currentPath: string[]) {
    for (const key in obj) {
      const value = obj[key]
      const path = [...currentPath, key]
      if (typeof value === 'string') {
        try {
          baseCompile(value, {
            onError: (err) => {
              throw err
            }
          })
        } catch (e: any) {
          console.error(`\x1b[31m[ERROR] i18n compile error in ${file}\x1b[0m`)
          console.error(`        Path: ${path.join('.')}`)
          console.error(`        String: "${value}"`)
          console.error(`        Error: ${e.message}`)
          hasError = true
        }
      } else if (typeof value === 'object' && value !== null) {
        traverse(value, path)
      }
    }
  }

  traverse(data, [])
  return hasError
}

let hasError = false
const dirs = [
  join(process.cwd(), 'i18n'),
  join(process.cwd(), 'haze-keystone-ui/locales'),
  join(process.cwd(), 'haze-electron-app/main/locales')
]

for (const dir of dirs) {
  try {
    walkDir(dir, (file) => {
      if (validateFile(file)) {
        hasError = true
      }
    })
  } catch (e) {
    // directory might not exist, ignore or log
  }
}

if (hasError) {
  process.exit(1)
} else {
  console.log('\x1b[32m[SUCCESS] All i18n locale files passed validation.\x1b[0m')
}
