package org.spongepowered.common.item.inventory;

import net.minecraft.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.crafting.CraftingOutput;


public class CraftingOutputImpl extends OutputSlotImpl implements CraftingOutput {

    public CraftingOutputImpl(Inventory parent, Container container, int slotIndex) {
        super(parent, container, slotIndex);
    }

}
