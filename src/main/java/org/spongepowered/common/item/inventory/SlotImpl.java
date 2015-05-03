package org.spongepowered.common.item.inventory;

import net.minecraft.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;

public class SlotImpl extends BaseInventory implements Slot {

    public SlotImpl(Inventory parent, Container container, int slotIndex) {
        super(parent, container, slotIndex, slotIndex + 1);
    }

    @Override
    public int getStackSize() {
        // TODO Auto-generated method stub
        return 0;
    }

}
