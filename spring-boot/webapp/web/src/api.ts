export type Hello = { message: string };

/** The backend's greeting from `GET /api/hello`. */
export async function fetchHello(fetchFn: typeof fetch = fetch): Promise<string> {
  const res = await fetchFn("/api/hello");
  if (!res.ok) throw new Error(`GET /api/hello: ${res.status}`);
  return ((await res.json()) as Hello).message;
}
