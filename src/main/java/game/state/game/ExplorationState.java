package game.state.game;

/**
 * State concreto - Exploración de mazmorra
 */
public class ExplorationState implements GameState {
    private final GameStateContext contexto;
    private int salaActual;
    
    public ExplorationState(GameStateContext contexto) {
        this.contexto = contexto;
        this.salaActual = 1;
    }
    
    @Override
    public void manejarEntrada(String entrada) {
        switch (entrada.toLowerCase()) {
            case "n", "norte" -> {
                System.out.println("Avanzas hacia el norte...");
                salaActual++;
                if (Math.random() < 0.3) {
                    // 30% de probabilidad de combate
                    contexto.cambiarEstado(new CombatState(contexto, this));
                }
            }
            case "i", "inventario" -> {
                contexto.cambiarEstado(new InventoryState(contexto, this));
            }
            case "m", "menu" -> {
                contexto.cambiarEstado(new MenuState(contexto));
            }
            default -> System.out.println("Comando no reconocido.");
        }
    }
    
    @Override
    public void actualizar() {
        // Lógica de exploración
    }
    
    @Override
    public void render() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("       EXPLORACIÓN - Sala " + salaActual);
        System.out.println("=".repeat(40));
        System.out.println("Estás explorando la mazmorra...");
        System.out.println();
        System.out.println("Comandos:");
        System.out.println("  N - Avanzar al norte");
        System.out.println("  I - Abrir inventario");
        System.out.println("  M - Menú principal");
        System.out.println("=".repeat(40));
        System.out.print("> ");
    }
    
    @Override
    public void onEnter() {
        System.out.println("[State] Iniciando exploración");
    }
    
    @Override
    public void onExit() {
        System.out.println("[State] Saliendo de exploración");
    }
    
    @Override
    public String getNombre() {
        return "Exploration";
    }
    
    public int getSalaActual() {
        return salaActual;
    }
}
