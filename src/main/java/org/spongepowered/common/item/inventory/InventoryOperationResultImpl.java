package org.spongepowered.common.item.inventory;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryOperationResult;

import java.util.Collection;
import java.util.Optional;

public class InventoryOperationResultImpl implements InventoryOperationResult {

    public static final InventoryOperationResult FAILURE = new InventoryOperationResultImpl(Type.FAILURE, null, null);

    private Type type;
    private Collection<ItemStack> rejected;
    private Collection<ItemStack> replaced;

    public InventoryOperationResultImpl(Type type, Collection<ItemStack> rejected, Collection<ItemStack> replaced) {
        this.type = type;
        this.rejected = rejected;
        this.replaced = replaced;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public Optional<Collection<ItemStack>> getRejectedItems() {
        return Optional.ofNullable(this.rejected);
    }

    @Override
    public Optional<Collection<ItemStack>> getReplacedItems() {
        return Optional.ofNullable(this.replaced);
    }

}
