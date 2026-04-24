---
description: "Revisa, centraliza y valida la documentación final del proyecto contra el código fuente"
argument-hint: "Fecha de revisión, alcance y criterios de auditoría"
agent: "agent"
---

Realiza una revisión final de la documentación del proyecto **dungeon-crawler-patterns** con fecha de corte **23 de abril de 2026**.

Objetivo:

- Centralizar la documentación para que exista una sola fuente de verdad por concepto.
- Verificar que la documentación esté alineada con el código real del repositorio.
- Confirmar que cada patrón de diseño esté explicado, ubicado correctamente y tenga su diagrama de clases asociado.
- Detectar duplicidades, referencias obsoletas, contradicciones o secciones huérfanas.

Alcance mínimo de la revisión:

- [docs/README.md](docs/README.md) como índice canónico de documentación.
- [README.md](README.md) como entrada principal del proyecto.
- [docs/03-patterns/](docs/03-patterns/) para la documentación de patrones.
- [docs/02-architecture/diagramas/](docs/02-architecture/diagramas/) para los diagramas de clases y artefactos visuales.
- [docs/05-audit/](docs/05-audit/) para el estado de auditoría vigente.
- Código fuente en [src/main/java](src/main/java) y pruebas relevantes en [src/test/java](src/test/java).

Criterios de revisión:

1. Verifica que cada patrón declarado en la documentación exista en el código y tenga una explicación clara de propósito, participantes y responsabilidades.
2. Confirma que cada patrón cuente con al menos un diagrama de clases o una referencia visual equivalente dentro de la documentación.
3. Revisa que la nomenclatura, los paquetes y las clases mencionadas en la documentación coincidan con las clases reales.
4. Identifica documentos duplicados y propone cuál debe ser la fuente de verdad.
5. Señala referencias desactualizadas a rutas, conteos de tests, cantidad de patrones o nombres de clases.
6. Detecta patrones documentados que no estén implementados o implementaciones presentes que no estén documentadas.
7. Valida que la documentación final soporte una defensa técnica académica: claridad, trazabilidad, evidencia y consistencia.

Formato de salida esperado:

- Resumen ejecutivo breve.
- Lista priorizada de hallazgos con severidad, impacto y archivo afectado.
- Estado por patrón en una tabla con columnas: patrón, ubicación en código, documentación, diagrama de clases, estado.
- Recomendaciones concretas de centralización documental.
- Si no hay hallazgos críticos, indícalo explícitamente y explica los riesgos residuales.

Reglas de evaluación:

- Prioriza la evidencia del código sobre la declaración documental.
- No inventes clases, rutas ni patrones.
- Si un patrón no puede verificarse, márcalo como inconsistente hasta encontrar evidencia suficiente.
- Mantén el foco en la revisión documental; no propongas cambios funcionales fuera del alcance salvo que afecten directamente la veracidad de la documentación.
