# Indice Canonico de Documentacion

- Fecha de actualizacion: 2026-04-04
- Rama auditada: Flujo-de-mazmorra
- Estado: fuente de verdad del indice documental

## Fuente de verdad por concepto

| Concepto | Documento fuente de verdad | Ubicacion |
| --- | --- | --- |
| GDD (producto) | docs/01-product/GDD_CANONICO.md | docs/01-product/ |
| Arquitectura | docs/02-architecture/ARQUITECTURA_RUNTIME.md | docs/02-architecture/ |
| Patrones | docs/03-patterns/README.md | docs/03-patterns/ |
| Testing | docs/04-testing/ESTRATEGIA_TESTING.md | docs/04-testing/ |
| Auditoria | docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md | docs/05-audit/ |

## Navegacion documental

- Producto (GDD): docs/01-product/GDD_CANONICO.md
- Arquitectura: docs/02-architecture/ARQUITECTURA_RUNTIME.md
- Patrones: docs/03-patterns/README.md
- Testing: docs/04-testing/ESTRATEGIA_TESTING.md
- Auditoria: docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md

## Bloque obligatorio para archivos legacy

```markdown
# ⚠️ DOCUMENTO OBSOLETO — NO USAR

Este documento NO es fuente de verdad.

Fuente vigente:
👉 docs/RUTA/AL/DOCUMENTO_VIGENTE.md

Estado:
- Obsoleto desde: 2026-04-04
- Motivo: consolidación post-auditoría

Este archivo se conserva únicamente por trazabilidad histórica.
```

## Regla de interpretacion

- Un concepto solo puede tener una fuente de verdad vigente.
- Todo archivo fuera de la fuente vigente debe declararse como legacy en la primera pantalla.
- Ningun archivo legacy puede duplicar contenido canonico completo.
