package game.items.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite del patrón Composite.
 * Representa un contenedor que puede almacenar otros items (simples o contenedores).
 * Ejemplo: mochilas, cofres, bolsas.
 */
public class ContainerItem extends ItemComponent {
    private final List<ItemComponent> items;
    private final int capacidadMaxima;
    private final int pesoPropio;

    public ContainerItem(String nombre, String descripcion, int capacidadMaxima, int pesoPropio) {
        super(nombre, descripcion);
        this.items = new ArrayList<>();
        this.capacidadMaxima = Math.max(1, capacidadMaxima);
        this.pesoPropio = Math.max(0, pesoPropio);
    }

    @Override
    public void agregar(ItemComponent item) {
        if (items.size() >= capacidadMaxima) {
            throw new IllegalStateException(
                "El contenedor " + nombre + " está lleno (capacidad: " + capacidadMaxima + ")"
            );
        }
        items.add(item);
    }

    @Override
    public void remover(ItemComponent item) {
        if (!items.remove(item)) {
            throw new IllegalArgumentException(
                "El item " + item.getNombre() + " no está en el contenedor " + nombre
            );
        }
    }

    @Override
    public ItemComponent obtenerHijo(int indice) {
        if (indice < 0 || indice >= items.size()) {
            throw new IndexOutOfBoundsException(
                "Índice " + indice + " fuera de rango. Contenedor tiene " + items.size() + " items"
            );
        }
        return items.get(indice);
    }

    /**
     * Obtiene una lista inmutable de los items contenidos.
     */
    public List<ItemComponent> obtenerItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Cantidad de items actualmente almacenados.
     */
    public int getCantidadItems() {
        return items.size();
    }

    /**
     * Capacidad máxima del contenedor.
     */
    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    /**
     * Verifica si el contenedor está lleno.
     */
    public boolean estaLleno() {
        return items.size() >= capacidadMaxima;
    }

    /**
     * Verifica si el contenedor está vacío.
     */
    public boolean estaVacio() {
        return items.isEmpty();
    }

    @Override
    public int getValorTotal() {
        // Valor propio del contenedor + suma del valor de todos los items contenidos
        int valorContenido = items.stream()
            .mapToInt(ItemComponent::getValorTotal)
            .sum();
        return valorContenido;
    }

    @Override
    public int getPesoTotal() {
        // Peso propio del contenedor + suma del peso de todos los items contenidos
        int pesoContenido = items.stream()
            .mapToInt(ItemComponent::getPesoTotal)
            .sum();
        return pesoPropio + pesoContenido;
    }

    @Override
    public String mostrarDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s - %s [Capacidad: %d/%d, Peso: %d, Valor total: %d]\n", 
            nombre, descripcion, items.size(), capacidadMaxima, getPesoTotal(), getValorTotal()));
        
        if (items.isEmpty()) {
            sb.append("  (vacío)");
        } else {
            for (int i = 0; i < items.size(); i++) {
                ItemComponent item = items.get(i);
                sb.append("  [").append(i).append("] ");
                
                // Si es otro contenedor, mostrar solo su resumen
                if (item instanceof ContainerItem) {
                    ContainerItem cont = (ContainerItem) item;
                    sb.append(item.getNombre())
                      .append(" (Contenedor con ")
                      .append(cont.getCantidadItems())
                      .append(" items)");
                } else {
                    sb.append(item.getNombre());
                }
                
                if (i < items.size() - 1) {
                    sb.append("\n");
                }
            }
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s (Contenedor: %d/%d items)", 
            nombre, items.size(), capacidadMaxima);
    }

    public int getPesoPropio() {
        return pesoPropio;
    }
    @Override
    public ItemComponent deepCopy() {
        ContainerItem copy = new ContainerItem(nombre, descripcion, capacidadMaxima, pesoPropio);
        for (ItemComponent child : items) {
            copy.agregar(child.deepCopy());
        }
        return copy;
    }
}
