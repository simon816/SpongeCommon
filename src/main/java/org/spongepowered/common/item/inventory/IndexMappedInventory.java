package org.spongepowered.common.item.inventory;

import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.InventoryOperationResult;
import org.spongepowered.api.text.translation.Translatable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class IndexMappedInventory implements EquipmentInventory {

    public interface Getter {

        ItemStack getItem(Integer index);
    }

    public interface Setter {

        void setItem(Integer index, ItemStack item);
    }

    private final Map<Integer, ?> indexMap;
    private final Getter getter;
    private final Setter setter;
    private Set<Integer> validIndexes;

    public IndexMappedInventory(Map<Integer, ?> map, Getter getter, Setter setter) {
        this.indexMap = map;
        this.validIndexes = map.keySet();
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index) {
        Optional<ItemStack> stack = this.peek(index);
        if (stack.isPresent()) {
            this.setter.setItem(index.getValue(), null);
        }
        return stack;
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index, int limit) {
//        if (stack.isPresent() && stack.get().getQuantity() > limit) {
//            ItemStack leftOver = (ItemStack) ((net.minecraft.item.ItemStack) stack.get()).copy();
//            leftOver.setQuantity(stack.get().getQuantity() - limit);
//            this.setter.setItem(index.getValue(), leftOver);
//            stack.get().setQuantity(limit);
//        }
        // TODO
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index) {
        if (!this.validIndexes.contains(index)) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.getter.getItem(index.getValue()));
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index, int limit) {
        if (!this.validIndexes.contains(index)) {
            return Optional.empty();
        }
        Optional<ItemStack> stack = Optional.ofNullable(this.getter.getItem(index.getValue()));
        if (stack.isPresent() && stack.get().getQuantity() > limit) {
            ItemStack saturatedStack = (ItemStack) ((net.minecraft.item.ItemStack) stack.get()).copy();
            saturatedStack.setQuantity(limit);
            return Optional.of(saturatedStack);
        }
        return stack;
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

    @Override
    public Inventory parent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Inventory> Iterable<T> slots() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Inventory> T first() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Inventory> T next() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> poll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> poll(int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek(int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean offer(ItemStack stack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public InventoryOperationResult set(ItemStack stack) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int totalItems() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int capacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(ItemStack stack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(ItemType type) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getMaxStackSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setMaxStackSize(int size) {
        // TODO Auto-generated method stub

    }

    @Override
    public <T extends InventoryProperty<?, ?>> Collection<T> getProperties(Inventory child, Class<T> property) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Collection<T> getProperties(Class<T> property) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Inventory child, Class<T> property, Object key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Class<T> property, Object key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Inventory> T query(Class<?>... types) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Inventory> T query(ItemType... types) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Inventory> T query(ItemStack... types) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Inventory> T query(InventoryProperty<?, ?>... props) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Inventory> T query(Translatable... names) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Inventory> T query(String... names) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Inventory> T query(Object... args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<Inventory> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Translatable getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ArmorEquipable> getCarrier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> poll(EquipmentSlotType equipmentType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> poll(EquipmentSlotType equipmentType, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> poll(EquipmentType equipmentType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> poll(EquipmentType equipmentType, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek(EquipmentSlotType equipmentType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek(EquipmentSlotType equipmentType, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek(EquipmentType equipmentType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> peek(EquipmentType equipmentType, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InventoryOperationResult set(EquipmentSlotType equipmentType, ItemStack stack) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InventoryOperationResult set(EquipmentType equipmentType, ItemStack stack) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Slot> getSlot(EquipmentSlotType equipmentType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Slot> getSlot(EquipmentType equipmentType) {
        // TODO Auto-generated method stub
        return null;
    }

}
