export function createOfferLauncherUrl(description: string) {
  return `haze://peer/offer/${description}`
}

export function createAnswerLauncherUrl(description: string) {
  return `haze://peer/answer/${description}`
}

export function createOfferAppUrl(description: string, inviter: string) {
  return `https://haze.app/peer?description=${description}?type=offer?inviter=${inviter}`
}

export function createAnswerAppUrl(description: string, inviter: string) {
  return `https://haze.app/peer?description=${description}?type=answer?inviter=${inviter}`
}
