export type Greeting = { message: string };

/** The JVM's greeting from `GET /api/hello`, which next.config rewrites to the Spring Boot app. */
export async function fetchGreeting(fetchFn: typeof fetch = fetch): Promise<string> {
  const res = await fetchFn("/api/hello");
  if (!res.ok) throw new Error(`GET /api/hello: ${res.status}`);
  return ((await res.json()) as Greeting).message;
}
