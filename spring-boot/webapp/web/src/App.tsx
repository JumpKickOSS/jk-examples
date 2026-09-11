import { useEffect, useState } from "react";

type Hello = { message: string };

// One fetch to the backend and one client-side "route" (no router library): the path is
// changed with pushState, and a reload of /about is what the app's SPA fallback exists for.
export function App() {
  const [hello, setHello] = useState<string>("…");
  const [path, setPath] = useState(window.location.pathname);

  useEffect(() => {
    fetch("/api/hello")
      .then((r) => r.json() as Promise<Hello>)
      .then((h) => setHello(h.message))
      .catch((e: unknown) => setHello(`request failed: ${String(e)}`));
  }, []);

  useEffect(() => {
    const onPop = () => setPath(window.location.pathname);
    window.addEventListener("popstate", onPop);
    return () => window.removeEventListener("popstate", onPop);
  }, []);

  const go = (to: string) => (e: React.MouseEvent) => {
    e.preventDefault();
    window.history.pushState(null, "", to);
    setPath(to);
  };

  return (
    <main style={{ fontFamily: "system-ui, sans-serif", margin: "3rem auto", maxWidth: "40rem" }}>
      <h1>webapp</h1>
      <p>
        <code>GET /api/hello</code> → {hello}
      </p>
      <p>
        You are at <code>{path}</code>.{" "}
        <a href="/" onClick={go("/")}>home</a> · <a href="/about" onClick={go("/about")}>about</a> — reload
        on either; the server answers both with this page.
      </p>
    </main>
  );
}
