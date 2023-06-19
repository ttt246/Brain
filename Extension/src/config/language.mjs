import { defaultConfig, getUserConfig } from './index.mjs'

export const languageList = {
  auto: { name: 'Auto', native: 'Auto' },
  en: { name: 'en-US', native: 'en-US' },
}

export async function getUserLanguage() {
  return languageList[defaultConfig.userLanguage].name
}

export async function getUserLanguageNative() {
  return languageList[defaultConfig.userLanguage].native
}

export async function getPreferredLanguage() {
  const config = await getUserConfig()
  if (config.preferredLanguage === 'auto') return await getUserLanguage()
  return languageList[config.preferredLanguage].name
}

export async function getPreferredLanguageNative() {
  const config = await getUserConfig()
  if (config.preferredLanguage === 'auto') return await getUserLanguageNative()
  return languageList[config.preferredLanguage].native
}
