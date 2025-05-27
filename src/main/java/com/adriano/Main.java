package com.adriano;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private WorldRenderer worldRenderer;
    private Player player;
    private BitmapFont font;

    private OrthographicCamera camera;

    @Override
    public void create() {
        System.out.println("LibGDX: Il gioco è stato creato!");
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        worldRenderer = new WorldRenderer(batch);

        float initialPlayerX = 0;
        float initialPlayerY = (worldRenderer.getMapHeightInTiles() - 1) * WorldRenderer.TILE_SIZE;
        float playerBaseSpeed = 100.0f;
        player = new Player("player.png", "player_swim.png", initialPlayerX, initialPlayerY, playerBaseSpeed);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        Vector2 oldPlayerPos = new Vector2(player.getX(), player.getY());
        boolean playerMoved = false;

        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            player.moveLeft(deltaTime);
            playerMoved = true;
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            player.moveRight(deltaTime);
            playerMoved = true;
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            player.moveUp(deltaTime);
            playerMoved = true;
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            player.moveDown(deltaTime);
            playerMoved = true;
        }

        player.update(deltaTime);

        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(),
                                             player.getPlayerTexture().getWidth(),
                                             player.getPlayerTexture().getHeight());

        int playerCenterX = (int) (player.getX() + player.getPlayerTexture().getWidth() / 2);
        int playerCenterY = (int) (player.getY() + player.getPlayerTexture().getHeight() / 2);

        int playerGridX = playerCenterX / WorldRenderer.TILE_SIZE;
        int playerGridY = worldRenderer.getMapHeightInTiles() - 1 - (playerCenterY / WorldRenderer.TILE_SIZE);

        int currentTileType = worldRenderer.getTileTypeAt(playerGridX, playerGridY);

        String tileMessage = "";
        switch (currentTileType) {
            case WorldRenderer.TILE_WATER:
                player.setSwimming(true);
                tileMessage = "Sei sull'ACQUA!";
                break;
            case WorldRenderer.TILE_SAND:
                player.setSwimming(false);
                tileMessage = "Sei sulla SABBIA!";
                break;
            case WorldRenderer.TILE_GRASS:
                player.setSwimming(false);
                tileMessage = "Sei sull'ERBA!";
                break;
            case WorldRenderer.TILE_ROCK: // Ora gestisce sia le vecchie montagne che le rocce
                if (playerMoved) {
                    player.setX(oldPlayerPos.x);
                    player.setY(oldPlayerPos.y);
                }
                player.setSwimming(false);
                tileMessage = "Non puoi passare sulla ROCCIA!"; // Messaggio generico per tutte le rocce/montagne bloccanti
                break;
            case WorldRenderer.TILE_SWAMP:
                player.setSwimming(false);
                player.setSpeedMultiplier(0.25f);
                tileMessage = "Sei nella PALUDE, ti muovi lentamente!";
                break;
            case -1:
                player.setSwimming(false);
                player.setSpeedMultiplier(1.0f);
                tileMessage = "Fuori dai limiti della mappa.";
                break;
            default:
                player.setSwimming(false);
                player.setSpeedMultiplier(1.0f);
                tileMessage = "Terreno sconosciuto.";
                break;
        }

        for (WorldObject object : worldRenderer.getWorldObjects()) {
            if (!object.isCollected() && playerBounds.overlaps(object.getBounds())) {
                switch (object.getType()) {
                    case WorldObject.TYPE_BUSH:
                        tileMessage = "C'e' un CESPUGLIO qui!";
                        if (Gdx.input.isKeyJustPressed(Keys.E)) {
                            object.setCollected(true);
                            tileMessage = "Hai raccolto il CESPUGLIO!";
                        }
                        break;
                    case WorldObject.TYPE_TREE_OLIVO:
                    case WorldObject.TYPE_TREE_PALMA:
                    case WorldObject.TYPE_TREE_PIOppo:
                        if (playerMoved) {
                            player.setX(oldPlayerPos.x);
                            player.setY(oldPlayerPos.y);
                            tileMessage = "Non puoi passare attraverso l'ALBERO!";
                        }
                        break;
                    case WorldObject.TYPE_LOG:
                        tileMessage = "C'e' un TRONCO caduto qui!";
                        if (Gdx.input.isKeyJustPressed(Keys.E)) {
                            object.setCollected(true);
                            tileMessage = "Hai raccolto il TRONCO!";
                        }
                        break;
                }
            }
        }

        camera.position.set(player.getX() + player.getPlayerTexture().getWidth() / 2,
                            player.getY() + player.getPlayerTexture().getHeight() / 2,
                            0);
        camera.update();

        float mapWidthInPixels = worldRenderer.getMapWidthInTiles() * WorldRenderer.TILE_SIZE;
        float mapHeightInPixels = worldRenderer.getMapHeightInTiles() * WorldRenderer.TILE_SIZE;

        float playerCurrentX = player.getX();
        float playerCurrentY = player.getY();
        float playerWidth = player.getPlayerTexture().getWidth();
        float playerHeight = player.getPlayerTexture().getHeight();

        if (playerCurrentX < 0) {
            player.setX(0);
        }
        if (playerCurrentX + playerWidth > mapWidthInPixels) {
            player.setX(mapWidthInPixels - playerWidth);
        }
        if (playerCurrentY < 0) {
            player.setY(0);
        }
        if (playerCurrentY + playerHeight > mapHeightInPixels) {
            player.setY(mapHeightInPixels - playerHeight);
        }

        ScreenUtils.clear(0, 0, 0, 1);

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        worldRenderer.render();
        player.render(batch);

        OrthographicCamera uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);

        font.draw(batch, "Posizione Giocatore: X=" + (int)player.getX() + ", Y=" + (int)player.getY(), 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "Velocita': " + String.format("%.0f", player.getCurrentSpeed()), 10, Gdx.graphics.getHeight() - 50);
        font.draw(batch, tileMessage, 10, Gdx.graphics.getHeight() - 30);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("Finestra ridimensionata a: " + width + "x" + height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        System.out.println("LibGDX: Il gioco è stato chiuso.");
        batch.dispose();
        worldRenderer.dispose();
        player.dispose();
        font.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Urthia wild world");
        config.setWindowedMode(800, 600);
        config.useVsync(true);
        config.setForegroundFPS(60);

        new Lwjgl3Application(new Main(), config);
    }
}