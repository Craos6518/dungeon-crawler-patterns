package game.dungeon.theme;

import game.balance.GameBalance;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.DragonFactory;
import game.domain.personaje.factory.EnemigoBasicoFactory;
import game.domain.personaje.factory.OrcoFactory;
import game.domain.personaje.factory.PersonajeFactory;
import game.items.model.SimpleItem;

/**
 * Abstract Factory - Implementación para el tema Oscuro
 * 
 * Crea enemigos y tesoros relacionados con oscuridad y sombras.
 */
public class DarkThemeFactory implements DungeonThemeFactory {
    
    @Override
    public Personaje crearEnemigoBasico() {
        Personaje p = new EnemigoBasicoFactory(20, 4).crearPersonaje("Sombra Errante");
        p.setResistenciaOscuridad(20);
        return p;
    }

    @Override
    public Personaje crearEnemigoMedio() {
        Personaje p = new OrcoFactory(70, 15).crearPersonaje("Caballero Oscuro");
        p.setResistenciaOscuridad(40);
        return p;
    }

    @Override
    public Personaje crearJefe() {
        GameBalance.BossProfile profile = GameBalance.boss("dark");
        Personaje p = new DragonFactory(profile.hp(), profile.attack()).crearPersonaje(profile.name());
        p.setResistenciaOscuridad(100);
        return p;
    }

    @Override
    public SimpleItem crearTesoroComun() {
        return new SimpleItem("Runa Oscura", 
            "Símbolo antiguo con poder de las sombras", 
            "Runa", 
            45,
            0);
    }

    @Override
    public SimpleItem crearTesoroRaro() {
        return new SimpleItem("Armadura de las Sombras", 
            "Armadura que absorbe la luz y otorga poder", 
            "Armadura", 
            250,
            8);
    }

    @Override
    public String getNombreTema() {
        return "Oscuridad";
    }
}
