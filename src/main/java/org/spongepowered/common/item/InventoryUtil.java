package org.spongepowered.common.item;

import com.google.common.base.Optional;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import java.util.ArrayList;
import java.util.List;

public class InventoryUtil {

    public static <T extends Inventory> T copy(T inventory) {
        if (inventory instanceof SpongeCompatibleInventory) {
            return ((SpongeCompatibleInventory<T>) inventory).copy();
        }
        return null;
    }

    // Subtract a from b
    public static List<ItemStack> subtract(Inventory a, Inventory b) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        Iterable<Slot> slots = a.slots();
        for (Slot slot : slots) {
            Optional<ItemStack> opItem = slot.peek();
            if (opItem.isPresent()) {
                opItem = b.<Slot>query(opItem.get().getItem()).peek();
                if (opItem.isPresent()) {
                    items.add(opItem.get());
                }
            }
        }
        return items;
    }

}
