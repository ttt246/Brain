import './styles.scss'
import { unmountComponentAtNode } from 'react-dom'
import { render } from 'preact'
import DecisionCard from '../components/DecisionCard'
import { config as siteConfig } from './site-adapters'
import { config as menuConfig } from './menu-tools'
import { getUserConfig, setAccessToken } from '../config/index.mjs'
import { createElementAtPosition, cropText, getPossibleElementByQuerySelector } from '../utils'
import FloatingToolbar from '../components/FloatingToolbar'
import Browser from 'webextension-polyfill'
import { getPreferredLanguage } from '../config/language.mjs'
import '../_locales/i18n-react'
import { initSession } from '../services/init-session.mjs'
import { registerPortListener } from '../services/wrappers.mjs'
import { generateAnswersWithCustomApi } from '../services/apis/rising-api.mjs'

/**
 * @param {SiteConfig} siteConfig
 * @param {UserConfig} userConfig
 */
async function mountComponent(siteConfig, userConfig) {
  const retry = 10
  let oldUrl = location.href
  for (let i = 1; i <= retry; i++) {
    if (location.href !== oldUrl) {
      console.log(`SiteAdapters Retry ${i}/${retry}: stop`)
      return
    }
    const e =
      (siteConfig &&
        (getPossibleElementByQuerySelector(siteConfig.sidebarContainerQuery) ||
          getPossibleElementByQuerySelector(siteConfig.appendContainerQuery) ||
          getPossibleElementByQuerySelector(siteConfig.resultsContainerQuery))) ||
      getPossibleElementByQuerySelector([userConfig.prependQuery]) ||
      getPossibleElementByQuerySelector([userConfig.appendQuery])
    if (e) {
      console.log(`SiteAdapters Retry ${i}/${retry}: found`)
      console.log(e)
      break
    } else {
      console.log(`SiteAdapters Retry ${i}/${retry}: not found`)
      if (i === retry) return
      else await new Promise((r) => setTimeout(r, 500))
    }
  }

  document.querySelectorAll('.chatgptbox-container,#chatgptbox-container').forEach((e) => {
    unmountComponentAtNode(e)
    e.remove()
  })

  let question
  if (userConfig.inputQuery) question = await getInput([userConfig.inputQuery])
  if (!question && siteConfig) question = await getInput(siteConfig.inputQuery)

  document.querySelectorAll('.chatgptbox-container,#chatgptbox-container').forEach((e) => {
    unmountComponentAtNode(e)
    e.remove()
  })
  const container = document.createElement('div')
  container.id = 'chatgptbox-container'
  render(
    <DecisionCard
      session={initSession()}
      question={question}
      siteConfig={siteConfig}
      container={container}
    />,
    container,
  )
}

/**
 * @param {string[]|function} inputQuery
 * @returns {Promise<string>}
 */
async function getInput(inputQuery) {
  let input
  if (typeof inputQuery === 'function') {
    input = await inputQuery()
    if (input) return `Reply in ${await getPreferredLanguage()}.\n` + input
    return input
  }
  const searchInput = getPossibleElementByQuerySelector(inputQuery)
  if (searchInput) {
    if (searchInput.value) input = searchInput.value
    else if (searchInput.textContent) input = searchInput.textContent
    if (input)
      return (
        `Reply in ${await getPreferredLanguage()}.\nThe following is a search input in a search engine, ` +
        `giving useful content or solutions and as much information as you can related to it, ` +
        `use markdown syntax to make your answer more readable, such as code blocks, bold, list:\n` +
        input
      )
  }
}

let menuX, menuY

async function prepareForRightClickMenu() {
  document.addEventListener('contextmenu', (e) => {
    menuX = e.clientX
    menuY = e.clientY
  })

  Browser.runtime.onMessage.addListener(async (message) => {
    if (message.type === 'CREATE_CHAT') {
      const data = message.data
      let prompt = ''
      if (data.itemId in menuConfig) {
        const menuItem = menuConfig[data.itemId]
        if (!menuItem.genPrompt) return
        else prompt = await menuItem.genPrompt()
        if (prompt) prompt = cropText(`Reply in ${await getPreferredLanguage()}.\n` + prompt)
      }

      const position = data.useMenuPosition
        ? { x: menuX, y: menuY }
        : { x: window.innerWidth / 2 - 300, y: window.innerHeight / 2 - 200 }
      const container = createElementAtPosition(position.x, position.y)
      container.className = 'chatgptbox-toolbar-container-not-queryable'
      render(
        <FloatingToolbar
          session={initSession({ modelName: (await getUserConfig()).modelName })}
          selection={data.selectionText}
          container={container}
          triggered={true}
          closeable={true}
          prompt={prompt}
        />,
        container,
      )
    }
  })
}

async function prepareForStaticCard() {
  const userConfig = await getUserConfig()
  let siteRegex
  if (userConfig.useSiteRegexOnly) siteRegex = userConfig.siteRegex
  else
    siteRegex = new RegExp(
      (userConfig.siteRegex && userConfig.siteRegex + '|') + Object.keys(siteConfig).join('|'),
    )

  const matches = location.hostname.match(siteRegex)
  if (matches) {
    const siteName = matches[0]

    if (
      userConfig.siteAdapters.includes(siteName) &&
      !userConfig.activeSiteAdapters.includes(siteName)
    )
      return

    if (siteName in siteConfig) {
      const siteAction = siteConfig[siteName].action
      if (siteAction && siteAction.init) {
        await siteAction.init(location.hostname, userConfig, getInput, mountComponent)
      }
    }

    mountComponent(siteConfig[siteName], userConfig)
  }
}

async function overwriteAccessToken() {
  if (location.hostname !== 'chat.openai.com') return

  let data
  if (location.pathname === '/api/auth/session') {
    const response = document.querySelector('pre').textContent
    try {
      data = JSON.parse(response)
    } catch (error) {
      console.error('json error', error)
    }
  } else {
    const resp = await fetch('https://chat.openai.com/api/auth/session')
    data = await resp.json().catch(() => ({}))
  }
  if (data && data.accessToken) {
    await setAccessToken(data.accessToken)
    console.log(data.accessToken)
  }
}

async function prepareForForegroundRequests() {
  const userConfig = await getUserConfig()
  registerPortListener(async (session, port) => {
    await generateAnswersWithCustomApi(port, session.question, session, '', userConfig.modelName)
  })
}

async function run() {
  await overwriteAccessToken()
  await prepareForForegroundRequests()

  prepareForStaticCard()
  prepareForRightClickMenu()
}

run()
