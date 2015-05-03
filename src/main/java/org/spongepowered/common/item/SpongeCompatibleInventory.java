package org.spongepowered.common.item;

import org.spongepowered.api.item.inventory.Inventory;

public interface SpongeCompatibleInventory<T extends Inventory> {

    T copy();

}
