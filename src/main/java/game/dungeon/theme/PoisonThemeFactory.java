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
        Personaje p = new EnemigoBasicoFactory(25, 5).crearPersonaje("Araña Venenosa");
        p.setResistenciaVeneno(20);
        return p;
    }

    @Override
    public Personaje crearEnemigoMedio() {
        Personaje p = new OrcoFactory(55, 11).crearPersonaje("Orco Putrefacto");
        p.setResistenciaVeneno(40);
        return p;
    }

    @Override
    public Personaje crearJefe() {
        GameBalance.BossProfile profile = GameBalance.boss("poison");
        Personaje p = new DragonFactory(profile.hp(), profile.attack()).crearPersonaje(profile.name());
        p.setResistenciaVeneno(100);
        return p;
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
