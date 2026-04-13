package game.dungeon.theme;

import game.balance.GameBalance;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.DragonFactory;
import game.domain.personaje.factory.EnemigoBasicoFactory;
import game.domain.personaje.factory.OrcoFactory;
import game.domain.personaje.factory.PersonajeFactory;
import game.items.model.SimpleItem;

/**
 * Abstract Factory - Implementación para el tema de Fuego
 * 
 * Crea enemigos y tesoros relacionados con fuego y volcanes.
 */
public class FireThemeFactory implements DungeonThemeFactory {
    
    @Override
    public Personaje crearEnemigoBasico() {
        PersonajeFactory factory = new EnemigoBasicoFactory(30, 6);
        return factory.crearPersonaje("Salamandra de Fuego");
    }

    @Override
    public Personaje crearEnemigoMedio() {
        PersonajeFactory factory = new OrcoFactory(60, 12);
        return factory.crearPersonaje("Orco Flamígero");
    }

    @Override
    public Personaje crearJefe() {
        GameBalance.BossProfile profile = GameBalance.boss("fire");
        PersonajeFactory factory = new DragonFactory(profile.hp(), profile.attack());
        return factory.crearPersonaje(profile.name());
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
