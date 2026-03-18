package game.state.game;

/**
 * State concreto - Combate
 */
public class CombatState implements GameState {
    private final GameStateContext contexto;
    private final GameState estadoAnterior;
    private int turnos;
    private boolean combateActivo;
    
    public CombatState(GameStateContext contexto, GameState estadoAnterior) {
        this.contexto = contexto;
        this.estadoAnterior = estadoAnterior;
        this.turnos = 0;
        this.combateActivo = true;
    }
    
    @Override
    public void manejarEntrada(String entrada) {
        if (!combateActivo) {
            System.out.println("Presiona cualquier tecla para continuar...");
            contexto.cambiarEstado(estadoAnterior);
            return;
        }
        
        switch (entrada.toLowerCase()) {
            case "a", "atacar" -> {
                System.out.println("¡Atacas al enemigo!");
                turnos++;
                
                // Simular combate simple
                if (Math.random() < 0.4) {
                    System.out.println("¡Has derrotado al enemigo!");
                    combateActivo = false;
                } else {
                    System.out.println("El enemigo contraataca...");
                }
            }
            case "d", "defender" -> {
                System.out.println("Te pones en guardia...");
                turnos++;
            }
            case "h", "huir" -> {
                System.out.println("¡Huyes del combate!");
                contexto.cambiarEstado(estadoAnterior);
            }
            default -> System.out.println("Acción no válida en combate.");
        }
    }
    
    @Override
    public void actualizar() {
        // Lógica de combate automática si es necesario
    }
    
    @Override
    public void render() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("       ⚔️  COMBATE - Turno " + turnos);
        System.out.println("=".repeat(40));
        
        if (combateActivo) {
            System.out.println("¡Te has encontrado con un enemigo!");
            System.out.println();
            System.out.println("Tu HP: 100/100");
            System.out.println("Enemigo HP: 50/50");
            System.out.println();
            System.out.println("Acciones:");
            System.out.println("  A - Atacar");
            System.out.println("  D - Defender");
            System.out.println("  H - Huir");
        } else {
            System.out.println("¡Victoria!");
            System.out.println("Has ganado experiencia y objetos.");
        }
        
        System.out.println("=".repeat(40));
        System.out.print("> ");
    }
    
    @Override
    public void onEnter() {
        System.out.println("[State] ¡Iniciando combate!");
    }
    
    @Override
    public void onExit() {
        System.out.println("[State] Combate finalizado");
    }
    
    @Override
    public String getNombre() {
        return "Combat";
    }
    
    public int getTurnos() {
        return turnos;
    }
}
