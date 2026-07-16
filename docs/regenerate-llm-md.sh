#!/usr/bin/env bash
# Regenerates LLM.md at the repository root using Claude Code.
# Usage: bin/regenerate-llm-md.sh
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

PROMPT=$(cat <<'EOF'
Create (overwrite) an LLM.md file at the repository root. Its purpose is to let LLM models /
coding assistants quickly understand what this repository is, when to use it, and how to use it.

Requirements for the process (accuracy matters more than speed):

1. Derive every fact from the repository itself — never from memory or the README alone.
   Cross-check the README against pom.xml files and git tags; if they disagree (e.g. stale
   version numbers), trust the poms/tags and flag the README as stale in the doc.
2. For every code example you include, state which module/package it comes from. If a snippet
   is illustrative rather than copied from real code, label it as such. Do not attribute an
   example to a location where it doesn't exist.
3. For non-obvious runtime semantics (e.g. interceptors), do not guess from the annotated
   sources: build the test module (`mvn -P jdk11 install/compile`, parent poms first if needed)
   and read the generated `*JAMImpl` sources under `target/generated-sources/annotations/`,
   plus the relevant processor code (e.g. `TypeMethodUtils.isMethodCallableForInterceptor`).
   Document what the generated code actually does.
4. After writing, verify the document against the source with fresh eyes (or a subagent):
   every annotation name/attribute, enum constant, method signature, Maven coordinate,
   version, and example attribution must match reality. Fix what doesn't.

Required content/sections:

- **What this repository is** — compile-time annotation-processor object mapper (JAM),
  modules (jam-common runtime, jam-processor, jam-tests), Maven coordinates, current version
  vs latest release tag, Java version support (jdk8/jdk11 profiles), license, upstream URL.
- **When to use it** — the problems it solves (compile-time generation, no reflection,
  cyclic graphs, in-place updates, Spring/CDI integration); comparable tools (MapStruct/SELMA).
- **Installation** — Maven dependency snippet (jam-common compile, jam-processor provided).
- **Quick start** — minimal @Mapper interface + MapperUtil.getMapper usage, note the
  generated `<Name>JAMImpl` naming and Lombok accessor support.
- **Public API** — table of all annotations in `sk.annotation.library.jam.annotations` with
  their targets and attributes; the enums with all constants; MapperUtil / MapperRunCtxData /
  InstanceCache signatures.
- **Examples** — representative snippets from jam-tests (renamed fields, field ignoring,
  aggregation, immutable/withCustom, @Return updates, Spring integration), each with its
  real source location.
- **Custom conversion methods and interceptors** — based on ex23 (MapperWithGenerics) and
  ex19: signature-matched factory/converter methods; interceptor matching rules (void, exactly
  two params, source assignable to param 1, destination to param 2, generic bounds); when they
  fire (end of each generated transform, declaration order) and when they don't (direct
  delegation to user methods). Include a generated-code excerpt.
- **Feature catalog** — one table row per jam-tests-minimum example package (ex1..ex23):
  feature demonstrated + key classes, verified against each package's mapper and test.
- **Gotchas** — @Return semantics, cyclic-mapping instance cache, config scoping
  (package/type/method), interceptor bypass, stale README versions.
EOF
)

STREAM_LOG="$(mktemp -t regenerate-llm-md.XXXXXX.jsonl)"
trap 'rm -f "$STREAM_LOG"' EXIT

# File modifications are restricted to LLM.md: the Edit(LLM.md) rule covers all
# file-editing tools (Write included) for that path only,
# git is limited to read-only subcommands, and no other write-capable tools are allowed
# (denied tool calls fail silently in -p mode). Exception: mvn still produces build output
# (target/, local ~/.m2 installs) — required to inspect generated *JAMImpl sources.
#
# --output-format stream-json emits one JSON event per line as Claude Code works; jq turns
# them into live progress messages so long silences don't look like a hang. The raw stream
# is kept in $STREAM_LOG to extract the final result afterwards.
claude -p "$PROMPT" \
    --permission-mode acceptEdits \
    --output-format stream-json --verbose \
    --allowedTools "Read Glob Grep Task Agent Edit(LLM.md) Bash(mvn:*) Bash(git log:*) Bash(git tag:*) Bash(git status) Bash(git diff:*) Bash(git show:*) Bash(ls:*) Bash(rg:*) Bash(fdfind:*) Bash(cat:*)" \
  | tee "$STREAM_LOG" \
  | jq --unbuffered -r '
      def brief: tostring | gsub("\\s+"; " ") | .[0:120];
      if .type == "system" and .subtype == "init" then
        "session started (model: \(.model), session: \(.session_id))"
      elif .type == "assistant" then
        (.message.content // [])[]
        | if .type == "tool_use" then
            "  tool \(.name): \((.input.description // .input.file_path // .input.pattern // .input.command // .input.prompt // "") | brief)"
          elif .type == "text" and (.text | length) > 0 then
            "* \(.text | brief)"
          else empty
          end
      elif .type == "result" then
        if .is_error then
          "FAILED after \((.duration_ms / 1000) | floor)s: \((.result // "unknown error") | brief)"
        else
          "finished in \((.duration_ms / 1000) | floor)s, \(.num_turns) turns, cost $\(.total_cost_usd), denied tool calls: \(.permission_denials | length)"
        end
      else empty
      end
    ' \
  | while IFS= read -r message; do
      printf '[%s] %s\n' "$(date +%H:%M:%S)" "$message"
    done

# The final "result" event is the authoritative outcome; a missing one means the run
# aborted before finishing.
RESULT_IS_ERROR=$(jq -r 'select(.type == "result") | .is_error' "$STREAM_LOG" | tail -1)
if [ "$RESULT_IS_ERROR" != "false" ]; then
    echo "Claude Code did not report success — LLM.md may not have been regenerated." >&2
    exit 1
fi

echo
echo "--- Final report from Claude Code ---"
jq -r 'select(.type == "result") | .result // empty' "$STREAM_LOG"
echo
echo "Done. Review the result: git diff LLM.md"
