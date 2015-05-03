package org.spongepowered.common.item.inventory;

import net.minecraft.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.slot.OutputSlot;


public class OutputSlotImpl extends SlotImpl implements OutputSlot {

    public OutputSlotImpl(Inventory parent, Container container, int slotIndex) {
        super(parent, container, slotIndex);
    }

}
