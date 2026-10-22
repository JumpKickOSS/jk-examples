import { expect, test } from 'vitest'
import { fetchGreeting } from './api'

test('fetchGreeting reads the message the API returns', async () => {
  const fake = async () => Response.json({ message: 'Hello, world!' })
  expect(await fetchGreeting(fake as typeof fetch)).toBe('Hello, world!')
})
