package game.dungeon.theme;

import game.domain.personaje.Dragon;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Orco;
import game.domain.personaje.Personaje;
import game.items.model.Item;

/**
 * Abstract Factory - Implementación para el tema de Hielo
 * 
 * Crea enemigos y tesoros relacionados con frío y hielo.
 */
public class IceThemeFactory implements DungeonThemeFactory {
    
    @Override
    public Personaje crearEnemigoBasico() {
        return new EnemigoBasico("Lobo de Hielo", 28, 7);
    }

    @Override
    public Personaje crearEnemigoMedio() {
        return new Orco("Orco Glacial", 58, 13);
    }

    @Override
    public Personaje crearJefe() {
        return new Dragon("Dragón de Escarcha", 160, 26);
    }

    @Override
    public Item crearTesoroComun() {
        return new Item("Cristal de Hielo", 
            "Cristal helado que nunca se derrite", 
            "Gema", 
            60);
    }

    @Override
    public Item crearTesoroRaro() {
        return new Item("Báculo del Invierno", 
            "Báculo que congela todo a su paso", 
            "Arma", 
            220);
    }

    @Override
    public String getNombreTema() {
        return "Hielo";
    }
}
