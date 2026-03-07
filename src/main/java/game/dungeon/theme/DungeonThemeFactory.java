package game.dungeon.theme;

import game.domain.personaje.Personaje;
import game.items.model.Item;

/**
 * Abstract Factory Pattern - Interfaz para crear familias de objetos temáticos
 * 
 * Cada implementación crea enemigos, tesoros y elementos coherentes con un tema.
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
     */
    Item crearTesoroComun();
    
    /**
     * Crea un tesoro raro del tema
     */
    Item crearTesoroRaro();
    
    /**
     * Retorna el nombre del tema
     */
    String getNombreTema();
}
