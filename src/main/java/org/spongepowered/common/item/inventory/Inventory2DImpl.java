package org.spongepowered.common.item.inventory;

import net.minecraft.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.transaction.InventoryOperationResult;
import org.spongepowered.api.item.inventory.type.Inventory2D;

import java.util.Optional;


public class Inventory2DImpl extends OrderedInventoryImpl implements Inventory2D {

    public Inventory2DImpl(Inventory parent, Container container, int startIndex, int endIndex) {
        super(parent, container, startIndex, endIndex);
    }

    @Override
    public Optional<ItemStack> poll(SlotPos pos) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> poll(SlotPos pos, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek(SlotPos pos) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek(SlotPos pos, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InventoryOperationResult set(SlotPos pos, ItemStack stack) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Slot> getSlot(SlotPos pos) {
        // TODO Auto-generated method stub
        return null;
    }

}
