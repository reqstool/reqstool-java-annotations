#!/usr/bin/env bash
# Combines this library's own main/test annotation processor output into a
# single annotations.yml, the way the Maven/Gradle plugins' combineOutput()
# would -- but this library has no shared mojo to do it, since main and test
# annotation processing run in separate javac invocations and write to
# separate generated-sources directories.
#
# Usage: scripts/combine-annotations.sh <main-annotations.yml> <test-annotations.yml> <output.yml>
set -euo pipefail

main_file="$1"
test_file="$2"
output_file="$3"

mkdir -p "$(dirname "$output_file")"

{
  echo '# yaml-language-server: $schema=https://raw.githubusercontent.com/reqstool/reqstool-client/main/src/reqstool/resources/schemas/v1/annotations.schema.json'
  yq eval-all '. as $item ireduce ({}; . * $item )' "$main_file" "$test_file" | grep -v "^#"
} > "$output_file"
