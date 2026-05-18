## [2.0.0] — 2026-05-18

### Added
- Soporte visual para nuevos tiers de enemigos `centinela` y `semi-jefe`.
- Nuevos assets de enemigos para centinelas y semi-jefes en la UI.
- Script `scripts/convert-enemy-images.sh` para normalizar imágenes de enemigos.
- Nuevos diagramas arquitectónicos, documentos de referencia de interfaz y capturas canónicas del juego.

### Changed
- El `GameViewModel` ahora clasifica enemigos con los tiers `centinela`, `semi-jefe` y `jefe`.
- Se ajustaron los umbrales de HP usados para inferir el tier de enemigos.
- La UI web (`game.js`, `game.css`) fue adaptada para renderizar los nuevos tiers y sus badges.
- El asset principal de Arachnovex pasó a usar formato `.jpg`.

### Fixed
- Ajuste de estilos para que los badges de tier sigan coloreándose correctamente tras el cambio de taxonomía de enemigos.

### Removed
- Se eliminó por completo la presentación estática ubicada en `presentation/` (`index.html`, `app.js`, `style.css`).
- Se retiraron los assets antiguos `arachnovex.png` y `arachnovex.webp`.

### Documentation
- Se actualizó la especificación funcional con diagramas de estados y secuencia refinados.
- Se revisó la documentación de patrones para unificar terminología y relaciones en diagramas.
- Se añadieron documentos canónicos de estructura de interfaz, exportaciones y screenshots del juego.