# Mapa de Migracion Documental

- Fecha de actualizacion: 2026-04-04
- Rama auditada: Flujo-de-mazmorra
- Estado: vigente

## Regla explicita de raiz documental (objetivo vigente)

- Se usa unicamente `docs/` como raiz documental
- `Docs/` queda eliminado

## Plan de migracion

| Origen | Destino | Accion |
| --- | --- | --- |
| Docs/Especificacion_Requerimientos_Sistema_ISO29148.md | docs/01-product/Especificacion_Requerimientos_Sistema_ISO29148.md | Migrar y conservar como referencia de requerimientos |
| Docs/Diagramas/* | docs/02-architecture/diagramas/* | Migrar diagramas y descriptores tecnicos |
| Docs/* legacy obsoleto | eliminado | Descartar para evitar duplicacion y conflicto de fuentes |

## Condicion de borrado de Docs/

`Docs/` se elimina solo cuando se cumplan simultaneamente estas condiciones:

1. Todo documento vigente apunta a `docs/` como unica raiz documental.
2. No queda ninguna referencia operativa a rutas `Docs/` en README, guias, auditorias y enlaces de navegacion.
3. Los archivos valiosos de `Docs/` fueron migrados o descartados con criterio explicito.
4. La busqueda `rg -n "Docs/"` solo devuelve referencias historicas o notas de migracion ya corregibles.
5. Revision final de coherencia documental completada sin duplicaciones activas.
