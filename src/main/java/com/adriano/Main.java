package com.adriano;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private WorldRenderer worldRenderer;
    private Player player;
    private BitmapFont font;
    private Inventory inventory;

    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;

    @Override
    public void create() {
        System.out.println("LibGDX: Il gioco è stato creato!");
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        worldRenderer = new WorldRenderer(batch);

        float initialPlayerX = 0;
        float initialPlayerY = (worldRenderer.getMapHeightInTiles() - 1) * WorldRenderer.TILE_SIZE;
        float playerBaseSpeed = 100.0f;
        player = new Player("player.png", "player_swim.png", initialPlayerX, initialPlayerY, playerBaseSpeed);

        inventory = new Inventory(font);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Gestione dell'input per l'inventario
        if (Gdx.input.isKeyJustPressed(Keys.I)) {
            inventory.toggle();
        }

        // Gestione del click del mouse
        if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();

            Vector3 touchPoint = new Vector3(mouseX, mouseY, 0);
            uiCamera.unproject(touchPoint);

            // Rileva click sul logo dell'inventario
            Rectangle logoBounds = new Rectangle(Inventory.LOGO_X, Inventory.LOGO_Y, Inventory.LOGO_WIDTH, Inventory.LOGO_HEIGHT);
            if (logoBounds.contains(touchPoint.x, touchPoint.y)) {
                inventory.toggle();
            }

            // Rileva click sulle frecce dell'inventario solo se l'inventario è aperto
            if (inventory.isOpen()) {
                Rectangle arrowLeftBounds = inventory.getArrowLeftBounds(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Rectangle arrowRightBounds = inventory.getArrowRightBounds(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

                if (arrowLeftBounds != null && arrowLeftBounds.contains(touchPoint.x, touchPoint.y)) {
                    inventory.previousPage();
                } else if (arrowRightBounds != null && arrowRightBounds.contains(touchPoint.x, touchPoint.y)) {
                    inventory.nextPage();
                }
            }
        }

        if (inventory.isOpen()) {
            player.setSpeedMultiplier(0.0f);
        } else {
            player.setSpeedMultiplier(1.0f);
        }

        Vector2 oldPlayerPos = new Vector2(player.getX(), player.getY());
        boolean playerMoved = false;

        if (!inventory.isOpen()) {
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
            case WorldRenderer.TILE_ROCK:
                if (playerMoved) {
                    player.setX(oldPlayerPos.x);
                    player.setY(oldPlayerPos.y);
                }
                player.setSwimming(false);
                tileMessage = "Non puoi passare sulla ROCCIA!";
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

        if (!inventory.isOpen()) {
            for (WorldObject object : worldRenderer.getWorldObjects()) {
                if (!object.isCollected() && playerBounds.overlaps(object.getBounds())) {
                    switch (object.getType()) {
                        case WorldObject.TYPE_BUSH:
                            tileMessage = "C'e' un CESPUGLIO qui! Premi E per raccogliere.";
                            if (Gdx.input.isKeyJustPressed(Keys.E)) {
                                if (object.getItemData() != null) {
                                    inventory.addItem(object.getItemData(), 1);
                                    object.setCollected(true);
                                    tileMessage = "Hai raccolto il CESPUGLIO!";
                                } else {
                                    tileMessage = "Questo cespuglio non ha un Item da raccogliere!";
                                }
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
                            tileMessage = "C'e' un TRONCO caduto qui! Premi E per raccogliere.";
                            if (Gdx.input.isKeyJustPressed(Keys.E)) {
                                if (object.getItemData() != null) {
                                    inventory.addItem(object.getItemData(), 1);
                                    object.setCollected(true);
                                    tileMessage = "Hai raccolto il TRONCO!";
                                } else {
                                    tileMessage = "Questo tronco non ha un Item da raccogliere!";
                                }
                            }
                            break;
                    }
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

        batch.setProjectionMatrix(uiCamera.combined);

        if (!inventory.isOpen()) {
            font.draw(batch, "Posizione Giocatore: X=" + (int)player.getX() + ", Y=" + (int)player.getY(), 10, Gdx.graphics.getHeight() - 10);
            font.draw(batch, "Velocita': " + String.format("%.0f", player.getCurrentSpeed()), 10, Gdx.graphics.getHeight() - 50);
            font.draw(batch, tileMessage, 10, Gdx.graphics.getHeight() - 30);
            font.draw(batch, "Premi 'I' o clicca sull'icona per aprire l'inventario", 10, Gdx.graphics.getHeight() - 70);
        }

        inventory.render(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), deltaTime);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("Finestra ridimensionata a: " + width + "x" + height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        uiCamera.viewportWidth = width;
        uiCamera.viewportHeight = height;
        uiCamera.update();
    }

    @Override
    public void dispose() {
        System.out.println("LibGDX: Il gioco è stato chiuso.");
        batch.dispose();
        worldRenderer.dispose();
        player.dispose();
        font.dispose();
        inventory.dispose();
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