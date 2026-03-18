# 1. Requerimientos Funcionales

RF-01 -- Crear Nueva Partida El sistema debe permitir al jugador iniciar
una nueva partida creando un equipo de hasta 3 héroes seleccionando su
clase y nombre. RF-02 -- Cargar Partida El sistema debe permitir cargar
una partida previamente guardada desde uno de los 3 slots disponibles.
RF-03 -- Exploración de Mazmorra El jugador debe poder desplazarse entre
habitaciones de una mazmorra generada proceduralmente. RF-04 --
Encuentro con Enemigos El sistema debe iniciar un combate
automáticamente cuando el jugador entre en una habitación con enemigos.
RF-05 -- Sistema de Combate por Turnos El sistema debe permitir combates
tácticos por turnos en un grid 6x6. RF-06 -- Acciones en Combate El
jugador debe poder: Moverse Atacar Usar habilidad Usar ítem Pasar turno
RF-07 -- IA de Enemigos Los enemigos deben actuar automáticamente según
una estrategia de comportamiento (Agresiva, Defensiva, Inteligente).
RF-08 -- Sistema de Experiencia y Nivel Los héroes deben ganar
experiencia al derrotar enemigos y subir de nivel automáticamente al
alcanzar el umbral requerido. RF-09 -- Inventario Jerárquico El sistema
debe permitir almacenar ítems en una estructura jerárquica (contenedores
dentro de contenedores). RF-10 -- Efectos de Estado El sistema debe
permitir aplicar efectos temporales a personajes (Veneno, Quemadura,
Fortaleza). RF-11 -- Guardar Partida El sistema debe permitir guardar el
estado completo del juego fuera de combate. RF-12 -- Estados del Juego
El sistema debe gestionar estados: Menú, Exploración, Combate,
Inventario, GameOver.

# 2. Historias de Usuario (Versión Refinada)

HU-01: Combate Básico Como jugador Quiero combatir enemigos por turnos
Para derrotarlos estratégicamente Criterios: Grid 6x6 visible Turnos por
velocidad Condición clara de victoria/derrota HU-02: Creación de Héroes
Como jugador Quiero elegir clase y nombre Para formar un equipo
estratégico Criterios: 3 clases disponibles Stats diferenciados
Habilidades únicas HU-03: Sistema de Inventario Como jugador Quiero
gestionar ítems Para mejorar mis capacidades Criterios: Estructura
jerárquica Equipar armas Usar consumibles HU-04: Generación Procedural
Como jugador Quiero que cada mazmorra sea diferente Para aumentar
rejugabilidad Criterios: 2 temas distintos Habitaciones conectadas
Habitación de jefe HU-05: Guardado de Partida Como jugador Quiero
guardar mi progreso Para continuar después Criterios: 3 slots
Persistencia completa Confirmación de guardado

# 3. Diagrama de Casos de Uso

Actor Principal: Jugador Casos de Uso: Iniciar Nueva Partida Cargar
Partida Explorar Mazmorra Iniciar Combate Ejecutar Acción de Combate
Gestionar Inventario Subir Nivel Guardar Partida Cambiar Estado del
Juego Relaciones: "Explorar Mazmorra" incluye "Iniciar Combate" "Iniciar
Combate" incluye "Ejecutar Acción de Combate" "Ejecutar Acción de
Combate" puede extender "Aplicar Efecto de Estado"

# 4. Casos de Uso Detallados

Caso de Uso 1 Nombre: Iniciar Nueva Partida Autor: Andrés Fecha:
27/02/2026 Descripción: Permite al jugador crear un nuevo equipo de
héroes y comenzar la exploración. Actores: Jugador Precondiciones: El
sistema se encuentra en el estado Menú Principal Flujo Normal: El
jugador selecciona "Nueva Partida". El sistema solicita creación de
hasta 3 héroes. El jugador selecciona clase y nombre para cada héroe. El
sistema crea los héroes mediante Factory Method. El sistema genera la
mazmorra usando Builder + Abstract Factory. El sistema cambia al estado
Exploración. Flujo Alternativo: 3a. El jugador cancela creación → el
sistema retorna al Menú Principal. Poscondiciones: Equipo creado
Mazmorra generada Estado cambiado a Exploración Caso de Uso 2 Nombre:
Ejecutar Combate Autor: Andrés Fecha: 27/02/2026 Descripción: Permite
ejecutar un combate táctico por turnos contra enemigos. Actores: Jugador
Sistema Precondiciones: El jugador se encuentra en una habitación con
enemigos Flujo Normal: El sistema cambia a estado Combate. El sistema
determina orden de turnos. Si es turno del jugador: Selecciona acción Se
crea Command correspondiente Se ejecuta acción Si es turno del enemigo:
Se consulta Strategy de IA Se ejecuta acción Se verifica condición de
victoria/derrota. Si victoria → otorgar recompensas y volver a
Exploración. Flujo Alternativo: 3a. El jugador usa ítem → se aplica
efecto y se elimina del inventario. 5a. Derrota → cambiar a estado
GameOver. Poscondiciones: Enemigos derrotados o héroes eliminados XP
asignada si hay victoria Caso de Uso 3 Nombre: Gestionar Inventario
Autor: Andrés Fecha: 27/02/2026 Descripción: Permite al jugador
visualizar y modificar el inventario. Actores: Jugador Precondiciones:
No estar en combate Flujo Normal: El jugador abre inventario. El sistema
cambia a estado Inventario. El sistema muestra estructura Composite. El
jugador puede equipar, usar o mover ítems. El jugador cierra inventario.
Flujo Alternativo: 4a. Ítem no compatible → el sistema muestra mensaje
de error. Poscondiciones: Inventario actualizado Estado anterior
restaurado Caso de Uso 4 Nombre: Guardar Partida Autor: Andrés Fecha:
27/02/2026 Descripción: Permite almacenar el estado actual del juego.
Actores: Jugador Precondiciones: No estar en combate Existencia de slot
disponible Flujo Normal: El jugador selecciona "Guardar". El sistema
solicita slot. El jugador confirma. El sistema crea Memento del estado.
El sistema serializa datos a archivo. El sistema confirma éxito. Flujo
Alternativo: 3a. Slot ocupado → solicitar confirmación de sobrescritura.
Poscondiciones: Estado persistido correctamente \# 5. Diagrama de Clases
(Estructura Base) Aquí está el esqueleto arquitectónico limpio que debes
modelar en UML: Núcleo Dominio Character (abstract) name hp attack
defense speed takeDamage() performAction() Hero extends Character Enemy
extends Character Factory Method HeroFactory createHero(type)
ConcreteHeroFactory Strategy AIStrategy decideAction()
AggressiveStrategy DefensiveStrategy IntelligentStrategy Enemy
AIStrategy strategy Command Command execute() undo() MoveCommand
AttackCommand SkillCommand UseItemCommand Composite Item (interface)
getWeight() SimpleItem ContainerItem List`<Item>`{=html} children State
GameState handleInput() MenuState ExplorationState CombatState
InventoryState GameOverState Builder DungeonBuilder buildRooms()
connectRooms() placeEnemies() ConcreteDungeonBuilder Abstract Factory
DungeonThemeFactory createEnemy() createTreasure() FireThemeFactory
PoisonThemeFactory Memento GameMemento snapshotData GameCaretaker

DIAGRAMA DE CLASES -- Arquitectura Base Completa

DIAGRAMA DE CLASES -- SISTEMA DE COMBATE DETALLADO

DIAGRAMA -- SUBSISTEMA DECORATOR

DIAGRAMA -- Versión Arquitectónicamente Impecable

DIAGRAMA INTEGRADO COMPLETO

DIAGRAMA DE SECUENCIA -- Turno de Ataque

DIAGRAMA DE SECUENCIA -- Procesamiento de Efectos por Turno

DIAGRAMA DE ESTADOS -- Combate

DIAGRAMA DE ACTIVIDADES -- Flujo de Turno

DIAGRAMA DE COMPONENTES -- Arquitectura General

DIAGRAMA ACTUALIZADO -- Motor con Estado Interno

Secuencia Actualizada del Turno Alternado
