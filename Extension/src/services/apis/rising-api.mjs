// custom api version

// There is a lot of duplicated code here, but it is very easy to refactor.
// The current state is mainly convenient for making targeted changes at any time,
// and it has not yet had a negative impact on maintenance.
// If necessary, I will refactor.

import { v4 as uuidv4 } from 'uuid'
import { getUserConfig, Models } from '../../config/index.mjs'
import { fetchForChat, fetchForBrowserMng } from '../../utils/fetch-sse.mjs'
import { getConversationPairs } from '../../utils/index.mjs'
import { isEmpty } from 'lodash-es'
import { pushRecord, setAbortController } from './shared.mjs'

/**
 * @param {Browser.Runtime.Port} port
 * @param {string} question
 * @param {Session} session
 * @param {string} apiKey
 * @param {string} modelName
 */
export async function generateAnswersWithCustomApi(port, question, session, apiKey, modelName) {
  const model = Models[modelName].value
  const uuid = uuidv4()

  const { controller, messageListener } = setAbortController(port)

  const config = await getUserConfig()
  const prompt = getConversationPairs(
    session.conversationRecords.slice(-config.maxConversationContextLength),
    false,
  )
  const apiUrl = config.customModelApiUrl

  let answer = ''
  await fetchForChat(apiUrl, {
    method: 'POST',
    signal: controller.signal,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${apiKey}`,
    },
    body: JSON.stringify({
      token: 'test_token', // it is token generated in firebase for push notification. it can be ignored
      uuid: uuid, // it is uuid for mobile devices or web users as well. please put it on "extension" for now.
      history: prompt,
      model: model,
      user_input: question,
    }),
    onMessage(message) {
      console.debug('sse message', message)
      if (message === '[DONE]') {
        pushRecord(session, question, answer)
        console.debug('conversation history', { content: session.conversationRecords })
        port.postMessage({ answer: null, done: true, session: session })
        return
      }
      answer = message
      port.postMessage({ answer: answer, done: false, session: null })
    },
    async onStart() {},
    async onEnd() {
      port.onMessage.removeListener(messageListener)
    },
    async onError(resp) {
      port.onMessage.removeListener(messageListener)
      if (resp instanceof Error) throw resp
      const error = await resp.json().catch(() => ({}))
      throw new Error(!isEmpty(error) ? JSON.stringify(error) : `${resp.status} ${resp.statusText}`)
    },
  })
}

/**
 * @param {Browser.Runtime.Port} port
 * @param {string} question
 * @param {Session} session
 * @param {string} apiKey
 */
export async function getManagementBrowser(port, question, session, apiKey) {
  const uuid = uuidv4()

  const { controller, messageListener } = setAbortController(port)

  const config = await getUserConfig()
  const apiUrl = config.customModelApiUrl

  let answer = ''
  await fetchForBrowserMng(apiUrl, {
    method: 'POST',
    signal: controller.signal,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${apiKey}`,
    },
    body: JSON.stringify({
      token: 'test_token', // it is token generated in firebase for push notification. it can be ignored
      uuid: uuid, // it is uuid for mobile devices or web users as well. please put it on "extension" for now.
      message: question,
    }),
    onMessage(message) {
      console.debug('sse message', message)
      if (message === '[DONE]') {
        pushRecord(session, question, answer)
        console.debug('conversation history', { content: session.conversationRecords })
        port.postMessage({ answer: null, done: true, session: session })
        return
      }
      answer = message
      port.postMessage({ answer: answer, done: false, session: null })
    },
    async onStart() {},
    async onEnd() {
      port.onMessage.removeListener(messageListener)
    },
    async onError(resp) {
      port.onMessage.removeListener(messageListener)
      if (resp instanceof Error) throw resp
      const error = await resp.json().catch(() => ({}))
      throw new Error(!isEmpty(error) ? JSON.stringify(error) : `${resp.status} ${resp.statusText}`)
    },
  })
}
