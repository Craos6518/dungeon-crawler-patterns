# GDD CANONICO - FUENTE DE VERDAD

Este es el documento oficial de producto.

Estado documental:
- Tipo: fuente de verdad
- Concepto: GDD (producto)
- Vigente desde: 2026-04-04
- Rama auditada: Flujo-de-mazmorra

Versiones anteriores (legacy):
- GDD fragmentado previo a la consolidacion documental del 2026-04-10.
- Los derivados legacy fueron retirados del arbol canonico para evitar duplicacion activa.

Auditoria relacionada:
- docs/05-audit/REPORTE_FINAL_DOCUMENTACION_2026-04-04.md
- docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md
- docs/01-product/Especificacion_Requerimientos_Sistema_ISO29148.md

## Navegacion documental

- Producto (GDD): docs/01-product/GDD_CANONICO.md
- Arquitectura: docs/02-architecture/ARQUITECTURA_RUNTIME.md
- Patrones: docs/03-patterns/README.md
- Testing: docs/04-testing/ESTRATEGIA_TESTING.md
- Auditoria: docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md

## Proposito academico
Este proyecto demuestra implementacion verificable de patrones de diseno en un dungeon crawler por turnos, con flujo productivo en runtime y validacion por pruebas automatizadas.

## Alcance funcional vigente
- Flujo principal: menu -> seleccion de heroe -> exploracion -> combate -> tesoro -> progresion de campana.
- Campana por temas: poison -> ice -> fire -> dark.
- Persistencia por slots con restauracion de sesion.
- Inventario jerarquico con consumo real de items.
- Eventos productivos con observers conectados al estado de sesion.

## Historias de usuario (estado al cierre de auditoria)
- HU-01 Combate basico: implementada.
- HU-02 Seleccion de clase de heroe: implementada.
- HU-03 Sistema de inventario: implementada.
- HU-04 Generacion procedural: implementada.
- HU-05 Guardado/carga: implementada.

## Nota de HU-02
El runtime productivo opera con seleccion por `heroType` y contrato simplificado de inicio de partida.

## Reglas de runtime relevantes para documentacion
- Fuente de verdad del flujo: `GameStateContext` + `GameFlowState`.
- `activeScreen` es valor derivado para presentacion/UI, no orquestador de estados.
- Observer se usa en runtime productivo desde `GameSessionFactory`, no solo en demos.
- Tesoro post-combate forma parte del loop principal y persiste en memento.

## Fuera de alcance actual
- Cobertura E2E con navegador real y validacion visual automatizada.
