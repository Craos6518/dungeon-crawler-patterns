package game.state.domain.exploration;

import game.domain.personaje.Personaje;
import game.dungeon.model.Room;
import game.effects.status.PoisonEffect;
import game.events.observer.EventManager;
import game.events.observer.GameEvent;
import game.events.observer.EventType;
import game.items.model.SimpleItem;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;
import game.persistence.memento.GameOriginator;
import game.state.domain.AbstractDomainGameState;
import game.state.domain.GameSessionData;
import game.command.actions.CommandInvoker;
import java.util.Scanner;

import java.util.Random;

/**
 * Estado de dominio que encapsula toda la lógica de exploración.
 * Responsable de:
 * - Loop de exploración de salas
 * - Búsqueda de tesoros
 * - Encuentro de enemigos
 * - Gestión de inventario durante exploración
 * - Guardado de partida
 * 
 * Completamente independiente y reutilizable en diferentes interfaces.
 */
public class ExplorationDomainState extends AbstractDomainGameState {
    
    private final GameSessionData sessionData;
    private final Random random;
    private final CombatCallback combatCallback;
    private final VictoryCallback victoryCallback;
    
    /**
     * Callback para iniciar combate cuando se encuentra un enemigo
     */
    public interface CombatCallback {
        void iniciarCombate(Personaje enemigo, boolean esJefe);
    }
    
    /**
     * Callback para cuando se alcanza victoria
     */
    public interface VictoryCallback {
        void alcanzarVictoria();
    }
    
    public ExplorationDomainState(
        GameSessionData sessionData,
        Random random,
        CombatCallback combatCallback,
        VictoryCallback victoryCallback
    ) {
        this.sessionData = sessionData;
        this.random = random;
        this.combatCallback = combatCallback;
        this.victoryCallback = victoryCallback;
    }
    
    @Override
    public boolean ejecutar() {
        return explorarMazmorra();
    }
    
    @Override
    public String getNombreEstado() {
        return "Exploración";
    }
    
    /**
     * Loop principal de exploración
     * @return false si el usuario quiso volver al menú, true si llegó al final
     */
    private boolean explorarMazmorra() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🗺️  EXPLORANDO: " + sessionData.getMazmorra().getNombre());
        System.out.println("=".repeat(60));

        while (sessionData.getHeroe().estaVivo() && 
               sessionData.getSalaActual() < sessionData.getMazmorra().getSalas().size()) {
            
            Room sala = sessionData.getMazmorra().getSalas().get(sessionData.getSalaActual());

            mostrarMapaMazmorra();
            mostrarHudExploracion();

            System.out.println("\n📍 Sala " + (sessionData.getSalaActual() + 1) + "/" 
                + sessionData.getMazmorra().getSalas().size() + ": " + sala.getNombre());
            System.out.println("   " + sala.getDescripcion());
            System.out.println("   Dificultad: " + sala.getDificultad());

            System.out.println("\n¿Qué deseas hacer?");
            System.out.println("1. [→] Avanzar a la siguiente sala");
            System.out.println("2. [E] Explorar sala / Buscar tesoro");
            System.out.println("3. [I] Abrir inventario");
            System.out.println("4. [G] Guardar partida");
            System.out.println("5. [C] Forzar combate (si hay enemigo)");
            System.out.println("6. Volver al menú principal");

            int opcion = leerOpcion(1, 6);

            switch (opcion) {
                case 1 -> avanzarSala();
                case 2 -> buscarTesoro();
                case 3 -> abrirInventario();
                case 4 -> guardarPartida();
                case 5 -> encontrarEnemigo();
                case 6 -> {
                    return false; // Volver al menú
                }
            }
        }

        // Determinación de victoria/derrota
        if (sessionData.getHeroe().estaVivo()) {
            victoryCallback.alcanzarVictoria();
            return true;
        } else {
            // La derrota se maneja en el combate
            return false;
        }
    }

    private void avanzarSala() {
        sessionData.setSalaActual(sessionData.getSalaActual() + 1);

        if (sessionData.getSalaActual() >= sessionData.getMazmorra().getSalas().size()) {
            System.out.println("\n🎉 ¡Has llegado al final de la mazmorra!");
            return;
        }

        // 70% probabilidad de encontrar enemigo al avanzar
        if (random.nextInt(100) < 70) {
            encontrarEnemigo();
        } else {
            System.out.println("\n✅ Sala despejada. Puedes continuar.");
        }
    }

    private void buscarTesoro() {
        System.out.println("\n🔍 Buscando tesoro...");

        int probabilidad = random.nextInt(100);
        SimpleItem tesoro;

        if (probabilidad < 30) {
            tesoro = sessionData.getTemaActual().crearTesoroRaro();
            System.out.println("✨ ¡Tesoro RARO encontrado!");
        } else if (probabilidad < 70) {
            tesoro = sessionData.getTemaActual().crearTesoroComun();
            System.out.println("💰 Tesoro común encontrado.");
        } else {
            System.out.println("❌ No encontraste nada.");
            return;
        }

        System.out.println("   → " + tesoro.getNombre() + " (Valor: " + tesoro.getValorTotal() + ")");
        sessionData.getInventario().agregar(tesoro);
        sessionData.setOroAcumulado(sessionData.getOroAcumulado() + tesoro.getValorTotal());
        System.out.println("   Agregado al inventario.");
    }

    private void abrirInventario() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎒 INVENTARIO");
        System.out.println("=".repeat(60));

        System.out.println(sessionData.getInventario().mostrarDetalle());
        System.out.println("\nValor total: " + sessionData.getInventario().getValorTotal() + " oro");
        System.out.println("Peso total: " + sessionData.getInventario().getPesoTotal() + " kg");

        System.out.println("\n(Presiona Enter para continuar)");
        esperarEnterSiDisponible();
    }

    private void guardarPartida() {
        System.out.println("\n💾 Guardando partida...");

        // Crear memento usando el método guardarde GameOriginator
        GameMemento memento = originator.guardar();
        caretaker.guardarEnMemoria(memento);

        System.out.print("Nombre del archivo (o Enter para autosave): ");
        String nombreArchivo = leerLineaRequerida().trim();
        if (nombreArchivo.isEmpty()) {
            nombreArchivo = "autosave";
        }

        try {
            caretaker.guardarEnDisco(memento, nombreArchivo);
            System.out.println("✅ Partida guardada: " + nombreArchivo + ".save");
            eventManager.notificar(new GameEvent(EventType.JUEGO_GUARDADO)
                .agregarDato("tipo", "manual")
                .agregarDato("archivo", nombreArchivo));
        } catch (RuntimeException e) {
            System.out.println("❌ Error al guardar partida: " + e.getMessage());
        }
    }

    private void encontrarEnemigo() {
        Personaje enemigo;
        boolean esJefe = sessionData.getSalaActual() == sessionData.getMazmorra().getSalas().size() - 1;

        if (esJefe) {
            enemigo = sessionData.getTemaActual().crearJefe();
            System.out.println("\n⚠️  ¡JEFE FINAL APARECIÓ!");
        } else {
            enemigo = (random.nextInt(100) < 70)
                ? sessionData.getTemaActual().crearEnemigoBasico()
                : sessionData.getTemaActual().crearEnemigoMedio();
            System.out.println("\n⚔️  ¡ENEMIGO APARECIÓ!");
        }

        System.out.println("   " + enemigo.getNombre() + " (HP: " + enemigo.getVida() + ")");

        // 50% probabilidad de envenenamiento si tema es veneno
        if (random.nextInt(100) < 50 && 
            sessionData.getTemaActual().getNombreTema().toLowerCase().contains("veneno")) {
            enemigo = new PoisonEffect(enemigo, 3, 4);
            System.out.println("   ☠️  El enemigo está envenenado!");
        }

        eventManager.notificar(new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("atacante", sessionData.getHeroe().getNombre())
            .agregarDato("defensor", enemigo.getNombre())
            .agregarDato("heroe", sessionData.getHeroe().getNombre())
            .agregarDato("enemigo", enemigo.getNombre()));

        // Delegar al callback de combate
        combatCallback.iniciarCombate(enemigo, esJefe);
    }

    private void mostrarMapaMazmorra() {
        int total = sessionData.getMazmorra().getSalas().size();
        StringBuilder mapa = new StringBuilder();
        mapa.append("\nMapa: ");

        for (int i = 0; i < total; i++) {
            if (i == sessionData.getSalaActual()) {
                mapa.append("[⚔]");
            } else if (i < sessionData.getSalaActual()) {
                mapa.append("[·]");
            } else if (i == total - 1) {
                mapa.append("[💀]");
            } else {
                mapa.append("[?]");
            }

            if (i < total - 1) {
                mapa.append("-");
            }
        }

        System.out.println(mapa);
    }

    private void mostrarHudExploracion() {
        System.out.println("Estado héroe: " + sessionData.getHeroe().getNombre() +
            " | HP: " + sessionData.getHeroe().getVida() +
            " | Oro: " + sessionData.getOroAcumulado() +
            " | Enemigos derrotados: " + sessionData.getEnemigosDerrota());
    }
}
