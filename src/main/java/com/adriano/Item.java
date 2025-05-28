package com.adriano;

// Questa classe rappresenta un tipo di oggetto nel gioco, non un'istanza fisica nel mondo.
// Ad esempio, "Legno" è un Item, e quando lo raccogli, la tua inventario avrà "Legno x 5".
public class Item {

    public final int id;
    public final String name;
    public final String texturePath; // Path della texture per l'icona nell'inventario
    public final boolean stackable;
    public final int maxStackSize;

    // Costruttore per item non stackabili (o che non hanno una dimensione massima di stack)
    public Item(int id, String name, String texturePath) {
        this(id, name, texturePath, false, 1); // Non stackabile, stack size di 1
    }

    // Costruttore completo per item stackabili
    public Item(int id, String name, String texturePath, boolean stackable, int maxStackSize) {
        this.id = id;
        this.name = name;
        this.texturePath = texturePath;
        this.stackable = stackable;
        this.maxStackSize = maxStackSize;
    }

    // Per confrontare due Item in base all'ID (utile per lo stacking)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}