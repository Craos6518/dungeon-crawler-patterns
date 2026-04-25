#!/usr/bin/env bash
set -euo pipefail

# Converts enemy art to a UI friendly fixed canvas.
# Default target is 1366x768 (16:9), matching the combat/exploration illustration ratio.
#
# Usage examples:
#   ./scripts/convert-enemy-images.sh
#   ./scripts/convert-enemy-images.sh --width 1280 --height 720
#   ./scripts/convert-enemy-images.sh --dry-run
#
# Requirements:
#   ImageMagick (magick or convert)

WIDTH=1366
HEIGHT=768
QUALITY=88
DRY_RUN=false

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CENTINELAS_DIR="$ROOT_DIR/src/main/resources/ui/assets/images/centinelas"
SEMIJEFES_DIR="$ROOT_DIR/src/main/resources/ui/assets/images/semijefes"

print_help() {
  cat <<'EOF'
convert-enemy-images.sh

Resize/crop enemy images to a fixed UI canvas.

Options:
  --width <px>     Target width (default: 1366)
  --height <px>    Target height (default: 768)
  --quality <1-100> Compression quality (default: 88)
  --dry-run        Show planned changes without writing files
  -h, --help       Show this help
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --width)
      WIDTH="$2"
      shift 2
      ;;
    --height)
      HEIGHT="$2"
      shift 2
      ;;
    --quality)
      QUALITY="$2"
      shift 2
      ;;
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    -h|--help)
      print_help
      exit 0
      ;;
    *)
      echo "Error: unknown option '$1'"
      print_help
      exit 1
      ;;
  esac
done

if ! [[ "$WIDTH" =~ ^[0-9]+$ ]] || ! [[ "$HEIGHT" =~ ^[0-9]+$ ]]; then
  echo "Error: --width and --height must be positive integers."
  exit 1
fi

if ! [[ "$QUALITY" =~ ^[0-9]+$ ]] || (( QUALITY < 1 || QUALITY > 100 )); then
  echo "Error: --quality must be an integer between 1 and 100."
  exit 1
fi

if command -v magick >/dev/null 2>&1; then
  IM_CMD=(magick)
elif command -v convert >/dev/null 2>&1; then
  IM_CMD=(convert)
else
  echo "Error: ImageMagick not found. Install 'magick' (or 'convert') and try again."
  exit 1
fi

if [[ ! -d "$CENTINELAS_DIR" ]] || [[ ! -d "$SEMIJEFES_DIR" ]]; then
  echo "Error: expected image folders not found:"
  echo "  $CENTINELAS_DIR"
  echo "  $SEMIJEFES_DIR"
  exit 1
fi

echo "Target size: ${WIDTH}x${HEIGHT}, quality=${QUALITY}"
echo "Dry-run: ${DRY_RUN}"

processed=0
skipped=0

process_file() {
  local file="$1"
  local ext="${file##*.}"
  local lower_ext="$(echo "$ext" | tr '[:upper:]' '[:lower:]')"

  case "$lower_ext" in
    jpg|jpeg|png|webp)
      ;;
    *)
      skipped=$((skipped + 1))
      return
      ;;
  esac

  echo "Processing: $file"
  processed=$((processed + 1))

  if [[ "$DRY_RUN" == true ]]; then
    return
  fi

  # Use a temp output and move atomically to avoid partial writes.
  local tmp
  tmp="$(mktemp "${file}.tmp.XXXXXX")"

  "${IM_CMD[@]}" "$file" \
    -auto-orient \
    -resize "${WIDTH}x${HEIGHT}^" \
    -gravity center \
    -extent "${WIDTH}x${HEIGHT}" \
    -strip \
    -quality "$QUALITY" \
    "$tmp"

  mv -f "$tmp" "$file"
}

while IFS= read -r -d '' file; do
  process_file "$file"
done < <(find "$CENTINELAS_DIR" "$SEMIJEFES_DIR" -maxdepth 1 -type f -print0)

echo

echo "Done. Processed: $processed, skipped (unsupported extension): $skipped"
