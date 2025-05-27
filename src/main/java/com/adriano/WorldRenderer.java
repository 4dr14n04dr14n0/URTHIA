package com.adriano;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class WorldRenderer {

    private SpriteBatch batch;
    private Texture waterTile;
    private Texture sandTile;
    private Texture grassTile;
    // private Texture mountainTile; // RIMOSSO: Non useremo più una texture dedicata per la montagna
    private Texture rockTile;   // Questa sarà la texture per le rocce/montagne bloccanti
    private Texture swampTile;

    public static final int TILE_SIZE = 32;

    public static final int TILE_WATER = 0;
    public static final int TILE_SAND = 1;
    public static final int TILE_GRASS = 2;
    // public static final int TILE_MOUNTAIN = 3; // RIMOSSO: Useremo TILE_ROCK (4) per questo scopo
    public static final int TILE_ROCK = 4;   // Ora TILE_ROCK (4) rappresenta tutte le zone rocciose/montagne bloccanti
    public static final int TILE_SWAMP = 5;

    // --- MAPPA AGGIORNATA: Tutti i '3' (montagne) sono stati cambiati in '4' (rocce) ---
    private int[][] worldMap = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 2, 4, 4, 4, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Ora rocce
        {0, 0, 1, 2, 4, 4, 4, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Ora rocce
        {0, 0, 1, 2, 4, 4, 4, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Ora rocce
        {0, 0, 1, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 2, 1, 0, 0, 0, 0, 0}, // Roccia singola (4)
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
        // mountainTile = new Texture("block_mountain.png"); // RIMOSSO
        rockTile = new Texture("block_rock.png");     // Usiamo questa per tutte le rocce/montagne
        swampTile = new Texture("block_swamp.png");

        worldObjects = new Array<>();

        // --- Aggiungi i cespugli (come prima) ---
        worldObjects.add(new WorldObject("object_bush_1.png", 5 * TILE_SIZE, (getMapHeightInTiles() - 1 - 4) * TILE_SIZE, WorldObject.TYPE_BUSH));
        worldObjects.add(new WorldObject("object_bush_2.png", 6 * TILE_SIZE, (getMapHeightInTiles() - 1 - 4) * TILE_SIZE, WorldObject.TYPE_BUSH));
        worldObjects.add(new WorldObject("object_bush_3.png", 5 * TILE_SIZE, (getMapHeightInTiles() - 1 - 5) * TILE_SIZE, WorldObject.TYPE_BUSH));
        worldObjects.add(new WorldObject("object_bush_4.png", 10 * TILE_SIZE, (getMapHeightInTiles() - 1 - 12) * TILE_SIZE, WorldObject.TYPE_BUSH));

        // --- Alberi e tronco (come prima) ---
        worldObjects.add(new WorldObject("object_tree_olivo.png", 1 * TILE_SIZE, (getMapHeightInTiles() - 1 - 4) * TILE_SIZE, WorldObject.TYPE_TREE_OLIVO));
        worldObjects.add(new WorldObject("object_tree_olivo.png", 2 * TILE_SIZE, (getMapHeightInTiles() - 1 - 5) * TILE_SIZE, WorldObject.TYPE_TREE_OLIVO));
        worldObjects.add(new WorldObject("object_tree_palma.png", 15 * TILE_SIZE, (getMapHeightInTiles() - 1 - 5) * TILE_SIZE, WorldObject.TYPE_TREE_PALMA));
        worldObjects.add(new WorldObject("object_tree_palma.png", 16 * TILE_SIZE, (getMapHeightInTiles() - 1 - 6) * TILE_SIZE, WorldObject.TYPE_TREE_PALMA));
        worldObjects.add(new WorldObject("object_tree_pioppo.png", 8 * TILE_SIZE, (getMapHeightInTiles() - 1 - 7) * TILE_SIZE, WorldObject.TYPE_TREE_PIOppo));
        worldObjects.add(new WorldObject("object_tree_pioppo.png", 9 * TILE_SIZE, (getMapHeightInTiles() - 1 - 8) * TILE_SIZE, WorldObject.TYPE_TREE_PIOppo));
        worldObjects.add(new WorldObject("object_log.png", 7 * TILE_SIZE, (getMapHeightInTiles() - 1 - 9) * TILE_SIZE, WorldObject.TYPE_LOG));
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
                    // case TILE_MOUNTAIN: tileToDraw = mountainTile; break; // RIMOSSO
                    case TILE_ROCK: tileToDraw = rockTile; break; // Ora gestisce tutte le rocce/montagne
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
        // mountainTile.dispose(); // RIMOSSO
        rockTile.dispose();
        swampTile.dispose();
        for (WorldObject object : worldObjects) {
            object.dispose();
        }
    }
}