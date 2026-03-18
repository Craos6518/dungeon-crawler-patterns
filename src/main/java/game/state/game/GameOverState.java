package game.state.game;

/**
 * State concreto - Game Over
 */
public class GameOverState implements GameState {
    private final GameStateContext contexto;
    private final boolean victoria;
    
    public GameOverState(GameStateContext contexto, boolean victoria) {
        this.contexto = contexto;
        this.victoria = victoria;
    }
    
    @Override
    public void manejarEntrada(String entrada) {
        switch (entrada.toLowerCase()) {
            case "1", "reintentar" -> {
                System.out.println("Reiniciando...");
                contexto.cambiarEstado(new MenuState(contexto));
            }
            case "2", "menu" -> contexto.cambiarEstado(new MenuState(contexto));
            case "3", "salir" -> contexto.detener();
            default -> System.out.println("Opción no válida.");
        }
    }
    
    @Override
    public void actualizar() {
        // Game Over es un estado terminal que no se actualiza
    }
    
    @Override
    public void render() {
        System.out.println("\n" + "=".repeat(40));
        
        if (victoria) {
            System.out.println("       🎉 ¡VICTORIA! 🎉");
            System.out.println("=".repeat(40));
            System.out.println("¡Has completado la mazmorra!");
        } else {
            System.out.println("       ☠️  GAME OVER ☠️");
            System.out.println("=".repeat(40));
            System.out.println("Has sido derrotado...");
        }
        
        System.out.println();
        System.out.println("1. Reintentar");
        System.out.println("2. Menú Principal");
        System.out.println("3. Salir");
        System.out.println("=".repeat(40));
        System.out.print("> ");
    }
    
    @Override
    public void onEnter() {
        if (victoria) {
            System.out.println("[State] ¡Victoria alcanzada!");
        } else {
            System.out.println("[State] Game Over");
        }
    }
    
    @Override
    public void onExit() {
        System.out.println("[State] Saliendo de Game Over");
    }
    
    @Override
    public String getNombre() {
        return victoria ? "Victory" : "GameOver";
    }
    
    public boolean isVictoria() {
        return victoria;
    }
}
