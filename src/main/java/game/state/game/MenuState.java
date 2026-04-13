package game.state.game;

/**
 * State concreto - Menú principal
 */
public class MenuState implements GameState {
    private final GameStateContext contexto;
    
    public MenuState(GameStateContext contexto) {
        this.contexto = contexto;
    }
    
    @Override
    public void manejarEntrada(String entrada) {
        String accion = entrada == null ? "" : entrada.trim().toLowerCase();
        if (!permiteAccion(accion)) {
            System.out.println("Accion no valida en Menu.");
            return;
        }

        switch (accion) {
            case "1", "jugar" -> {
                System.out.println("Iniciando nueva partida...");
                contexto.transitionTo(new ExplorationState(contexto));
            }
            case "2", "cargar" -> {
                System.out.println("Cargando partida...");
                // En un sistema real, cargaría el estado guardado
                contexto.transitionTo(new ExplorationState(contexto));
            }
            case "3", "salir" -> {
                System.out.println("Saliendo del juego...");
                contexto.detener();
            }
            default -> System.out.println("Opción no válida. Intenta de nuevo.");
        }
    }
    
    @Override
    public void actualizar() {
        // El menú no necesita actualización constante
    }
    
    @Override
    public void render() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("       DUNGEON CRAWLER");
        System.out.println("=".repeat(40));
        System.out.println("1. Nueva Partida");
        System.out.println("2. Cargar Partida");
        System.out.println("3. Salir");
        System.out.println("=".repeat(40));
        System.out.print("Selecciona una opción: ");
    }
    
    @Override
    public void onEnter() {
        System.out.println("[State] Entrando al menú principal");
    }
    
    @Override
    public void onExit() {
        System.out.println("[State] Saliendo del menú principal");
    }
    
    @Override
    public String getNombre() {
        return "Menu";
    }

    @Override
    public boolean permiteAccion(String accion) {
        if (accion == null || accion.isBlank()) {
            return false;
        }
        return "1".equals(accion)
            || "2".equals(accion)
            || "3".equals(accion)
            || "jugar".equals(accion)
            || "cargar".equals(accion)
            || "salir".equals(accion);
    }

    @Override
    public boolean permiteTransicionA(String nombreEstadoDestino) {
        if (nombreEstadoDestino == null || nombreEstadoDestino.isBlank()) {
            return false;
        }
        return "Menu".equals(nombreEstadoDestino)
            || "Exploration".equals(nombreEstadoDestino)
            || "RuntimeSetup".equals(nombreEstadoDestino)
            || "RuntimeMenu".equals(nombreEstadoDestino);
    }
}
