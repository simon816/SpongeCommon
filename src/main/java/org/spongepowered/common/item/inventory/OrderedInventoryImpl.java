package org.spongepowered.common.item.inventory;

import net.minecraft.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.InventoryOperationResult;
import org.spongepowered.api.item.inventory.type.OrderedInventory;

import java.util.Optional;

public class OrderedInventoryImpl extends BaseInventory implements OrderedInventory {

    public OrderedInventoryImpl(Inventory parent, Container container, int startIndex, int endIndex) {
        super(parent, container, startIndex, endIndex);
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InventoryOperationResult set(SlotIndex index, ItemStack stack) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Slot> getSlot(SlotIndex index) {
        // TODO Auto-generated method stub
        return null;
    }

}
