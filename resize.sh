#!/bin/bash
set -euo pipefail
for i in *.png; do
    if [[ "$i" =~ px.png$ ]] || [[ "$i" =~ -thumb.png$ ]]; then
      continue
    fi
    output="$(basename "$i" ".png")-1024px.png"
    output_thumb="$(basename "$i" ".png")-thumb.png"
    echo "$i -> $output"
    convert \
        -resize 1024x1024 \
        -define png:compression-filter=2 \
        -define png:compression-level=9 \
        -define png:compression-strategy=1 \
        "$i" "$output"

    echo "$i -> $output_thumb"
    convert \
        -resize 256x256 \
        -define png:compression-filter=2 \
        -define png:compression-level=9 \
        -define png:compression-strategy=1 \
        "$i" "$output_thumb"
done
