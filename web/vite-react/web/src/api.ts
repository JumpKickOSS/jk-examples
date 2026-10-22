export type Greeting = { message: string }

/** The JVM's greeting from `GET /api/hello`: Vite proxies it to :8080 in dev, the app serves it in the jar. */
export async function fetchGreeting(fetchFn: typeof fetch = fetch): Promise<string> {
  const res = await fetchFn('/api/hello')
  if (!res.ok) throw new Error(`GET /api/hello: ${res.status}`)
  return ((await res.json()) as Greeting).message
}
