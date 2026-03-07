package game.unit.structural;

import game.items.model.ContainerItem;
import game.items.model.ItemComponent;
import game.items.model.SimpleItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test del patrón Composite aplicado al sistema de inventario.
 * Verifica que los items simples y contenedores se comporten correctamente.
 */
class CompositePatternTest {

    @Test
    void simpleItemTieneValorYPesoDirecto() {
        SimpleItem espada = new SimpleItem("Espada de Hierro", "Arma básica", "Arma", 100, 5);

        assertEquals(100, espada.getValorTotal());
        assertEquals(5, espada.getPesoTotal());
        assertEquals("Espada de Hierro", espada.getNombre());
    }

    @Test
    void containerItemCalculaValorYPesoTotalDeContenido() {
        ContainerItem mochila = new ContainerItem("Mochila", "Mochila de aventurero", 10, 2);

        SimpleItem pocion = new SimpleItem("Poción", "Cura 50 HP", "Consumible", 50, 1);
        SimpleItem espada = new SimpleItem("Espada", "Arma básica", "Arma", 100, 5);

        mochila.agregar(pocion);
        mochila.agregar(espada);

        // Peso total = peso propio (2) + poción (1) + espada (5) = 8
        assertEquals(8, mochila.getPesoTotal());

        // Valor total = poción (50) + espada (100) = 150
        assertEquals(150, mochila.getValorTotal());

        assertEquals(2, mochila.getCantidadItems());
    }

    @Test
    void containerItemPuedeContenerOtrosContainers() {
        ContainerItem cofre = new ContainerItem("Cofre", "Cofre del tesoro", 5, 10);
        ContainerItem bolsa = new ContainerItem("Bolsa de oro", "Bolsa pequeña", 3, 1);

        SimpleItem oro = new SimpleItem("Moneda de oro", "Moneda valiosa", "Tesoro", 10, 0);
        bolsa.agregar(oro);
        bolsa.agregar(oro);

        cofre.agregar(bolsa);

        // Peso total: cofre(10) + bolsa(1) + oro(0) + oro(0) = 11
        assertEquals(11, cofre.getPesoTotal());

        // Valor total: oro(10) + oro(10) = 20
        assertEquals(20, cofre.getValorTotal());
    }

    @Test
    void containerItemRespetaCapacidadMaxima() {
        ContainerItem mochila = new ContainerItem("Mochila Pequeña", "Espacio limitado", 2, 1);

        SimpleItem item1 = new SimpleItem("Item 1", "Desc", "Tipo", 10, 1);
        SimpleItem item2 = new SimpleItem("Item 2", "Desc", "Tipo", 10, 1);
        SimpleItem item3 = new SimpleItem("Item 3", "Desc", "Tipo", 10, 1);

        mochila.agregar(item1);
        mochila.agregar(item2);

        assertTrue(mochila.estaLleno());

        assertThrows(IllegalStateException.class, () -> {
            mochila.agregar(item3);
        });
    }

    @Test
    void containerItemPermiteRemoverItems() {
        ContainerItem mochila = new ContainerItem("Mochila", "Mochila normal", 5, 2);
        SimpleItem espada = new SimpleItem("Espada", "Arma", "Arma", 100, 5);

        mochila.agregar(espada);
        assertEquals(1, mochila.getCantidadItems());

        mochila.remover(espada);
        assertEquals(0, mochila.getCantidadItems());
        assertTrue(mochila.estaVacio());
    }

    @Test
    void simpleItemNoPermiteAgregarOtrosItems() {
        SimpleItem espada = new SimpleItem("Espada", "Arma", "Arma", 100, 5);
        SimpleItem escudo = new SimpleItem("Escudo", "Defensa", "Armadura", 80, 6);

        assertThrows(UnsupportedOperationException.class, () -> {
            espada.agregar(escudo);
        });
    }

    @Test
    void containerItemMuestraDetallesContenido() {
        ContainerItem mochila = new ContainerItem("Mochila", "Mochila de aventurero", 5, 2);
        SimpleItem pocion = new SimpleItem("Poción", "Cura 50 HP", "Consumible", 50, 1);

        mochila.agregar(pocion);

        String detalle = mochila.mostrarDetalle();

        assertNotNull(detalle);
        assertTrue(detalle.contains("Mochila"));
        assertTrue(detalle.contains("1/5"));
    }
}
