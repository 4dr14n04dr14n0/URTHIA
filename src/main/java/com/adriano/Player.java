package com.adriano;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {

    private Texture playerTexture;
    private Texture walkingTexture;
    private Texture swimmingTexture;

    private float x;
    private float y;
    private float baseSpeed;
    private float currentSpeed;

    public Player(String walkingTexturePath, String swimmingTexturePath, float startX, float startY, float baseSpeed) {
        this.walkingTexture = new Texture(walkingTexturePath);
        this.swimmingTexture = new Texture(swimmingTexturePath);
        this.playerTexture = this.walkingTexture;

        this.x = startX;
        this.y = startY;
        this.baseSpeed = baseSpeed;
        this.currentSpeed = baseSpeed;
    }

    public void render(SpriteBatch batch) {
        batch.draw(playerTexture, x, y);
    }

    public void dispose() {
        walkingTexture.dispose();
        swimmingTexture.dispose();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Texture getPlayerTexture() {
        return playerTexture;
    }

    // --- Metodo esistente per nuoto, ora possiamo generalizzarlo ---
    // (Per ora lo manteniamo per la chiarezza dell'esempio precedente,
    // ma potremmo renderlo più generico in futuro)
    public void setSwimming(boolean isSwimming) {
        if (isSwimming) {
            this.playerTexture = swimmingTexture;
            this.currentSpeed = baseSpeed * 0.5f; // Esempio: 50% di velocità in acqua
        } else {
            this.playerTexture = walkingTexture;
            this.currentSpeed = baseSpeed;
        }
    }

    // --- NUOVO METODO: Imposta la velocità in base a una percentuale della velocità base ---
    public void setSpeedMultiplier(float multiplier) {
        this.currentSpeed = baseSpeed * multiplier;
    }

    // --- NUOVO METODO: Imposta la texture (utile per non essere legati solo al nuoto) ---
    public void setPlayerTexture(Texture texture) {
        this.playerTexture = texture;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void moveUp(float deltaTime) {
        this.y += currentSpeed * deltaTime;
    }

    public void moveDown(float deltaTime) {
        this.y -= currentSpeed * deltaTime;
    }

    public void moveLeft(float deltaTime) {
        this.x -= currentSpeed * deltaTime;
    }

    public void moveRight(float deltaTime) {
        this.x += currentSpeed * deltaTime;
    }

    public void update(float deltaTime) {
        // Nessun cambiamento qui per ora
    }
}