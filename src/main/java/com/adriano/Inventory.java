package com.adriano;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class Inventory {

    public static class InventorySlot {
        public Item item;
        public int quantity;

        public InventorySlot(Item item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        public int addQuantity(int amount) {
            int canAdd = item.maxStackSize - quantity;
            int actualAdd = Math.min(canAdd, amount);
            quantity += actualAdd;
            return amount - actualAdd;
        }
    }

    private Array<InventorySlot> slots;
    private BitmapFont font;
    private Texture inventorySlotTexture;
    private Texture inventoryBackground;
    private Texture inventoryLogo;

    private Texture arrowLeftTexture;
    private Texture arrowRightTexture;

    private ObjectMap<String, Texture> itemTextures;

    private static final int INVENTORY_SLOT_SIZE = 48;

    public static final float INVENTORY_BG_WIDTH = 400;
    public static final float INVENTORY_BG_HEIGHT = 382;

    private static final int GRID_AREA_WIDTH = 253;
    private static final int GRID_AREA_HEIGHT = 186;
    private static final int GRID_AREA_OFFSET_LEFT = 77;
    private static final int GRID_AREA_OFFSET_BOTTOM = 51;
    // NUOVA COSTANTE: Offset dal bordo destro della texture di sfondo
    private static final int GRID_AREA_OFFSET_RIGHT = 66;


    public static final float LOGO_X = 10;
    public static final float LOGO_Y = 10;
    public static final float LOGO_WIDTH = 64;
    public static final float LOGO_HEIGHT = 64;

    private static final int INVENTORY_SLOTS_PER_ROW = 8;
    private static final int MAX_SLOTS = 60;

    private int currentPage;
    private int slotsPerPage;
    private int totalPages;

    private boolean isOpen;

    private boolean isAnimating;
    private float animationTime;
    private float animationDuration = 0.2f;
    private float currentScale;


    public Inventory(BitmapFont font) {
        this.font = font;
        this.slots = new Array<>();
        for (int i = 0; i < MAX_SLOTS; i++) {
            slots.add(null);
        }

        this.inventorySlotTexture = new Texture("inventory_slot.png");
        this.inventoryBackground = new Texture("inventory_background.png");
        this.inventoryLogo = new Texture("inventory_logo.png");

        this.arrowLeftTexture = new Texture("arrow_left.png");
        this.arrowRightTexture = new Texture("arrow_right.png");

        this.itemTextures = new ObjectMap<>();
        itemTextures.put("bush_icon", new Texture("object_bush_1.png"));
        itemTextures.put("log_icon", new Texture("object_log.png"));
        // ... Aggiungi altre icone degli item qui ...

        this.isOpen = false;
        this.isAnimating = false;
        this.animationTime = 0;
        this.currentScale = 0;

        int rowsPerGridArea = GRID_AREA_HEIGHT / INVENTORY_SLOT_SIZE;
        this.slotsPerPage = INVENTORY_SLOTS_PER_ROW * rowsPerGridArea;
        this.totalPages = (int) Math.ceil((double) MAX_SLOTS / slotsPerPage);
        this.currentPage = 0;
    }

    public void addItem(Item itemToAdd, int amount) {
        if (itemToAdd == null || amount <= 0) {
            return;
        }

        if (itemToAdd.stackable) {
            for (InventorySlot slot : slots) {
                if (slot != null && slot.item.equals(itemToAdd) && slot.quantity < slot.item.maxStackSize) {
                    int remainingAmount = slot.addQuantity(amount);
                    if (remainingAmount == 0) {
                        System.out.println("Aggiunto all'inventario: " + itemToAdd.name + " x " + amount);
                        return;
                    } else {
                        amount = remainingAmount;
                    }
                }
            }
        }

        while (amount > 0) {
            int emptySlotIndex = -1;
            for (int i = 0; i < slots.size; i++) {
                if (slots.get(i) == null) {
                    emptySlotIndex = i;
                    break;
                }
            }

            if (emptySlotIndex != -1) {
                int quantityForNewSlot = amount;
                if (itemToAdd.stackable) {
                    quantityForNewSlot = Math.min(amount, itemToAdd.maxStackSize);
                }

                slots.set(emptySlotIndex, new InventorySlot(itemToAdd, quantityForNewSlot));
                amount -= quantityForNewSlot;
                System.out.println("Aggiunto all'inventario: " + itemToAdd.name + " x " + quantityForNewSlot);
            } else {
                System.out.println("Inventario pieno! Impossibile aggiungere " + itemToAdd.name);
                break;
            }
        }
    }

    public void nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
        }
    }

    public void render(SpriteBatch batch, float screenWidth, float screenHeight, float deltaTime) {
        batch.draw(inventoryLogo, LOGO_X, LOGO_Y, LOGO_WIDTH, LOGO_HEIGHT);

        if (isAnimating) {
            animationTime += deltaTime;
            if (animationTime >= animationDuration) {
                animationTime = animationDuration;
                isAnimating = false;
                if (!isOpen) {
                    currentScale = 0;
                    return;
                }
            }

            if (isOpen) {
                currentScale = animationTime / animationDuration;
            } else {
                currentScale = 1 - (animationTime / animationDuration);
            }
        } else if (!isOpen && currentScale <= 0) {
            return;
        } else if (isOpen && currentScale < 1) {
             currentScale = 1;
        }

        float desiredPanelWidth = INVENTORY_BG_WIDTH;
        float desiredPanelHeight = INVENTORY_BG_HEIGHT;

        float finalPanelX = (screenWidth - desiredPanelWidth) / 2;
        float finalPanelY = (screenHeight - desiredPanelHeight) / 2;

        float currentPanelWidth = desiredPanelWidth * currentScale;
        float currentPanelHeight = desiredPanelHeight * currentScale;

        float originX = LOGO_X + LOGO_WIDTH / 2;
        float originY = LOGO_Y + LOGO_HEIGHT / 2;

        float panelX = originX - (desiredPanelWidth / 2) * currentScale;
        float panelY = originY - (desiredPanelHeight / 2) * currentScale;

        panelX = finalPanelX + (panelX - finalPanelX) * (1 - currentScale);
        panelY = finalPanelY + (panelY - finalPanelY) * (1 - currentScale);

        batch.draw(inventoryBackground, panelX, panelY, currentPanelWidth, currentPanelHeight);

        if (currentScale > 0.8f) {
            String inventoryTitle = "INVENTARIO";
            GlyphLayout layout = new GlyphLayout(font, inventoryTitle);
            float titleX = panelX + (desiredPanelWidth - layout.width) / 2;
            float titleY = panelY + desiredPanelHeight - 132 + (font.getCapHeight() / 2) ;

            font.draw(batch, inventoryTitle, titleX, titleY);

            float gridStartX = panelX + GRID_AREA_OFFSET_LEFT;
            float gridStartY = panelY + GRID_AREA_OFFSET_BOTTOM;

            int actualRowsInGrid = GRID_AREA_HEIGHT / INVENTORY_SLOT_SIZE;
            float verticalSpacing = (GRID_AREA_HEIGHT - (actualRowsInGrid * INVENTORY_SLOT_SIZE));
            float verticalOffset = verticalSpacing / (actualRowsInGrid + 1);

            int horizontalSpacing = (GRID_AREA_WIDTH - (INVENTORY_SLOTS_PER_ROW * INVENTORY_SLOT_SIZE));
            float horizontalOffset = horizontalSpacing / (INVENTORY_SLOTS_PER_ROW + 1);

            int startIndex = currentPage * slotsPerPage;
            int endIndex = Math.min(startIndex + slotsPerPage, MAX_SLOTS);

            for (int row = 0; row < actualRowsInGrid; row++) {
                float currentRowY = gridStartY + (row * INVENTORY_SLOT_SIZE) + (row + 1) * verticalOffset;

                for (int col = 0; col < INVENTORY_SLOTS_PER_ROW; col++) {
                    float currentSlotX = gridStartX + (col * INVENTORY_SLOT_SIZE) + (col + 1) * horizontalOffset;
                    
                    int slotIndex = startIndex + (row * INVENTORY_SLOTS_PER_ROW) + col;

                    if (slotIndex < endIndex && slotIndex < MAX_SLOTS) {
                        batch.draw(inventorySlotTexture, currentSlotX, currentRowY, INVENTORY_SLOT_SIZE, INVENTORY_SLOT_SIZE);

                        InventorySlot slot = slots.get(slotIndex);
                        if (slot != null && slot.item != null) {
                            Texture itemIcon = itemTextures.get(slot.item.texturePath);
                            if (itemIcon != null) {
                                batch.draw(itemIcon, currentSlotX, currentRowY, INVENTORY_SLOT_SIZE, INVENTORY_SLOT_SIZE);
                            }

                            if (slot.item.stackable && slot.quantity > 1) {
                                String quantityText = String.valueOf(slot.quantity);
                                GlyphLayout quantityLayout = new GlyphLayout(font, quantityText);
                                font.draw(batch, quantityText, currentSlotX + INVENTORY_SLOT_SIZE - quantityLayout.width - 2, currentRowY + quantityLayout.height + 2);
                            }
                        }
                    }
                }
            }
            
            float arrowWidth = 32;
            float arrowHeight = 32;

            float arrowLeftX = panelX + GRID_AREA_OFFSET_LEFT;
            float arrowLeftY = panelY + 15;

            // Ora usa la nuova costante GRID_AREA_OFFSET_RIGHT
            float arrowRightX = panelX + desiredPanelWidth - GRID_AREA_OFFSET_RIGHT - arrowWidth;
            float arrowRightY = panelY + 15;

            if (currentPage > 0) {
                batch.draw(arrowLeftTexture, arrowLeftX, arrowLeftY, arrowWidth, arrowHeight);
            }

            if (currentPage < totalPages - 1) {
                batch.draw(arrowRightTexture, arrowRightX, arrowRightY, arrowWidth, arrowHeight);
            }

            String pageText = (currentPage + 1) + "/" + totalPages;
            GlyphLayout pageLayout = new GlyphLayout(font, pageText);
            float pageTextX = panelX + (desiredPanelWidth - pageLayout.width) / 2;
            float pageTextY = panelY + 15 + arrowHeight / 2 + pageLayout.height / 2;
            font.draw(batch, pageText, pageTextX, pageTextY);
        }
    }

    public void dispose() {
        inventorySlotTexture.dispose();
        inventoryBackground.dispose();
        inventoryLogo.dispose();
        arrowLeftTexture.dispose();
        arrowRightTexture.dispose();
        for (Texture texture : itemTextures.values()) {
            texture.dispose();
        }
    }

    public void toggle() {
        if (!isAnimating) {
            isOpen = !isOpen;
            isAnimating = true;
            animationTime = 0;
            if (isOpen) {
                currentScale = 0;
            } else {
                currentScale = 1;
            }
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    // NUOVI METODI per ottenere i limiti delle frecce per la rilevazione del click in Main.java
    public Rectangle getArrowLeftBounds(float screenWidth, float screenHeight) {
        if (!isOpen && !isAnimating) return null;

        float desiredPanelWidth = INVENTORY_BG_WIDTH;
        float desiredPanelHeight = INVENTORY_BG_HEIGHT;
        float finalPanelX = (screenWidth - desiredPanelWidth) / 2;
        float finalPanelY = (screenHeight - desiredPanelHeight) / 2;

        float arrowWidth = 32;
        float arrowHeight = 32;

        float arrowLeftX = finalPanelX + GRID_AREA_OFFSET_LEFT;
        float arrowLeftY = finalPanelY + 15;

        float currentPanelX = finalPanelX + (LOGO_X + LOGO_WIDTH / 2 - finalPanelX) * (1 - currentScale);
        float currentPanelY = finalPanelY + (LOGO_Y + LOGO_HEIGHT / 2 - finalPanelY) * (1 - currentScale);

        arrowLeftX = currentPanelX + (arrowLeftX - finalPanelX) * currentScale;
        arrowLeftY = currentPanelY + (arrowLeftY - finalPanelY) * currentScale;

        return new Rectangle(arrowLeftX, arrowLeftY, arrowWidth * currentScale, arrowHeight * currentScale);
    }

    public Rectangle getArrowRightBounds(float screenWidth, float screenHeight) {
        if (!isOpen && !isAnimating) return null;

        float desiredPanelWidth = INVENTORY_BG_WIDTH;
        float desiredPanelHeight = INVENTORY_BG_HEIGHT;
        float finalPanelX = (screenWidth - desiredPanelWidth) / 2;
        float finalPanelY = (screenHeight - desiredPanelHeight) / 2;

        float arrowWidth = 32;
        float arrowHeight = 32;

        // Ora usa la nuova costante GRID_AREA_OFFSET_RIGHT
        float arrowRightX = finalPanelX + desiredPanelWidth - GRID_AREA_OFFSET_RIGHT - arrowWidth;
        float arrowRightY = finalPanelY + 15;

        float currentPanelX = finalPanelX + (LOGO_X + LOGO_WIDTH / 2 - finalPanelX) * (1 - currentScale);
        float currentPanelY = finalPanelY + (LOGO_Y + LOGO_HEIGHT / 2 - finalPanelY) * (1 - currentScale);

        arrowRightX = currentPanelX + (arrowRightX - finalPanelX) * currentScale;
        arrowRightY = currentPanelY + (arrowRightY - finalPanelY) * currentScale;

        return new Rectangle(arrowRightX, arrowRightY, arrowWidth * currentScale, arrowHeight * currentScale);
    }
}