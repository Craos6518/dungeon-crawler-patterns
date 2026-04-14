package game.state.game;

/**
 * State concreto - Gestión de inventario
 */
public class InventoryState implements GameState {
    private final GameStateContext contexto;
    private final GameState estadoAnterior;
    
    public InventoryState(GameStateContext contexto, GameState estadoAnterior) {
        this.contexto = contexto;
        this.estadoAnterior = estadoAnterior;
    }
    
    @Override
    public void manejarEntrada(String entrada) {
        String accion = entrada == null ? "" : entrada.trim().toLowerCase();
        if (!permiteAccion(accion)) {
            System.out.println("Accion no valida en Inventory.");
            return;
        }

        switch (accion) {
            case "1" -> System.out.println("Usaste una poción de vida");
            case "2" -> System.out.println("Equipaste la espada");
            case "e", "salir" -> contexto.transitionTo(estadoAnterior);
            default -> System.out.println("Opción no válida.");
        }
    }
    
    @Override
    public void actualizar() {
        // El inventario no necesita actualización
    }
    
    @Override
    public void render() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("       🎒 INVENTARIO");
        System.out.println("=".repeat(40));
        System.out.println("Tus objetos:");
        System.out.println("  1. Poción de Vida x3");
        System.out.println("  2. Espada de Hierro");
        System.out.println("  3. Escudo de Madera");
        System.out.println();
        System.out.println("  E - Cerrar inventario");
        System.out.println("=".repeat(40));
        System.out.print("> ");
    }
    
    @Override
    public void onEnter() {
        System.out.println("[State] Abriendo inventario");
    }
    
    @Override
    public void onExit() {
        System.out.println("[State] Cerrando inventario");
    }
    
    @Override
    public String getNombre() {
        return "Inventory";
    }

    @Override
    public boolean permiteAccion(String accion) {
        if (accion == null || accion.isBlank()) {
            return false;
        }
        return "1".equals(accion)
            || "2".equals(accion)
            || "e".equals(accion)
            || "salir".equals(accion);
    }

    @Override
    public boolean permiteTransicionA(String nombreEstadoDestino) {
        if (nombreEstadoDestino == null || nombreEstadoDestino.isBlank()) {
            return false;
        }
        return "Inventory".equals(nombreEstadoDestino)
            || "Exploration".equals(nombreEstadoDestino)
            || "Combat".equals(nombreEstadoDestino)
            || "Menu".equals(nombreEstadoDestino);
    }
}
