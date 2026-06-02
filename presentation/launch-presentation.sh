#!/bin/bash

# 🎭 Eranthia Presentation - Quick Launcher
# Abre la presentación académica en el navegador por defecto

# Detectar el sistema operativo
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    xdg-open "$( cd "$(dirname "$0")" && pwd )/index.html" 2>/dev/null || \
    xdg-open "$( cd "$(dirname "$0")" && pwd )/eranthia-presentation.html"
    
elif [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    open "$( cd "$(dirname "$0")" && pwd )/index.html" 2>/dev/null || \
    open "$( cd "$(dirname "$0")" && pwd )/eranthia-presentation.html"
    
elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
    # Windows (Git Bash)
    start "$( cd "$(dirname "$0")" && pwd )\index.html" 2>/dev/null || \
    start "$( cd "$(dirname "$0")" && pwd )\eranthia-presentation.html"
    
else
    echo "Sistema operativo no detectado. Abre manualmente:"
    echo "  file://$(cd "$(dirname "$0")" && pwd)/index.html"
    exit 1
fi

echo "✅ Abriendo presentación Eranthia..."
