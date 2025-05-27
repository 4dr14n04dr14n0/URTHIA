package com.adriano;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class WorldObject {

    private Texture texture;
    private float x, y;
    private int type;
    private boolean collected;

    // --- Nuove Costanti per i Tipi di Oggetto ---
    public static final int TYPE_BUSH = 0; // Usiamo 0 per i cespugli (se non vuoi un numero specifico per ogni bush)
    public static final int TYPE_TREE_OLIVO = 1;
    public static final int TYPE_TREE_PALMA = 2;
    public static final int TYPE_TREE_PIOppo = 3;
    public static final int TYPE_LOG = 4; // Tronco caduto
    // Puoi anche lasciare un generico TYPE_TREE e poi usare sottotipi se vuoi differenziare

    public WorldObject(String texturePath, float x, float y, int type) {
        this.texture = new Texture(texturePath);
        this.x = x;
        this.y = y;
        this.type = type;
        this.collected = false;
    }

    public void render(SpriteBatch batch) {
        if (!collected) {
            batch.draw(texture, x, y);
        }
    }

    public void dispose() {
        texture.dispose();
    }

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

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }
}