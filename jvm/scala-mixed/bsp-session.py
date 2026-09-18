#!/usr/bin/env python3
"""Drive `jk bsp serve` over stdio the way a BSP client does and print what it answers.

Sends build/initialize, workspace/buildTargets, buildTarget/scalacOptions, buildTarget/javacOptions and
buildTarget/compile, then shuts the server down. Long URI arrays are folded to their first entries and a
count. `--diagnostic` drops a Scala file with a type error into src/main/scala before the compile and
removes it afterwards, so build/publishDiagnostics shows up in the transcript.
"""
import json
import os
import subprocess
import sys
from pathlib import Path

HERE = Path(__file__).resolve().parent
BROKEN = HERE / "src/main/scala/com/example/mixed/Broken.scala"


def frame(body: dict) -> bytes:
    data = json.dumps(body).encode()
    return b"Content-Length: %d\r\n\r\n%s" % (len(data), data)


def read_message(out) -> dict | None:
    length = None
    while True:
        line = out.readline()
        if not line:
            return None
        if line.lower().startswith(b"content-length:"):
            length = int(line.split(b":")[1])
        if line in (b"\r\n", b"\n"):
            break
    return json.loads(out.read(length))


def fold(value, key=""):
    """Every list of more than four strings becomes its first two plus a count."""
    if isinstance(value, dict):
        return {k: fold(v, k) for k, v in value.items()}
    if isinstance(value, list):
        if len(value) > 4 and all(isinstance(v, str) for v in value):
            return value[:2] + [f"… {len(value) - 2} more"]
        return [fold(v) for v in value]
    return value


def main() -> int:
    diagnostic = "--diagnostic" in sys.argv
    root = HERE.as_uri()
    proc = subprocess.Popen(["jk", "bsp", "serve"], cwd=HERE, stdin=subprocess.PIPE, stdout=subprocess.PIPE)
    requests = [
        {"jsonrpc": "2.0", "id": 1, "method": "build/initialize",
         "params": {"displayName": "bsp-session.py", "version": "1", "bspVersion": "2.1.0", "rootUri": root,
                    "capabilities": {"languageIds": ["java", "scala"]}}},
        {"jsonrpc": "2.0", "method": "build/initialized", "params": {}},
        {"jsonrpc": "2.0", "id": 2, "method": "workspace/buildTargets", "params": {}},
    ]
    try:
        if diagnostic:
            BROKEN.write_text("package com.example.mixed\n\nobject Broken:\n  val n: Int = \"not a number\"\n")
        for r in requests:
            proc.stdin.write(frame(r))
        proc.stdin.flush()
        targets = None
        while targets is None:
            m = read_message(proc.stdout)
            if m is None:
                return 1
            print(json.dumps(fold(m), indent=1))
            if m.get("id") == 2:
                targets = [t["id"] for t in m["result"]["targets"]]
        follow = [
            {"jsonrpc": "2.0", "id": 3, "method": "buildTarget/scalacOptions", "params": {"targets": targets}},
            {"jsonrpc": "2.0", "id": 4, "method": "buildTarget/javacOptions", "params": {"targets": targets}},
            {"jsonrpc": "2.0", "id": 5, "method": "buildTarget/compile", "params": {"targets": targets}},
            {"jsonrpc": "2.0", "id": 6, "method": "build/shutdown", "params": None},
            {"jsonrpc": "2.0", "method": "build/exit"},
        ]
        for r in follow:
            proc.stdin.write(frame(r))
        proc.stdin.flush()
        while True:
            m = read_message(proc.stdout)
            if m is None:
                break
            print(json.dumps(fold(m), indent=1))
    finally:
        if diagnostic and BROKEN.exists():
            BROKEN.unlink()
        proc.stdin.close()
        proc.wait(timeout=60)
    return proc.returncode


if __name__ == "__main__":
    sys.exit(main())
