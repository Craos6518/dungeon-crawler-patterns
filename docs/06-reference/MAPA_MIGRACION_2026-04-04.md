# Mapa de Migracion Documental

- Fecha de actualizacion: 2026-04-04
- Rama auditada: Flujo-de-mazmorra
- Estado: vigente

## Regla explicita de raiz documental (objetivo de corte)

- Se usa unicamente `Docs/` como raiz documental
- `docs/` queda prohibido

## Plan de migracion

| Origen | Destino | Accion |
| --- | --- | --- |
| docs/01-product/GDD_CANONICO.md | Docs/01-product/GDD_CANONICO.md | Mover y actualizar enlaces entrantes/salientes |
| docs/02-architecture/ARQUITECTURA_RUNTIME.md | Docs/02-architecture/ARQUITECTURA_RUNTIME.md | Mover y actualizar enlaces entrantes/salientes |
| docs/03-patterns/README.md | Docs/03-patterns/README.md | Mover y actualizar enlaces entrantes/salientes |
| docs/03-patterns/*.md | Docs/03-patterns/*.md | Mover y actualizar enlaces entrantes/salientes |
| docs/04-testing/ESTRATEGIA_TESTING.md | Docs/04-testing/ESTRATEGIA_TESTING.md | Mover y actualizar enlaces entrantes/salientes |
| docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md | Docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md | Mover y actualizar enlaces entrantes/salientes |
| docs/05-audit/REPORTE_FINAL_DOCUMENTACION_2026-04-04.md | Docs/05-audit/REPORTE_FINAL_DOCUMENTACION_2026-04-04.md | Mover y actualizar enlaces entrantes/salientes |
| docs/06-reference/*.md | Docs/06-reference/*.md | Mover y actualizar enlaces entrantes/salientes |
| docs/README.md | Docs/README.md | Reemplazar indice raiz por indice canonico en Docs |

## Condicion de borrado de docs/

`docs/` se elimina solo cuando se cumplan simultaneamente estas condiciones:

1. Todo archivo canonico de `docs/` existe en `Docs/` con el mismo contenido funcional.
2. No queda ninguna referencia a rutas `docs/` en README, guias, auditorias y enlaces de navegacion.
3. Todos los archivos trasladados declaran su estado documental (fuente de verdad o legacy).
4. La busqueda `rg -n "docs/"` solo devuelve referencias historicas explicitamente marcadas como legacy.
5. Revision final de coherencia documental completada sin duplicaciones activas.
