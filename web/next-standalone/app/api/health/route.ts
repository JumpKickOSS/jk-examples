/** `GET /api/health`: answers once the standalone server is up. */
export function GET(): Response {
  return Response.json({ status: "ok" });
}
