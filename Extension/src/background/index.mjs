import Browser from 'webextension-polyfill'
import { getUserConfig, setUserConfig } from '../config/index.mjs'
import '../_locales/i18n'
import { openUrl } from '../utils/open-url'
import { registerPortListener } from '../services/wrappers.mjs'
import { refreshMenu } from './menus.mjs'
import { registerCommands } from './commands.mjs'
import { getManagementBrowser } from '../services/apis/rising-api.mjs'

async function executeApi(session, port, config) {
  // ### the part for integrating with backend ###
  console.debug('config', config)
  await getManagementBrowser(port, session.question, session, '')
}

Browser.runtime.onMessage.addListener(async (message, sender) => {
  switch (message.type) {
    case 'NEW_URL': {
      const newTab = await Browser.tabs.create({
        url: message.data.url,
        pinned: message.data.pinned,
      })
      if (message.data.saveAsChatgptConfig) {
        await setUserConfig({
          chatgptTabId: newTab.id,
          chatgptJumpBackTabId: sender.tab.id,
        })
      }
      break
    }
    case 'SET_CHATGPT_TAB': {
      await setUserConfig({
        chatgptTabId: sender.tab.id,
      })
      break
    }
    case 'ACTIVATE_URL':
      await Browser.tabs.update(message.data.tabId, { active: true })
      break
    case 'OPEN_URL':
      openUrl(message.data.url)
      break
    case 'OPEN_CHAT_WINDOW': {
      const config = await getUserConfig()
      const url = Browser.runtime.getURL('IndependentPanel.html')
      const tabs = await Browser.tabs.query({ url: url, windowType: 'popup' })
      if (!config.alwaysCreateNewConversationWindow && tabs.length > 0)
        await Browser.windows.update(tabs[0].windowId, { focused: true })
      else
        await Browser.windows.create({
          url: url,
          type: 'popup',
          width: 500,
          height: 650,
        })
      break
    }
    case 'REFRESH_MENU':
      refreshMenu()
      break
    case 'PIN_TAB': {
      let tabId
      if (message.data.tabId) tabId = message.data.tabId
      else tabId = sender.tab.id

      await Browser.tabs.update(tabId, { pinned: true })
      if (message.data.saveAsChatgptConfig) {
        await setUserConfig({ chatgptTabId: tabId })
      }
      break
    }
  }
})

registerPortListener(async (session, port, config) => await executeApi(session, port, config))
registerCommands()
refreshMenu()
