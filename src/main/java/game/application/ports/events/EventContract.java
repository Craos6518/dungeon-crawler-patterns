package game.application.ports.events;

/**
 * Contrato oficial de claves de eventos del sistema.
 * 
 * Define qué claves DEBEN estar presentes en cada tipo de evento
 * para mantener consistencia entre todos los emisores.
 * 
 * Esto previene NPE y facilita el debugging.
 */
public final class EventContract {
    
    // Prevenir instanciación
    private EventContract() {}
    
    // ==================== EVENTOS DE COMBATE ====================
    
    /**
     * COMBATE_INICIADO
     * Emitido cuando comienza un combate
     * 
     * Claves requeridas:
        * - heroe: Clase del heroe (String)
     * - enemigo: Nombre del enemigo (String)
     * - vidaHeroe: Vida actual del héroe (Integer)
     * - vidaEnemigo: Vida actual del enemigo (Integer)
     * - estrategia: Estrategia de IA inicial (String)
     */
    public static class CombateIniciado {
        public static final String HEROE = "heroe";
        public static final String ENEMIGO = "enemigo";
        public static final String SALA = "sala";
        public static final String ATACANTE = "atacante";
        public static final String DEFENSOR = "defensor";
        public static final String VIDA_HEROE = "vidaHeroe";
        public static final String VIDA_ENEMIGO = "vidaEnemigo";
        public static final String ESTRATEGIA = "estrategia";
    }
    
    /**
     * ATAQUE_REALIZADO
     * Emitido cuando un personaje ataca a otro
     * 
     * Claves requeridas:
     * - atacante: Nombre del atacante (String)
     * - defensor: Nombre del defensor (String)
     * - danio: Daño aplicado (Integer)
     * - vidaRestante: Vida actual del defensor tras el ataque (Integer)
     * - ronda: Número de ronda actual (Integer)
     */
    public static class AtaqueRealizado {
        public static final String ATACANTE = "atacante";
        public static final String DEFENSOR = "defensor";
        public static final String DANIO = "danio";
        public static final String VIDA_RESTANTE = "vidaRestante";
        public static final String RONDA = "ronda";
    }
    
    /**
     * ACCION_REALIZADA
     * Emitido cuando se realiza una acción general (defender, usar habilidad, etc)
     * 
     * Claves requeridas:
     * - personaje: Nombre del personaje (String)
     * - accion: Tipo de acción (String: "defender", "habilidad", etc)
     * - ronda: Número de ronda actual (Integer)
     * 
     * Claves opcionales:
     * - nombre: Nombre de la acción/habilidad specific (String)
     */
    public static class AccionRealizada {
        public static final String PERSONAJE = "personaje";
        public static final String ACCION = "accion";
        public static final String RONDA = "ronda";
        public static final String NOMBRE = "nombre"; // Opcional
        public static final String RESULTADO = "resultado"; // Opcional
        public static final String ENEMIGO = "enemigo"; // Opcional
    }
    
    /**
     * EFECTO_APLICADO
     * Emitido cuando se aplica un efecto a un personaje (veneno, buff, etc)
     * 
     * Claves requeridas:
     * - personaje: Nombre del personaje afectado (String)
     * - efecto: Nombre del efecto en MAYUSCULA_SNAKE_CASE (String: "VENENO", "CURACION", etc)
     * - duracion: Duración en turnos (Integer)
     */
    public static class EfectoAplicado {
        public static final String PERSONAJE = "personaje";
        public static final String EFECTO = "efecto";
        public static final String DURACION = "duracion";
        public static final String ACUMULACIONES = "acumulaciones"; // Opcional
    }
    
    /**
     * ESTADO_CAMBIADO (sub-categorías)
     * Emitido cuando cambia el estado interno de la IA o del sistema
     * 
     * Para IA: tipo="estrategia"
     * - nuevaEstrategia: Nombre de la estrategia (String)
     * 
     * Para Sistema: tipo="sistema"
     * - nuevoEstado: Nuevo estado (String: "MenuPrincipal", "Exploracion", etc)
     * 
     * Para GameFlow: tipo="flujo"
     * - estado: Nombre del estado de flujo (String)
     */
    public static class EstadoCambiado {
        public static final String TIPO = "tipo";
        
        // Sub-tipo: estrategia
        public static final String TIPO_ESTRATEGIA = "estrategia";
        public static final String NUEVA_ESTRATEGIA = "nuevaEstrategia";
        
        // Sub-tipo: sistema
        public static final String TIPO_SISTEMA = "sistema";
        public static final String NUEVO_ESTADO = "nuevoEstado";
        
        // Sub-tipo: flujo
        public static final String TIPO_FLUJO = "flujo";
        public static final String ESTADO = "estado";
    }
    
    /**
     * COMBATE_FINALIZADO
     * Emitido cuando termina un combate
     * 
     * Claves requeridas:
     * - ganador: Nombre del ganador (String)
     * - vencido: Nombre del vencido (String)
     * - rondas: Total de rondas jugadas (Integer)
     */
    public static class Combatefinalizado {
        public static final String GANADOR = "ganador";
        public static final String VENCIDO = "vencido";
        public static final String RONDAS = "rondas";
        public static final String ENEMIGOS_DERROTADOS = "enemigosDerrotados"; // Opcional
    }

    public static class ItemRecogido {
        public static final String ITEM = "item";
        public static final String ORIGEN = "origen"; // Opcional
    }

    public static class ItemUsado {
        public static final String USUARIO = "usuario";
        public static final String ITEM = "item";
    }

    public static class TesoroEncontrado {
        public static final String ITEM = "item";
        public static final String ORO = "oro";
    }

    public static class SalaEntrar {
        public static final String SALA = "sala";
        public static final String NOMBRE = "nombre";
    }

    public static class SalaCompletada {
        public static final String RESULTADO = "resultado";
        public static final String MAZMORRA = "mazmorra";
        public static final String TEMA = "tema";
        public static final String SALAS = "salas";
    }
    
    // ==================== EVENTOS DE JUEGO ====================
    
    /**
     * JUEGO_INICIADO
     * 
     * Claves requeridas:
        * - heroe: Clase del heroe (String)
     * - tema: Tema de la mazmorra (String)
     */
    public static class JuegoIniciado {
        public static final String HEROE = "heroe";
        public static final String TEMA = "tema";
    }
    
    /**
     * JUEGO_GUARDADO
     * 
     * Claves requeridas:
     * - tipo: "manual" o "checkpoint-auto" (String)
     * - archivo: Nombre del archivo (String)
     */
    public static class JuegoGuardado {
        public static final String TIPO = "tipo";
        public static final String ARCHIVO = "archivo";
        public static final String SALA = "sala"; // Opcional
    }
    
    /**
     * JUEGO_CARGADO
     * 
     * Claves requeridas:
     * - jugador: Nombre del jugador (String)
     * - sala: Número de sala (Integer)
     * - tema: Tema de la mazmorra (String)
     */
    public static class JuegoCargado {
        public static final String ARCHIVO = "archivo";
        public static final String JUGADOR = "jugador";
        public static final String SALA = "sala";
        public static final String TEMA = "tema";
    }
    
    /**
     * JUEGO_TERMINADO
     * 
     * Claves requeridas:
     * - resultado: "Victoria" o "Derrota" (String)
     */
    public static class JuegoTerminado {
        public static final String RESULTADO = "resultado";
    }
}
