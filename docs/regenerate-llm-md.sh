#!/usr/bin/env bash
# Regenerates LLM.md at the repository root using Claude Code.
# Usage: bin/regenerate-llm-md.sh
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

PROMPT_FILE="$REPO_ROOT/docs/LLM.md.prompt"
if [ ! -f "$PROMPT_FILE" ]; then
    echo "Prompt file not found: $PROMPT_FILE" >&2
    exit 1
fi

# The prompt file starts with a human-facing preamble followed by a '---' separator line;
# everything after that separator is the actual prompt sent to Claude Code.
PROMPT=$(awk 'found { print } /^---$/ { found = 1 }' "$PROMPT_FILE")
if [ -z "$PROMPT" ]; then
    echo "No prompt content found after the '---' separator in $PROMPT_FILE" >&2
    exit 1
fi

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
    --allowedTools "Read Glob Grep Task Agent Edit(LLM.md) Bash(mvn:*) Bash(./gradlew:*) Bash(git log:*) Bash(git tag:*) Bash(git status) Bash(git diff:*) Bash(git show:*) Bash(ls:*) Bash(rg:*) Bash(fdfind:*) Bash(cat:*)" \
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

