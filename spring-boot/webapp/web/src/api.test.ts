import { expect, test } from "vitest";
import { fetchHello } from "./api";

test("fetchHello reads the message the API returns", async () => {
  const fake = async () => Response.json({ message: "Hello, world!" });
  expect(await fetchHello(fake as typeof fetch)).toBe("Hello, world!");
});
