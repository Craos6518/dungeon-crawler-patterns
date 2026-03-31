package game.domain.inventory;

import game.items.model.SimpleItem;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Entidad de inventario orientada al dominio.
 */
public class Item {

    private final String id;
    private final SimpleItem raw;

    public Item(String id, SimpleItem raw) {
        this.id = id;
        this.raw = raw;
    }

    public static Item from(SimpleItem item, int index) {
        return new Item(buildId(item.getNombre(), index), item);
    }

    public static String buildId(String name, int index) {
        return slugify(name) + "-" + index;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return raw.getNombre();
    }

    public String getType() {
        return raw.getTipo();
    }

    public String getDescription() {
        return raw.getDescripcion();
    }

    public int getValue() {
        return raw.getValorTotal();
    }

    public double getWeight() {
        return raw.getPesoTotal();
    }

    public SimpleItem getRaw() {
        return raw;
    }

    public boolean isPotion() {
        return normalize(getName()).contains("poci");
    }

    public boolean isAntidote() {
        return normalize(getName()).contains("antid");
    }

    public boolean isConsumable() {
        String type = normalize(getType());
        return type.contains("consum") || isPotion() || isAntidote();
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    private static String slugify(String text) {
        String normalized = normalize(text);
        return normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
