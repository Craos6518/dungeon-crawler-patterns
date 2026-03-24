# Guía del Usuario - Juego Interactivo

## 1. Introducción
Bienvenido a **Dungeon Crawler Académico**. Esta es una experiencia de consola donde podrás explorar mazmorras, luchar contra enemigos y gestionar tu inventario, todo mientras observas los patrones de diseño en acción.

## 2. Cómo Jugar
Para iniciar el juego, ejecuta:
```bash
mvn exec:java -Dexec.mainClass="game.InteractiveGame"
```

### Configuración de la Partida
1. **Selección de Héroe**: Elige entre Guerrero (Resistencia), Mago (Poder) o Arquero (Precisión).
2. **Selección de Tema**: Elige la mazmorra (Fuego, Hielo, Oscuridad, Veneno).

### Menú de Exploración
- **Avanzar**: Muévete a la siguiente sala de la mazmorra.
- **Buscar Tesoro**: Intenta encontrar items en la sala actual.
- **Inventario**: Gestiona tus objetos y contenedores.
- **Guardar**: Salva tu progreso actual.

### Menú de Combate
Al encontrar un enemigo, entrarás en combate por turnos:
1. **Atacar**: Realiza un ataque físico básico.
2. **Defender**: Reduce el daño recibido en el siguiente turno.
3. **Usar Item**: Consume una poción u objeto del inventario.
4. **Habilidad**: Usa una habilidad especial de tu clase.

## 3. Guía de Mapas y Temas
Cada tema de mazmorra tiene enemigos y tesoros coherentes:
- **Fuego**: Enemigos con efectos de quemadura.
- **Hielo**: Enemigos que pueden ralentizar o congelar.
- **Veneno**: Enemigos que aplican daño por desgaste.
- **Oscuridad**: Enemigos con ataques poderosos y sombríos.

## 4. Gestión de Inventario
Tu inventario es jerárquico. Puedes encontrar bolsas o cajas que contienen otros objetos. Para usarlos, simplemente selecciona el objeto deseado desde el menú de inventario.

## 5. Guardado y Carga
Puedes guardar tu partida en cualquier momento fuera del combate. El sistema creará un archivo en la carpeta `game-saves/`. Para cargar, selecciona la opción "Cargar Partida" en el menú principal e introduce el nombre del archivo.
