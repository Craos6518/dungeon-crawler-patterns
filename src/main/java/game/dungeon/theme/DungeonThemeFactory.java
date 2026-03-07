package game.dungeon.theme;

import game.domain.personaje.Personaje;
import game.items.model.SimpleItem;

/**
 * Abstract Factory Pattern - Interfaz para crear familias de objetos temáticos
 * 
 * Cada implementación crea enemigos, tesoros y elementos coherentes con un tema.
 * Integrado con el patrón Composite: retorna SimpleItem que forma parte del sistema de inventario.
 */
public interface DungeonThemeFactory {
    /**
     * Crea un enemigo básico del tema
     */
    Personaje crearEnemigoBasico();
    
    /**
     * Crea un enemigo de nivel medio del tema
     */
    Personaje crearEnemigoMedio();
    
    /**
     * Crea un jefe del tema
     */
    Personaje crearJefe();
    
    /**
     * Crea un tesoro común del tema
     * Retorna SimpleItem que puede ser agregado a contenedores (patrón Composite)
     */
    SimpleItem crearTesoroComun();
    
    /**
     * Crea un tesoro raro del tema
     * Retorna SimpleItem que puede ser agregado a contenedores (patrón Composite)
     */
    SimpleItem crearTesoroRaro();
    
    /**
     * Retorna el nombre del tema
     */
    String getNombreTema();
}
