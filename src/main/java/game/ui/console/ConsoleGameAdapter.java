package game.ui.console;

import com.google.gson.JsonObject;
import game.application.dto.UiCommand;
import game.application.ports.GameInputPort;
import game.application.ports.GameOutputPort;
import game.application.runtime.GameRuntime;
import game.ui.GameViewModel;

import java.util.List;

/**
 * Adaptador de consola para ejecutar el runtime unico de juego.
 */
public class ConsoleGameAdapter {

    private final GameRuntime runtime;
    private final GameInputPort input;
    private final GameOutputPort output;
    private boolean running;

    public ConsoleGameAdapter(GameRuntime runtime, GameInputPort input, GameOutputPort output) {
        this.runtime = runtime;
        this.input = input;
        this.output = output;
        this.running = true;
    }

    public void run() {
        output.printLine("=== DUNGEON CRAWLER - CONSOLA (RUNTIME UNICO) ===");
        output.printLine("Comandos de estado enviados al backend GameRuntime.");

        while (running) {
            GameViewModel vm = runtime.presentViewModel();
            render(vm);
            renderMenu(vm);

            String rawOption = input.readLine();
            if (rawOption == null) {
                running = false;
                break;
            }

            String option = rawOption.trim();
            try {
                handleOption(vm, option);
            } catch (RuntimeException ex) {
                runtime.registrarMensajeSistema(ex.getMessage());
                output.printLine("[ERROR] " + ex.getMessage());
            }
        }

        output.printLine("Sesion de consola finalizada.");
    }

    private void render(GameViewModel vm) {
        output.printLine("");
        output.printLine("Pantalla: " + vm.screen
            + " | Sala " + vm.room + "/" + vm.totalRooms
            + " | HP " + vm.playerHp + "/" + vm.playerHpMax
            + " | Oro " + vm.gold);

        if ("combat".equals(vm.screen) && vm.enemy != null) {
            output.printLine("Enemigo: " + vm.enemy.name + " HP " + vm.enemy.hp + "/" + vm.enemy.hpMax);
            printLastLines("Log combate", vm.combatLog, 3);
        } else {
            if (vm.roomName != null) {
                output.printLine("Sala actual: " + vm.roomName);
            }
            printLastLines("Eventos", vm.eventLog, 3);
        }

        if ("inventory".equals(vm.screen) && vm.inventory != null) {
            output.printLine("Inventario: " + vm.inventory.itemCount + "/" + vm.inventory.maxCapacity);
            if (vm.selectedItem != null) {
                output.printLine("Seleccionado: " + vm.selectedItem.name + " (" + vm.selectedItem.type + ")");
            }
        }
    }

    private void renderMenu(GameViewModel vm) {
        output.printLine("");
        if ("combat".equals(vm.screen)) {
            output.printLine("1) Atacar  2) Defender  3) Usar item  4) Habilidad  5) Inventario  0) Salir");
            output.printLine("> ");
            return;
        }

        if ("inventory".equals(vm.screen)) {
            output.printLine("1) Arriba  2) Abajo  3) Usar item  4) Volver  5) Guardar  6) Cargar  0) Salir");
            output.printLine("> ");
            return;
        }

        output.printLine("1) Avanzar  2) Explorar  3) Inventario  4) Guardar  5) Cargar  6) Forzar combate  0) Salir");
        output.printLine("> ");
    }

    private void handleOption(GameViewModel vm, String option) {
        if ("0".equals(option)) {
            running = false;
            return;
        }

        if ("combat".equals(vm.screen)) {
            handleCombatOption(vm, option);
            return;
        }

        if ("inventory".equals(vm.screen)) {
            handleInventoryOption(vm, option);
            return;
        }

        handleExplorationOption(option);
    }

    private void handleCombatOption(GameViewModel vm, String option) {
        switch (option) {
            case "1" -> send("attack", payload -> payload.addProperty("targetId", "current"));
            case "2" -> send("defend", null);
            case "3" -> sendUseSelectedItem(vm);
            case "4" -> send("useSkill", payload -> payload.addProperty("skill", "Golpe Especial"));
            case "5" -> send("openInventory", null);
            default -> output.printLine("Opcion no valida.");
        }
    }

    private void handleInventoryOption(GameViewModel vm, String option) {
        switch (option) {
            case "1" -> send("inventoryUp", null);
            case "2" -> send("inventoryDown", null);
            case "3" -> sendUseSelectedItem(vm);
            case "4" -> send("closeInventory", null);
            case "5" -> sendWithSlot("saveGame");
            case "6" -> sendWithSlot("loadGame");
            default -> output.printLine("Opcion no valida.");
        }
    }

    private void handleExplorationOption(String option) {
        switch (option) {
            case "1" -> send("advanceRoom", null);
            case "2" -> send("searchTreasure", null);
            case "3" -> send("openInventory", null);
            case "4" -> sendWithSlot("saveGame");
            case "5" -> sendWithSlot("loadGame");
            case "6" -> send("forceCombat", null);
            default -> output.printLine("Opcion no valida.");
        }
    }

    private void sendUseSelectedItem(GameViewModel vm) {
        Integer selected = vm.selectedItemIndex;
        if (selected == null) {
            runtime.registrarMensajeSistema("No hay item seleccionado para usar.");
            output.printLine("No hay item seleccionado para usar.");
            return;
        }

        send("useItem", payload -> payload.addProperty("itemIndex", selected));
    }

    private void sendWithSlot(String action) {
        output.printLine("Slot (1-3): ");
        String slotText = input.readLine();
        int slot = parseSlot(slotText);
        send(action, payload -> payload.addProperty("slot", slot));
    }

    private int parseSlot(String slotText) {
        if (slotText == null) {
            return 1;
        }
        try {
            int value = Integer.parseInt(slotText.trim());
            if (value >= 1 && value <= 3) {
                return value;
            }
        } catch (NumberFormatException ignored) {
            // fallback
        }
        return 1;
    }

    private void send(String action, java.util.function.Consumer<JsonObject> payloadBuilder) {
        UiCommand command = new UiCommand();
        command.action = action;
        command.payload = new JsonObject();
        if (payloadBuilder != null) {
            payloadBuilder.accept(command.payload);
        }
        runtime.handleCommand(command);
    }

    private void printLastLines(String title, List<String> lines, int count) {
        if (lines == null || lines.isEmpty()) {
            return;
        }
        output.printLine(title + ":");
        int from = Math.max(0, lines.size() - Math.max(1, count));
        for (int i = from; i < lines.size(); i++) {
            output.printLine("- " + lines.get(i));
        }
    }
}