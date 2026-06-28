import { Client, type SetActivity } from '@haze/discord-rpc'
import { type PresenceService as IPresenceService, type SharedState, PresenceServiceKey, Settings } from '@haze/runtime-api'
import { Inject, LauncherAppKey } from '~/app'
import { AbstractService, ExposeServiceKey } from '~/service'
import { kSettings } from '~/settings'
import { LauncherApp } from '../app/LauncherApp'
import { LaunchService } from '../launch/LaunchService'

@ExposeServiceKey(PresenceServiceKey)
export class PresenceService extends AbstractService implements IPresenceService {
  private discord: Client
  private current: SetActivity = {
  }
  private runningGameCount = 0

  constructor(@Inject(LauncherAppKey) app: LauncherApp,
    @Inject(kSettings) private settings: SharedState<Settings>,
    @Inject(LaunchService) launchService: LaunchService,
  ) {
    super(app, async () => {
      if (settings.discordPresence) {
        try {
          await this.discord.connect()
        } catch (e) {
          // Ignore
        }
      }
    })

    // Initialize Discord client first before setting up event listeners
    this.discord = new Client({
      clientId: '1122334455667788990', // Haze Client App ID
    })

    settings.subscribe('discordPresenceSet', async (state) => {
      if (state) {
        await this.discord.connect().catch((e: any) => {
          this.warn('Fail to connect to discord. %o', e)
        })
      } else {
        await this.discord.destroy()
      }
    })

    // Listen to game launch events to disable presence when game is running
    launchService.on('minecraft-start', () => {
      this.runningGameCount++
      this.log(`Game started, updating Discord presence (running games: ${this.runningGameCount})`)
      // Set the current activity to In-Game when the first game starts
      if (this.runningGameCount === 1 && this.discord.isConnected) {
        this.current.details = 'Playing Haze Client (1.20.1)'
        this.current.state = 'In-Game'
        this.current.largeImageKey = 'haze_logo'
        this.discord.user?.setActivity(this.current).catch((e: any) => {
          this.warn('Fail to set discord presence. %o', e)
        })
      }
    })

    launchService.on('minecraft-exit', () => {
      this.runningGameCount = Math.max(0, this.runningGameCount - 1)
      this.log(`Game exited, re-enabling Discord presence updates if no games running (running games: ${this.runningGameCount})`)
    })

    // TODO: finish this
    // .subscribe('connectionGroup', (id) => {
    //   if (this.discord.isConnected) {
    //     this.current.state = id ? 'Group ' + id : ''
    //     this.current.partyId = id || undefined
    //     if (id) {
    //       this.current.partyMax = 20
    //       this.current.partySize = 1
    //     } else {
    //       this.current.partyMax = undefined
    //       this.current.partySize = undefined
    //     }
    //     this.current.joinSecret = id ? id + 'secret' : undefined
    //     this.discord.user?.setActivity(this.current).catch((e) => {
    //       this.warn('Fail to set discord presence. %o', e)
    //     })
    //   }
    // }).subscribe('connectionAdd', () => {
    //   this.current.partySize = (this.current.partySize || 1) + 1
    //   this.discord.user?.setActivity(this.current).catch((e) => {
    //     this.warn('Fail to set discord presence. %o', e)
    //   })
    // }).subscribe('connectionDrop', () => {
    //   this.current.partySize = (this.current.partySize || 1) - 1
    //   this.discord.user?.setActivity(this.current).catch((e) => {
    //     this.warn('Fail to set discord presence. %o', e)
    //   })
    // })
  }

  async setActivity(activity: string): Promise<void> {
    if (!this.settings.discordPresence) {
      return
    }
    // Don't update Discord presence if any game is running
    if (this.runningGameCount > 0) {
      this.log('Game is running, skipping Discord launcher presence update')
      return
    }
    if (!this.discord.isConnected) {
      try {
        await this.discord.connect()
      } catch (e) {
        return
      }
    }
    const param = this.current
    this.current.largeImageKey = 'haze_logo'
    this.current.startTimestamp = Date.now()
    this.current.details = 'Configuring Modules'
    this.current.state = activity // Show the current launcher view
    await this.discord.user?.setActivity(param).catch((e: any) => {
      this.warn('Fail to set discord presence. %o', e)
    })
  }
}
