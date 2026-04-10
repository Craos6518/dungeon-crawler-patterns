package game.application.ports.events;

/**
 * Tipos de eventos del juego
 */
public enum EventType {
    // Eventos de combate
    COMBATE_INICIADO,
    ATAQUE_REALIZADO,
    DANIO_RECIBIDO,
    PERSONAJE_MUERTO,
    COMBATE_FINALIZADO,
    TURNO_CAMBIADO,
    
    // Eventos de items
    ITEM_RECOGIDO,
    ITEM_USADO,
    ITEM_EQUIPADO,
    
    // Eventos de mazmorra
    SALA_ENTRAR,
    SALA_COMPLETADA,
    TESORO_ENCONTRADO,
    TRAMPA_ACTIVADA,
    
    // Eventos de sistema
    JUEGO_INICIADO,
    JUEGO_PAUSADO,
    JUEGO_GUARDADO,
    JUEGO_CARGADO,
    JUEGO_TERMINADO,
    
    // Eventos adicionales para integración
    ESTADO_CAMBIADO,
    EFECTO_APLICADO,
    ACCION_REALIZADA
}
