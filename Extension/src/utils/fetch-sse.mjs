import { createParser } from 'eventsource-parser'
import { streamAsyncIterable } from './stream-async-iterable'

export async function fetchForChat(resource, options) {
  const { onMessage, onStart, onEnd, onError, ...fetchOptions } = options
  const resp = await fetch(resource, fetchOptions).catch(async (err) => {
    await onError(err)
  })
  if (!resp) return
  if (!resp.ok) {
    await onError(resp)
    return
  }
  const parser = createParser((event) => {
    if (event.type === 'event') {
      onMessage(event.data)
    }
  })
  let hasStarted = false
  for await (const chunk of streamAsyncIterable(resp.body)) {
    const str = new TextDecoder().decode(chunk)
    if (!str.startsWith('{') && !str.startsWith('"{')) {
      parser.feed(str)
    } else {
      const result = JSON.parse(str).result
      const message = JSON.parse(result).message
      const content = JSON.parse(message).content
      const formattedStr = 'data: ' + content + '\n\ndata: [DONE]\n\n'
      parser.feed(formattedStr)
    }

    if (!hasStarted) {
      hasStarted = true
      await onStart(str)
    }
  }
  await onEnd()
}

export async function fetchForBrowserMng(resource, options) {
  const { onMessage, onStart, onEnd, onError, ...fetchOptions } = options
  const resp = await fetch(resource, fetchOptions).catch(async (err) => {
    await onError(err)
  })
  if (!resp) return
  if (!resp.ok) {
    await onError(resp)
    return
  }
  const parser = createParser((event) => {
    if (event.type === 'event') {
      onMessage(event.data)
    }
  })
  let hasStarted = false
  for await (const chunk of streamAsyncIterable(resp.body)) {
    const str = new TextDecoder().decode(chunk)
    if (!str.startsWith('{') && !str.startsWith('"{')) {
      parser.feed(str)
    } else {
      const result = JSON.parse(str).result
      const formattedStr = 'data: ' + result + '\n\ndata: [DONE]\n\n'
      parser.feed(formattedStr)
    }

    if (!hasStarted) {
      hasStarted = true
      await onStart(str)
    }
  }
  await onEnd()
}
