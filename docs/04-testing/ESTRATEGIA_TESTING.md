# Estrategia de Testing y Evidencia

- Fecha de creacion: 2026-04-04
- Rama auditada: Flujo-de-mazmorra
- Estado: vigente

## Objetivo
Definir como se valida el sistema y como interpretar resultados en evaluacion academica.

## Navegacion documental

- Producto (GDD): docs/01-product/GDD_CANONICO.md
- Arquitectura: docs/02-architecture/ARQUITECTURA_RUNTIME.md
- Patrones: docs/03-patterns/README.md
- Testing: docs/04-testing/ESTRATEGIA_TESTING.md
- Auditoria: docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md

## Comandos recomendados
```bash
source setup-java.sh
mvn test
```

En VS Code, tambien puede ejecutarse la suite con el runner de pruebas integrado.

## Metricas de referencia para documentacion
- Baseline academico reportado: 241 tests en verde, 0 fallos, 2 omitidos.
- Ejecucion integrada en este workspace: 241 passed, 0 failed.

## Interpretacion de 241/241 y 2 omitidos
- 241/241 significa que todos los tests ejecutados en el baseline pasaron.
- 2 omitidos no son fallos funcionales; no bloquean validez arquitectonica.
- La gobernanza de pruebas impide introducir `@Disabled` arbitrarios (`DisabledAnnotationPolicyTest`).

## Cobertura y alcance
- Fuerte en runtime de negocio: estado, combate, persistencia, observer y comandos.
- E2E existente: validacion de contrato runtime/adaptadores.
- E2E pendiente: automatizacion visual completa en navegador real.

## Sobre `target/surefire-reports`
`target/surefire-reports/*` se considera evidencia generada no versionable.
No debe usarse como fuente documental canonica porque puede variar segun runner, entorno y momento de ejecucion.
