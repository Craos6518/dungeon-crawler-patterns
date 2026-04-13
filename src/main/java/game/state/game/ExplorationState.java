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
        String accion = entrada == null ? "" : entrada.trim().toLowerCase();
        if (!permiteAccion(accion)) {
            System.out.println("Accion no valida en Exploration.");
            return;
        }

        switch (accion) {
            case "n", "norte" -> {
                System.out.println("Avanzas hacia el norte...");
                salaActual++;
                if (Math.random() < 0.3) {
                    // 30% de probabilidad de combate
                    contexto.transitionTo(new CombatState(contexto, this));
                }
            }
            case "i", "inventario" -> {
                contexto.transitionTo(new InventoryState(contexto, this));
            }
            case "m", "menu" -> {
                contexto.transitionTo(new MenuState(contexto));
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

    @Override
    public boolean permiteAccion(String accion) {
        if (accion == null || accion.isBlank()) {
            return false;
        }
        return "n".equals(accion)
            || "norte".equals(accion)
            || "i".equals(accion)
            || "inventario".equals(accion)
            || "m".equals(accion)
            || "menu".equals(accion);
    }

    @Override
    public boolean permiteTransicionA(String nombreEstadoDestino) {
        if (nombreEstadoDestino == null || nombreEstadoDestino.isBlank()) {
            return false;
        }
        return "Exploration".equals(nombreEstadoDestino)
            || "Combat".equals(nombreEstadoDestino)
            || "Inventory".equals(nombreEstadoDestino)
            || "Menu".equals(nombreEstadoDestino)
            || "GameOver".equals(nombreEstadoDestino)
            || "Victory".equals(nombreEstadoDestino)
            || "RuntimeMenu".equals(nombreEstadoDestino);
    }
}
