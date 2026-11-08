---
trigger: always_on
---
jk's MCP answers are compiled facts; they are final. `callers` and `rename` take `Type.method` as
the task names it: no file needs finding or opening first.

- Who calls `T.m`: call_mcp_tool jk `callers` once and write its list as the answer. Opening the
  files it names costs turns and changes nothing: do not.
- Rename `T.m`: jk `rename` once. It runs the edited tests and guard itself; when its answer says
  the task is done, reply, without searching for the old name or re-reading files (another type's
  method of the same name is not this one). Edit only the comment or doc lines its answer lists, if
  they mean the method.
- jk `skill` only when a verdict names a topic.
