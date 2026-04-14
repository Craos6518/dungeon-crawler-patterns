package game.items.model;

/**
 * Componente base del patrón Composite.
 * Define la interfaz común para items simples y contenedores.
 */
public abstract class ItemComponent {
    protected final String nombre;
    protected final String descripcion;

    protected ItemComponent(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Calcula el valor total del item.
     * Para items simples: su valor propio.
     * Para contenedores: suma de valores de sus contenidos.
     */
    public abstract int getValorTotal();

    /**
     * Calcula el peso total del item.
     * Para items simples: su peso propio.
     * Para contenedores: suma de pesos de sus contenidos.
     */
    public abstract int getPesoTotal();

    /**
     * Muestra la información del item.
     * Para contenedores, muestra también su contenido.
     */
    public abstract String mostrarDetalle();

    /**
     * Intenta agregar un item (solo válido para contenedores).
     * Por defecto lanza excepción.
     */
    public void agregar(ItemComponent item) {
        throw new UnsupportedOperationException(
            "No se puede agregar items a " + this.getClass().getSimpleName()
        );
    }

    /**
     * Intenta remover un item (solo válido para contenedores).
     * Por defecto lanza excepción.
     */
    public void remover(ItemComponent item) {
        throw new UnsupportedOperationException(
            "No se puede remover items de " + this.getClass().getSimpleName()
        );
    }

    /**
     * Intenta obtener un hijo (solo válido para contenedores).
     * Por defecto lanza excepción.
     */
    public ItemComponent obtenerHijo(int indice) {
        throw new UnsupportedOperationException(
            "No se puede obtener hijos de " + this.getClass().getSimpleName()
        );
    }
    /**
     * Crea una copia profunda del componente y toda su descendencia.
     */
    public abstract ItemComponent deepCopy();
}
