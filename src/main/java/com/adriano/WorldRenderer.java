package com.adriano;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class WorldRenderer {

    private SpriteBatch batch;
    private Texture waterTile;
    private Texture sandTile;
    private Texture grassTile;
    private Texture rockTile;
    private Texture swampTile;

    public static final int TILE_SIZE = 32;

    public static final int TILE_WATER = 0;
    public static final int TILE_SAND = 1;
    public static final int TILE_GRASS = 2;
    public static final int TILE_ROCK = 4;
    public static final int TILE_SWAMP = 5;

    // --- NUOVO: Definizione degli Item che possono essere raccolti ---
    // Questi ID sono interni al gioco e non devono corrispondere ai TYPE di WorldObject.
    public static final Item ITEM_BUSH = new Item(100, "Cespuglio", "bush_icon", true, 99); // ID, Nome, Texture Icona, Stackable, Max Stack Size
    public static final Item ITEM_LOG = new Item(101, "Tronco", "log_icon", true, 50);

    // Mappa del mondo (lasciata invariata, ma le rocce sono i 4)
    private int[][] worldMap = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 2, 4, 4, 4, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 2, 4, 4, 4, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 2, 4, 4, 4, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 2, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    private Array<WorldObject> worldObjects;

    public WorldRenderer(SpriteBatch batch) {
        this.batch = batch;
        waterTile = new Texture("block_water.png");
        sandTile = new Texture("block_sand.png");
        grassTile = new Texture("block_grass.png");
        rockTile = new Texture("block_rock.png");
        swampTile = new Texture("block_swamp.png");

        worldObjects = new Array<>();

        // --- Aggiorna la creazione dei WorldObject per associare un Item ---
        // I cespugli ora hanno un Item (ITEM_BUSH)
        worldObjects.add(new WorldObject("object_bush_1.png", 5 * TILE_SIZE, (getMapHeightInTiles() - 1 - 4) * TILE_SIZE, WorldObject.TYPE_BUSH, ITEM_BUSH));
        worldObjects.add(new WorldObject("object_bush_2.png", 6 * TILE_SIZE, (getMapHeightInTiles() - 1 - 4) * TILE_SIZE, WorldObject.TYPE_BUSH, ITEM_BUSH));
        worldObjects.add(new WorldObject("object_bush_3.png", 5 * TILE_SIZE, (getMapHeightInTiles() - 1 - 5) * TILE_SIZE, WorldObject.TYPE_BUSH, ITEM_BUSH));
        worldObjects.add(new WorldObject("object_bush_4.png", 10 * TILE_SIZE, (getMapHeightInTiles() - 1 - 12) * TILE_SIZE, WorldObject.TYPE_BUSH, ITEM_BUSH));

        // Gli alberi non hanno un Item associato per ora, poiché non li raccogliamo direttamente
        worldObjects.add(new WorldObject("object_tree_olivo.png", 1 * TILE_SIZE, (getMapHeightInTiles() - 1 - 4) * TILE_SIZE, WorldObject.TYPE_TREE_OLIVO, null));
        worldObjects.add(new WorldObject("object_tree_olivo.png", 2 * TILE_SIZE, (getMapHeightInTiles() - 1 - 5) * TILE_SIZE, WorldObject.TYPE_TREE_OLIVO, null));
        worldObjects.add(new WorldObject("object_tree_palma.png", 15 * TILE_SIZE, (getMapHeightInTiles() - 1 - 5) * TILE_SIZE, WorldObject.TYPE_TREE_PALMA, null));
        worldObjects.add(new WorldObject("object_tree_palma.png", 16 * TILE_SIZE, (getMapHeightInTiles() - 1 - 6) * TILE_SIZE, WorldObject.TYPE_TREE_PALMA, null));
        worldObjects.add(new WorldObject("object_tree_pioppo.png", 8 * TILE_SIZE, (getMapHeightInTiles() - 1 - 7) * TILE_SIZE, WorldObject.TYPE_TREE_PIOppo, null));
        worldObjects.add(new WorldObject("object_tree_pioppo.png", 9 * TILE_SIZE, (getMapHeightInTiles() - 1 - 8) * TILE_SIZE, WorldObject.TYPE_TREE_PIOppo, null));

        // Il tronco ha un Item (ITEM_LOG)
        worldObjects.add(new WorldObject("object_log.png", 7 * TILE_SIZE, (getMapHeightInTiles() - 1 - 9) * TILE_SIZE, WorldObject.TYPE_LOG, ITEM_LOG));
    }

    public void render() {
        for (int y = 0; y < worldMap.length; y++) {
            for (int x = 0; x < worldMap[0].length; x++) {
                int tileType = worldMap[y][x];
                Texture tileToDraw;

                switch (tileType) {
                    case TILE_WATER: tileToDraw = waterTile; break;
                    case TILE_SAND: tileToDraw = sandTile; break;
                    case TILE_GRASS: tileToDraw = grassTile; break;
                    case TILE_ROCK: tileToDraw = rockTile; break;
                    case TILE_SWAMP: tileToDraw = swampTile; break;
                    default: tileToDraw = waterTile; break;
                }

                float drawX = x * TILE_SIZE;
                float drawY = (worldMap.length - 1 - y) * TILE_SIZE;

                batch.draw(tileToDraw, drawX, drawY);
            }
        }

        for (WorldObject object : worldObjects) {
            object.render(batch);
        }
    }

    public int getTileTypeAt(int gridX, int gridY) {
        if (gridX >= 0 && gridX < worldMap[0].length &&
            gridY >= 0 && gridY < worldMap.length) {
            return worldMap[gridY][gridX];
        }
        return -1;
    }

    public int getMapHeightInTiles() {
        return worldMap.length;
    }

    public int getMapWidthInTiles() {
        return worldMap[0].length;
    }

    public Array<WorldObject> getWorldObjects() {
        return worldObjects;
    }

    public void dispose() {
        waterTile.dispose();
        sandTile.dispose();
        grassTile.dispose();
        rockTile.dispose();
        swampTile.dispose();
        for (WorldObject object : worldObjects) {
            object.dispose();
        }
        // Non disporre gli Item statici qui, poiché non sono Texture dirette.
        // Le loro texture sono gestite dall'Inventory.
    }
}