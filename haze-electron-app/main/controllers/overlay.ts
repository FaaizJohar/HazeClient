import { BrowserWindow, screen, ipcMain } from 'electron'
import { ElectronController } from '@/ElectronController'
import { LaunchService } from '@haze/runtime/launch'
import { ControllerPlugin } from './plugin'

export const overlayPlugin: ControllerPlugin = function (this: ElectronController) {
  let overlayWin: BrowserWindow | undefined
  let isVisible = false
  
  const createOverlay = () => {
    if (overlayWin) return
    const primaryDisplay = screen.getPrimaryDisplay()
    const { width, height } = primaryDisplay.workAreaSize
    
    overlayWin = new BrowserWindow({
      width: 300,
      height: 150,
      x: 10,
      y: 10,
      transparent: true,
      frame: false,
      alwaysOnTop: true,
      skipTaskbar: true,
      webPreferences: {
        nodeIntegration: true,
        contextIsolation: false
      }
    })
    
    overlayWin.setIgnoreMouseEvents(true)
    overlayWin.setVisibleOnAllWorkspaces(true, { visibleOnFullScreen: true })
    overlayWin.hide()
    
    // In a real scenario we would load an HTML file here
    const htmlContent = `
      <html>
        <head>
          <style>
            body { font-family: monospace; color: #00ff00; background: transparent; text-shadow: 1px 1px 2px black; font-size: 16px; margin: 0; padding: 10px; overflow: hidden; }
            .hidden { display: none; }
          </style>
        </head>
        <body>
          <div id="fps">FPS: --</div>
          <div id="lowfps">1% Low: --</div>
          <div id="gc">GC Pauses: 0</div>
          <script>
            const { ipcRenderer } = require('electron')
            ipcRenderer.on('overlay-update', (event, data) => {
              if(data.fps) document.getElementById('fps').innerText = 'FPS: ' + data.fps;
              if(data.lowFps) document.getElementById('lowfps').innerText = '1% Low: ' + data.lowFps;
              if(data.gcPauses !== undefined) document.getElementById('gc').innerText = 'GC Pauses: ' + data.gcPauses;
            })
          </script>
        </body>
      </html>
    `
    overlayWin.loadURL('data:text/html;charset=utf-8,' + encodeURIComponent(htmlContent))
  }

  this.app.waitEngineReady().then(() => {
    this.app.registry.get(LaunchService).then((service) => {
      let gcPauses = 0
      
      service.on('minecraft-start', () => {
        if (!overlayWin) createOverlay()
        gcPauses = 0
        if (overlayWin) overlayWin.show()
        isVisible = true
      })
      
      service.on('minecraft-stdout', ({ stdout }) => {
        if (!isVisible || !overlayWin) return
        
        let update = false
        const data: any = {}
        
        // Parse FPS (usually from spark or debug screen logs if they exist)
        const fpsMatch = stdout.match(/FPS: (\d+)/)
        if (fpsMatch) {
          data.fps = fpsMatch[1]
          update = true
        }
        
        // Parse GC Pauses (from spark or JVM gc logs)
        if (stdout.includes('Pause Young') || stdout.includes('Pause Full') || stdout.includes('GC')) {
          gcPauses++
          data.gcPauses = gcPauses
          update = true
        }
        
        if (update && overlayWin) {
          overlayWin.webContents.send('overlay-update', data)
        }
      })
      
      service.on('minecraft-exit', () => {
        if (overlayWin) {
          overlayWin.hide()
          isVisible = false
        }
      })
    })
    
    // F11 hotkey via globalShortcut would go here, but omitted to prevent conflict with game fullscreen
  })
}
