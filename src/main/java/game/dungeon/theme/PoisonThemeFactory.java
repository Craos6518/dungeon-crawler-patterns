package game.dungeon.theme;

import game.domain.personaje.Dragon;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Orco;
import game.domain.personaje.Personaje;
import game.items.model.SimpleItem;

/**
 * Abstract Factory - Implementación para el tema de Veneno
 * 
 * Crea enemigos y tesoros relacionados con veneno y toxinas.
 */
public class PoisonThemeFactory implements DungeonThemeFactory {
    
    @Override
    public Personaje crearEnemigoBasico() {
        return new EnemigoBasico("Araña Venenosa", 25, 5);
    }

    @Override
    public Personaje crearEnemigoMedio() {
        return new Orco("Orco Putrefacto", 55, 11);
    }

    @Override
    public Personaje crearJefe() {
        return new Dragon("Arachnovex", 120, 20);
    }

    @Override
    public SimpleItem crearTesoroComun() {
        return new SimpleItem("Vial de Veneno", 
            "Frasco con líquido verde brillante", 
            "Poción", 
            40,
            1);
    }

    @Override
    public SimpleItem crearTesoroRaro() {
        return new SimpleItem("Daga del Asesino", 
            "Daga impregnada con veneno letal", 
            "Arma", 
            180,
            2);
    }

    @Override
    public String getNombreTema() {
        return "Veneno";
    }
}
