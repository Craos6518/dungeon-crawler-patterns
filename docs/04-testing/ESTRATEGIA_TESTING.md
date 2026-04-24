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
source scripts/setup-java.sh
mvn test
```

En VS Code, tambien puede ejecutarse la suite con el runner de pruebas integrado.

## Metrica operativa unica (fuente de verdad)

- Fecha de corte: 2026-04-23
- Sello de ejecucion: `mvn test` en rama `Revision-final`
- Resultado vigente: 221 tests, 0 fallos, 0 errores, 0 omitidos

## Regla de interpretacion de metricas

- Cualquier conteo en README, auditorias o reportes historicos se considera contextual.
- El conteo oficial vigente para defensa tecnica se toma exclusivamente de esta seccion.
- `DisabledAnnotationPolicyTest` mantiene la gobernanza para evitar exclusiones arbitrarias.

## Historial de baseline

- Baseline documentado previo: 241 tests en verde, 0 fallos, 2 omitidos.
- Se conserva solo como referencia historica, no como metrica operativa actual.

## Cobertura y alcance

- Fuerte en runtime de negocio: estado, combate, persistencia, observer y comandos.
- E2E existente: validacion de contrato runtime/adaptadores.
- E2E pendiente: automatizacion visual completa en navegador real.

## Sobre `target/surefire-reports`

`target/surefire-reports/*` se considera evidencia generada no versionable.
No debe usarse como fuente documental canonica porque puede variar segun runner, entorno y momento de ejecucion.
