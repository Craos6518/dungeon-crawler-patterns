package game.events.observer;

import java.util.List;
import java.util.Map;

/**
 * Valida el contrato de eventos antes de notificarlos a observers productivos.
 *
 * Responsabilidad:
 * - Exigir claves minimas por tipo de evento para mantener consistencia entre emisores.
 * - Fallar de forma explicita cuando el payload no cumple EventContract.
 *
 * No hace:
 * - notificacion de observers.
 * - transformacion de datos del evento.
 */
public final class EventContractValidator {

    private EventContractValidator() {
    }

    public static void validateOrThrow(GameEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("El evento no puede ser null");
        }

        switch (event.getTipo()) {
            case COMBATE_INICIADO -> validateCombateIniciado(event);
            case ATAQUE_REALIZADO -> requireKeys(event,
                EventContract.AtaqueRealizado.ATACANTE,
                EventContract.AtaqueRealizado.DEFENSOR,
                EventContract.AtaqueRealizado.DANIO
            );
            case ACCION_REALIZADA -> requireKeys(event,
                EventContract.AccionRealizada.PERSONAJE,
                EventContract.AccionRealizada.ACCION
            );
            case EFECTO_APLICADO -> requireKeys(event,
                EventContract.EfectoAplicado.PERSONAJE,
                EventContract.EfectoAplicado.EFECTO
            );
            case ESTADO_CAMBIADO -> validateEstadoCambiado(event);
            case COMBATE_FINALIZADO -> requireKeys(event, EventContract.Combatefinalizado.GANADOR);
            case ITEM_RECOGIDO -> requireKeys(event, EventContract.ItemRecogido.ITEM);
            case ITEM_USADO -> requireKeys(event,
                EventContract.ItemUsado.USUARIO,
                EventContract.ItemUsado.ITEM
            );
            case TESORO_ENCONTRADO -> requireKeys(event,
                EventContract.TesoroEncontrado.ITEM,
                EventContract.TesoroEncontrado.ORO
            );
            case SALA_ENTRAR -> requireKeys(event,
                EventContract.SalaEntrar.SALA,
                EventContract.SalaEntrar.NOMBRE
            );
            case SALA_COMPLETADA -> requireKeys(event,
                EventContract.SalaCompletada.RESULTADO,
                EventContract.SalaCompletada.MAZMORRA,
                EventContract.SalaCompletada.TEMA,
                EventContract.SalaCompletada.SALAS
            );
            case JUEGO_INICIADO -> requireKeys(event,
                EventContract.JuegoIniciado.HEROE,
                EventContract.JuegoIniciado.TEMA
            );
            case JUEGO_GUARDADO -> requireKeys(event, EventContract.JuegoGuardado.ARCHIVO);
            case JUEGO_CARGADO -> validateJuegoCargado(event);
            case JUEGO_TERMINADO -> requireKeys(event, EventContract.JuegoTerminado.RESULTADO);
            default -> {
                // Eventos sin contrato estricto por ahora.
            }
        }
    }

    private static void validateCombateIniciado(GameEvent event) {
        boolean byHeroEnemy = hasKey(event, EventContract.CombateIniciado.HEROE)
            && hasKey(event, EventContract.CombateIniciado.ENEMIGO);
        boolean byAttackerDefender = hasKey(event, EventContract.CombateIniciado.ATACANTE)
            && hasKey(event, EventContract.CombateIniciado.DEFENSOR);

        if (!byHeroEnemy && !byAttackerDefender) {
            throw missingKeys(event, List.of(
                EventContract.CombateIniciado.HEROE + "+" + EventContract.CombateIniciado.ENEMIGO,
                EventContract.CombateIniciado.ATACANTE + "+" + EventContract.CombateIniciado.DEFENSOR
            ));
        }
    }

    private static void validateEstadoCambiado(GameEvent event) {
        if (hasKey(event, EventContract.EstadoCambiado.TIPO)) {
            String tipo = String.valueOf(event.getDato(EventContract.EstadoCambiado.TIPO));
            switch (tipo) {
                case EventContract.EstadoCambiado.TIPO_ESTRATEGIA -> requireKeys(
                    event, EventContract.EstadoCambiado.NUEVA_ESTRATEGIA
                );
                case EventContract.EstadoCambiado.TIPO_SISTEMA -> requireKeys(
                    event, EventContract.EstadoCambiado.NUEVO_ESTADO
                );
                case EventContract.EstadoCambiado.TIPO_FLUJO -> requireKeys(
                    event, EventContract.EstadoCambiado.ESTADO
                );
                default -> throw new IllegalArgumentException(
                    "Evento " + event.getTipo() + " con tipo no soportado: " + tipo
                );
            }
            return;
        }

        // Compatibilidad con emisores activos de checkpoint táctico.
        requireKeys(event, "personaje", EventContract.EstadoCambiado.ESTADO);
    }

    private static void validateJuegoCargado(GameEvent event) {
        boolean byRuntime = hasKey(event, EventContract.JuegoCargado.ARCHIVO)
            && hasKey(event, EventContract.JuegoCargado.SALA);

        boolean byLegacy = hasKey(event, EventContract.JuegoCargado.JUGADOR)
            && hasKey(event, EventContract.JuegoCargado.SALA)
            && hasKey(event, EventContract.JuegoCargado.TEMA);

        if (!byRuntime && !byLegacy) {
            throw missingKeys(event, List.of(
                EventContract.JuegoCargado.ARCHIVO + "+" + EventContract.JuegoCargado.SALA,
                EventContract.JuegoCargado.JUGADOR + "+" + EventContract.JuegoCargado.SALA + "+"
                    + EventContract.JuegoCargado.TEMA
            ));
        }
    }

    private static void requireKeys(GameEvent event, String... keys) {
        for (String key : keys) {
            if (!hasKey(event, key)) {
                throw missingKeys(event, List.of(key));
            }
        }
    }

    private static boolean hasKey(GameEvent event, String key) {
        Map<String, Object> data = event.getDatos();
        if (!data.containsKey(key)) {
            return false;
        }

        Object value = data.get(key);
        if (value == null) {
            return false;
        }

        if (value instanceof String text) {
            return !text.isBlank();
        }

        return true;
    }

    private static IllegalArgumentException missingKeys(GameEvent event, List<String> keys) {
        return new IllegalArgumentException(
            "Evento " + event.getTipo() + " incumple EventContract. Faltan claves requeridas: " + keys
        );
    }
}
