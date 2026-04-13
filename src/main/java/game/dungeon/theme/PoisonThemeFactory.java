package game.dungeon.theme;

import game.balance.GameBalance;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.DragonFactory;
import game.domain.personaje.factory.EnemigoBasicoFactory;
import game.domain.personaje.factory.OrcoFactory;
import game.domain.personaje.factory.PersonajeFactory;
import game.items.model.SimpleItem;

/**
 * Abstract Factory - Implementación para el tema de Veneno
 * 
 * Crea enemigos y tesoros relacionados con veneno y toxinas.
 */
public class PoisonThemeFactory implements DungeonThemeFactory {
    
    @Override
    public Personaje crearEnemigoBasico() {
        PersonajeFactory factory = new EnemigoBasicoFactory(25, 5);
        return factory.crearPersonaje("Araña Venenosa");
    }

    @Override
    public Personaje crearEnemigoMedio() {
        PersonajeFactory factory = new OrcoFactory(55, 11);
        return factory.crearPersonaje("Orco Putrefacto");
    }

    @Override
    public Personaje crearJefe() {
        GameBalance.BossProfile profile = GameBalance.boss("poison");
        PersonajeFactory factory = new DragonFactory(profile.hp(), profile.attack());
        return factory.crearPersonaje(profile.name());
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
