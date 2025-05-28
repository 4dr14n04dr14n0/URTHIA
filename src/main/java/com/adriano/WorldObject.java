package com.adriano;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class WorldObject {

    private Texture texture;
    private float x, y;
    private int type; // Manteniamo type per la logica di interazione (es. bloccante)
    private boolean collected;
    private Item itemData; // NUOVO: L'oggetto Item che questo WorldObject rappresenta una volta raccolto

    // Vecchie costanti per i tipi di WorldObject (per la logica di gioco)
    public static final int TYPE_BUSH = 0;
    public static final int TYPE_TREE_OLIVO = 1;
    public static final int TYPE_TREE_PALMA = 2;
    public static final int TYPE_TREE_PIOppo = 3;
    public static final int TYPE_LOG = 4;

    // Costruttore modificato per includere l'Item associato
    public WorldObject(String texturePath, float x, float y, int type, Item itemData) {
        this.texture = new Texture(texturePath);
        this.x = x;
        this.y = y;
        this.type = type;
        this.collected = false;
        this.itemData = itemData; // Assegna l'Item
    }

    public void render(SpriteBatch batch) {
        if (!collected) {
            batch.draw(texture, x, y);
        }
    }

    public void dispose() {
        texture.dispose();
        // Non fare dispose di itemData.texture qui, perché potrebbe essere condivisa tra più WorldObject
        // e verrà gestita dall'Inventory o da un AssetManager centrale.
    }

    // --- Getter NUOVO ---
    public Item getItemData() {
        return itemData;
    }

    // --- Getter (vecchi) ---
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getType() {
        return type;
    }

    public boolean isCollected() {
        return collected;
    }

    // --- Setter (vecchio) ---
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }
}