# Dungeon Crawler Patterns

Proyecto academico en Java 17 para demostrar patrones de diseno ejecutandose en un runtime real, con interfaces de consola y GUI, pruebas automatizadas y documentacion tecnica consolidada en `docs/`.

Este `README.md` de raiz no reemplaza la documentacion canonica: su funcion es servir como puerta de entrada operativa del repositorio.

## Para que entrar por aqui

- Entender rapido que contiene el repo.
- Ejecutar el juego o las pruebas sin navegar toda la documentacion.
- Ubicar el codigo principal antes de ir al detalle canonico en `docs/`.

## Inicio rapido

### Prerrequisito

```bash
source setup-java.sh
```

### Ejecutar en consola

```bash
./play.sh
```

### Ejecutar GUI

```bash
./play-gui.sh
```

### Ejecutar tests

```bash
mvn test
```

## Mapa rapido del repositorio

| Ruta | Uso principal |
| --- | --- |
| `src/main/java` | Codigo fuente del juego y de los patrones en runtime |
| `src/test/java` | Tests unitarios e integracion |
| `docs/` | Documentacion canonica del proyecto |
| `play.sh` | Arranque en consola |
| `play-gui.sh` | Arranque GUI |
| `package-linux.sh` | Empaquetado Linux con `jpackage` |
| `package-windows.ps1` | Empaquetado Windows |

## Puntos de entrada utiles en codigo

- Runtime y coordinacion general: `src/main/java/game/application`
- Estado de sesion y memento: `src/main/java/game/application/state`
- Dominio de combate: `src/main/java/game/domain/combat`
- Exploracion y dungeon: `src/main/java/game/domain/exploration`
- UI y arranque: `src/main/java/game/ui`

## Fuente de verdad documental

La documentacion vigente vive en `docs/`. Este `README.md` resume operacion y orientacion; el detalle canonico por tema se mantiene aqui:

- Indice documental: [docs/README.md](docs/README.md)
- Producto: [docs/01-product/GDD_CANONICO.md](docs/01-product/GDD_CANONICO.md)
- Arquitectura: [docs/02-architecture/ARQUITECTURA_RUNTIME.md](docs/02-architecture/ARQUITECTURA_RUNTIME.md)
- Patrones: [docs/03-patterns/README.md](docs/03-patterns/README.md)
- Testing: [docs/04-testing/ESTRATEGIA_TESTING.md](docs/04-testing/ESTRATEGIA_TESTING.md)
- Auditoria: [docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md](docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md)

## Regla de mantenimiento

- `README.md` raiz: onboarding operativo del repositorio.
- `docs/`: detalle canonico y fuente de verdad por concepto.
- Si aparece duplicacion, prevalece `docs/`.
