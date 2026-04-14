package game.items.model;

/**
 * Leaf del patrón Composite.
 * Representa un item individual que no puede contener otros items.
 */
public class SimpleItem extends ItemComponent {
    private final String tipo;
    private final int valor;
    private final int peso;

    public SimpleItem(String nombre, String descripcion, String tipo, int valor, int peso) {
        super(nombre, descripcion);
        this.tipo = tipo;
        this.valor = Math.max(0, valor);
        this.peso = Math.max(0, peso);
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public int getValorTotal() {
        return valor;
    }

    @Override
    public int getPesoTotal() {
        return peso;
    }

    @Override
    public String mostrarDetalle() {
        return String.format("%s (%s) - %s [Valor: %d, Peso: %d]", 
            nombre, tipo, descripcion, valor, peso);
    }

    @Override
    public String toString() {
        return mostrarDetalle();
    }
    @Override
    public ItemComponent deepCopy() {
        return new SimpleItem(nombre, descripcion, tipo, valor, peso);
    }
}
