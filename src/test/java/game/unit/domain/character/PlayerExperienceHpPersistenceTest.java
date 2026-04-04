package game.unit.domain.character;

import game.domain.character.Player;
import game.domain.inventory.Inventory;
import game.domain.personaje.factory.GuerreroFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerExperienceHpPersistenceTest {

    @Test
    void gainExperienceDoesNotAutoHealAfterLevelUp() {
        Player player = new Player(new GuerreroFactory(150, 25).crearPersonaje("Heroe"), Inventory.demo());

        player.receiveDamage(40);
        assertEquals(110, player.hp());

        int levels = player.gainExperience(150);

        assertEquals(1, levels);
        assertEquals(110, player.hp());
        assertEquals(170, player.maxHp());
    }
}
