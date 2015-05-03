package org.spongepowered.common.item.inventory;

import com.flowpowered.math.vector.Vector2i;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryOperationResult;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.inventory.type.InventoryColumn;
import org.spongepowered.api.item.inventory.type.InventoryRow;
import org.spongepowered.common.interfaces.IMixinInventoryCrafting;

import java.util.Optional;

public class GridInventoryImpl extends Inventory2DImpl implements GridInventory {

    private final int colNum;
    private final int rowNum;
    private final Vector2i dims;

    public GridInventoryImpl(Inventory parent, Container container, int width, int startIndex, int endIndex) {
        super(parent, container, startIndex, endIndex);
        this.colNum = width;
        this.rowNum = (endIndex - startIndex) / width;
        this.dims = new Vector2i(this.colNum, this.rowNum);
    }

    @Override
    public int getColumns() {
        return this.colNum;
    }

    @Override
    public int getRows() {
        return this.rowNum;
    }

    @Override
    public Vector2i getDimensions() {
        return this.dims;
    }

    @Override
    public Optional<ItemStack> poll(int x, int y) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> poll(int x, int y, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek(int x, int y) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek(int x, int y, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InventoryOperationResult set(int x, int y, ItemStack stack) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Slot> getSlot(int x, int y) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<InventoryRow> getRow(int y) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<InventoryColumn> getColumn(int x) {
        // TODO Auto-generated method stub
        return null;
    }

    private InventoryCrafting inventoryCrafting;

    public InventoryCrafting getInventoryCrafting() {
        if (this.inventoryCrafting == null) {
            this.inventoryCrafting = new InventoryCrafting(this.container, this.colNum, this.rowNum);
        }
        return this.inventoryCrafting;
    }

    public static GridInventory fromInventoryCrafting(InventoryCrafting inv) {
        return ((IMixinInventoryCrafting) inv).getGridInventory();
    }

}
