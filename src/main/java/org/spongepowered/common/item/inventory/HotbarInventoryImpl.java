package org.spongepowered.common.item.inventory;

import net.minecraft.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.Hotbar;

public class HotbarInventoryImpl extends Inventory2DImpl implements Hotbar {

    public HotbarInventoryImpl(Inventory parent, Container container, int startIndex, int endIndex) {
        super(parent, container, startIndex, endIndex);
    }

    @Override
    public int getSelectedSlotIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setSelectedSlotIndex(int index) {
        // TODO Auto-generated method stub

    }

}
