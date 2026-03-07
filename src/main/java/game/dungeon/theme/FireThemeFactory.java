package game.dungeon.theme;

import game.domain.personaje.Dragon;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Orco;
import game.domain.personaje.Personaje;
import game.items.model.SimpleItem;

/**
 * Abstract Factory - Implementación para el tema de Fuego
 * 
 * Crea enemigos y tesoros relacionados con fuego y volcanes.
 */
public class FireThemeFactory implements DungeonThemeFactory {
    
    @Override
    public Personaje crearEnemigoBasico() {
        return new EnemigoBasico("Salamandra de Fuego", 30, 6);
    }

    @Override
    public Personaje crearEnemigoMedio() {
        return new Orco("Orco Flamígero", 60, 12);
    }

    @Override
    public Personaje crearJefe() {
        return new Dragon("Dragón de Fuego Ancestral", 150, 25);
    }

    @Override
    public SimpleItem crearTesoroComun() {
        return new SimpleItem("Gema de Fuego", 
            "Una gema que brilla con luz ardiente", 
            "Gema", 
            50,
            1);
    }

    @Override
    public SimpleItem crearTesoroRaro() {
        return new SimpleItem("Espada Flamígera", 
            "Espada legendaria envuelta en llamas eternas", 
            "Arma", 
            200,
            5);
    }

    @Override
    public String getNombreTema() {
        return "Fuego";
    }
}
