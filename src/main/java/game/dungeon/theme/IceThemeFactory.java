package game.dungeon.theme;

import game.domain.personaje.Dragon;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Orco;
import game.domain.personaje.Personaje;
import game.items.model.SimpleItem;

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
        return new Dragon("Kryovaleth", 140, 24);
    }

    @Override
    public SimpleItem crearTesoroComun() {
        return new SimpleItem("Cristal de Hielo", 
            "Cristal helado que nunca se derrite", 
            "Gema", 
            60,
            1);
    }

    @Override
    public SimpleItem crearTesoroRaro() {
        return new SimpleItem("Báculo del Invierno", 
            "Báculo que congela todo a su paso", 
            "Arma", 
            220,
            4);
    }

    @Override
    public String getNombreTema() {
        return "Hielo";
    }
}
