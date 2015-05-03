package org.spongepowered.common.mixin.core.item.inventory;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.interfaces.IMixinInventoryCrafting;
import org.spongepowered.common.item.inventory.GridInventoryImpl;

@Mixin(InventoryCrafting.class)
public class MixinInventoryCrafting implements IMixinInventoryCrafting {

    @Shadow
    private int inventoryWidth;

    @Shadow
    private Container eventHandler;

    private GridInventory gridInventory;

    @Override
    public GridInventory getGridInventory() {
        if (this.gridInventory == null) {
            int startIndex = -1;
            int endIndex = -1;
            for (int i = 0, len = this.eventHandler.inventorySlots.size(); i < len; i++) {
                if (this.eventHandler.getSlot(i).inventory == this) {
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    endIndex = i;
                }
            }
            // TODO parent
            this.gridInventory = new GridInventoryImpl(null, this.eventHandler, this.inventoryWidth, startIndex, endIndex);
        }
        return this.gridInventory;
    }
}
