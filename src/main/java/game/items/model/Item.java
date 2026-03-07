package game.items.model;

/**
 * Representa un item/tesoro dentro del juego
 */
public class Item {
    private final String nombre;
    private final String descripcion;
    private final String tipo;
    private final int valor;

    public Item(String nombre, String descripcion, String tipo, int valor) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public int getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s [Valor: %d]", 
            nombre, tipo, descripcion, valor);
    }
}
