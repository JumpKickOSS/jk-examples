"use client";

import { useEffect, useState } from "react";
import { fetchGreeting } from "@/lib/api";

export default function Greeting() {
  const [greeting, setGreeting] = useState("…");

  useEffect(() => {
    fetchGreeting().then(setGreeting, (e: Error) => setGreeting(e.message));
  }, []);

  return (
    <p id="greeting" className="text-lg text-zinc-600 dark:text-zinc-400">
      The API says: <code>{greeting}</code>
    </p>
  );
}
