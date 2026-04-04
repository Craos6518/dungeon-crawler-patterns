package game.dungeon.theme;

import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Orco;
import game.domain.personaje.Personaje;
import game.items.model.SimpleItem;

/**
 * Abstract Factory - Implementación para el tema Oscuro
 * 
 * Crea enemigos y tesoros relacionados con oscuridad y sombras.
 */
public class DarkThemeFactory implements DungeonThemeFactory {
    
    @Override
    public Personaje crearEnemigoBasico() {
        return new EnemigoBasico("Sombra Errante", 20, 4);
    }

    @Override
    public Personaje crearEnemigoMedio() {
        return new Orco("Caballero Oscuro", 70, 15);
    }

    @Override
    public Personaje crearJefe() {
        return new Personaje("Malachar", 200) {
            private final int poderOscuro = 35;
            
            @Override
            public game.combat.model.ResultadoAtaque atacar(Personaje objetivo) {
                int danio = poderOscuro;
                objetivo.recibirDanio(danio);
                return new game.combat.model.ResultadoAtaque(
                    getNombre(), objetivo.getNombre(), danio, objetivo.getVida());
            }
        };
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
