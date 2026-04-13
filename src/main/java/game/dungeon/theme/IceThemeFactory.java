package game.dungeon.theme;

import game.balance.GameBalance;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.DragonFactory;
import game.domain.personaje.factory.EnemigoBasicoFactory;
import game.domain.personaje.factory.OrcoFactory;
import game.domain.personaje.factory.PersonajeFactory;
import game.items.model.SimpleItem;

/**
 * Abstract Factory - Implementación para el tema de Hielo
 * 
 * Crea enemigos y tesoros relacionados con frío y hielo.
 */
public class IceThemeFactory implements DungeonThemeFactory {
    
    @Override
    public Personaje crearEnemigoBasico() {
        PersonajeFactory factory = new EnemigoBasicoFactory(28, 7);
        return factory.crearPersonaje("Lobo de Hielo");
    }

    @Override
    public Personaje crearEnemigoMedio() {
        PersonajeFactory factory = new OrcoFactory(58, 13);
        return factory.crearPersonaje("Orco Glacial");
    }

    @Override
    public Personaje crearJefe() {
        GameBalance.BossProfile profile = GameBalance.boss("ice");
        PersonajeFactory factory = new DragonFactory(profile.hp(), profile.attack());
        return factory.crearPersonaje(profile.name());
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
