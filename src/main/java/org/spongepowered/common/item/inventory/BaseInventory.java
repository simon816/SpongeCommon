package org.spongepowered.common.item.inventory;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import net.minecraft.inventory.Container;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryOperationResult;
import org.spongepowered.api.text.translation.Translatable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BaseInventory implements Inventory {

    protected final Container container;
    private final int[] allSlots;
    private final Inventory parent;
    private final List<Inventory> subInventories = Lists.newArrayList();
    private int currentItrPtr;
    private final Map<Integer, Slot> slotMap = Maps.newHashMap();

    public BaseInventory(Inventory parent, Container container) {
        this(parent, container, 0, container.inventorySlots.size());
    }

    public BaseInventory(Inventory parent, Container container, int startIndex, int endIndex) {
        this.parent = parent;
        this.container = container;
        this.allSlots = Ints.toArray(ContiguousSet.create(Range.closedOpen(startIndex, endIndex), DiscreteDomain.integers()));
    }

    @Override
    public Iterator<Inventory> iterator() {
        return this.subInventories.iterator();
    }

    @Override
    public Translatable getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Inventory parent() {
        return this.parent;
    }

    @Override
    public <T extends Inventory> Iterable<T> slots() {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return new AbstractIterator<T>() {

                    private int slotPtr = 0;

                    @SuppressWarnings("unchecked")
                    @Override
                    protected T computeNext() {
                        if (this.slotPtr >= BaseInventory.this.allSlots.length) {
                            return this.endOfData();
                            //                            this.slotPtr = -1;
                        }
                        //                        if (this.slotPtr == -1) {
                        //                            if (nextInventoryIterator != null && !this.nextInventoryIterator.hasNext()) {
                        //                                this.nextInventoryIterator = null;
                        //                            }
                        //                            if (this.nextInventoryIterator == null) {
                        //                                if (this.subInvIterator == null) {
                        //                                    this.subInvIterator = BaseInventory.this.subInventories.iterator();
                        //                                }
                        //                                if (!subInvIterator.hasNext()) {
                        //                                    return this.endOfData();
                        //                                }
                        //                                this.nextInventoryIterator = this.subInvIterator.next().slots().iterator();
                        //                            }
                        //                            return (T) this.nextInventoryIterator.next();
                        //                        }
                        int slotIndex = BaseInventory.this.allSlots[this.slotPtr++];
                        return (T) BaseInventory.this.getSlotAt(slotIndex);
                    }
                };
            }
        };
    }

    protected Slot getSlotAt(int slotIndex) {
        Slot slot = this.slotMap.get(slotIndex);
        if (slot == null) {
            slot = new SlotImpl(this, this.container, slotIndex);
            this.slotMap.put(slotIndex, slot);
        }
        return slot;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T first() {
        if (this.subInventories.size() == 0) {
            return (T) this;
        }
        return (T) this.subInventories.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T next() {
        // TODO currentItrPtr cannot be reset
        if (this.subInventories.size() <= this.currentItrPtr) {
            return EmptyInventoryImpl.instance();
        }
        return (T) this.subInventories.get(this.currentItrPtr++);
    }

    @Override
    public Optional<ItemStack> poll() {
        return this.pollSlots(this.allSlots);
    }

    @Override
    public Optional<ItemStack> poll(int limit) {
        return this.pollSlots(this.allSlots, limit);
    }

    protected Optional<ItemStack> pollSlots(int[] slots) {
        if (slots == null || slots.length == 0) {
            return Optional.empty();
        }
        for (int slotIndex : slots) {
            net.minecraft.inventory.Slot slot = this.container.getSlot(slotIndex);
            net.minecraft.item.ItemStack stack = slot.getStack();
            if (stack != null) {
                slot.putStack(stack);
                return Optional.of((ItemStack) stack);
            }
        }
        return Optional.empty();
    }

    protected Optional<ItemStack> pollSlots(int[] slots, int limit) {
        if (slots == null || slots.length == 0) {
            return Optional.empty();
        }
        net.minecraft.item.ItemStack finalStack = null;
        int remaining = limit;
        for (int slot : slots) {
            net.minecraft.item.ItemStack stack = this.container.getSlot(slot).getStack();
            if (stack == null) {
                continue;
            }
            if (finalStack == null) {
                finalStack = stack.copy();
                finalStack.stackSize = 0;
            }
            if (stack.getItem() != finalStack.getItem()) {
                continue;
            }
            int removedCount = this.container.getSlot(slot).decrStackSize(Math.min(stack.stackSize, remaining)).stackSize;
            finalStack.stackSize += removedCount;
            remaining -= removedCount;
            if (remaining == 0) {
                break;
            }
        }
        return Optional.ofNullable((ItemStack) finalStack);
    }

    @Override
    public Optional<ItemStack> peek() {
        return this.peekSlots(this.allSlots);
    }

    @Override
    public Optional<ItemStack> peek(int limit) {
        return this.peekSlots(this.allSlots, limit);
    }

    protected Optional<ItemStack> peekSlots(int[] slots) {
        if (slots == null || slots.length == 0) {
            return Optional.empty();
        }
        for (int slotIndex : slots) {
            net.minecraft.inventory.Slot slot = this.container.getSlot(slotIndex);
            net.minecraft.item.ItemStack stack = slot.getStack();
            if (stack != null) {
                return Optional.of((ItemStack) stack);
            }
        }
        return Optional.empty();
    }

    protected Optional<ItemStack> peekSlots(int[] slots, int limit) {
        if (slots == null || slots.length == 0) {
            return Optional.empty();
        }
        net.minecraft.item.ItemStack finalStack = null;
        int remaining = limit;
        for (int slot : slots) {
            net.minecraft.item.ItemStack stack = this.container.getSlot(slot).getStack();
            if (stack == null) {
                continue;
            }
            if (finalStack == null) {
                finalStack = stack.copy();
                finalStack.stackSize = 0;
            }
            if (stack.getItem() != finalStack.getItem()) {
                continue;
            }
            int diff = Math.min(stack.stackSize, remaining);
            finalStack.stackSize += diff;
            remaining -= diff;
            if (remaining == 0) {
                break;
            }
        }
        return Optional.ofNullable((ItemStack) finalStack);
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
        for (int index : this.allSlots) {
            this.container.getSlot(index).putStack(null);
        }
    }

    @Override
    public int size() {
        int size = 0;
        for (int index : this.allSlots) {
            if (this.container.getSlot(index).getHasStack()) {
                size += 1;
            }
        }
        return size;
    }

    @Override
    public int totalItems() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int capacity() {
        return this.allSlots.length;
    }

    @Override
    public boolean isEmpty() {
        return this.size() > 0;
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
        return Collections.emptyList();
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Collection<T> getProperties(Class<T> property) {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Inventory child, Class<T> property, Object key) {
        // TODO Auto-generated method stub
        return EmptyInventoryImpl.instance();
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Class<T> property, Object key) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Class<?>... types) {
        Set<Class<?>> query = Sets.newHashSet(types);
        if (this.subInventories.size() == 0) {
            return EmptyInventoryImpl.instance();
        }
        for (Inventory inv : this.subInventories) {
            for (Class<?> queryClass : query) {
                if (queryClass.isAssignableFrom(inv.getClass())) {
                    return (T) inv;
                }
            }
        }
        return EmptyInventoryImpl.instance();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(ItemType... types) {
        Set<ItemType> query = Sets.newHashSet(types);
        for (int slotIndex : this.allSlots) {
            net.minecraft.item.ItemStack stack = this.container.getSlot(slotIndex).getStack();
            if (stack == null) {
                continue;
            }
            if (query.contains((ItemType) stack.getItem())) {
                return (T) this.getSlotAt(slotIndex);
            }
        }
        return EmptyInventoryImpl.instance();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(ItemStack... types) {
        Set<ItemStack> query = Sets.newHashSet(types);
        for (int slotIndex : this.allSlots) {
            net.minecraft.item.ItemStack stack = this.container.getSlot(slotIndex).getStack();
            if (stack == null) {
                continue;
            }
            for (ItemStack queryStack : query) {
                if (queryStack.getQuantity() == -1) {
                    if (stack.getItem() == queryStack.getItem()) {
                        return (T) this.getSlotAt(slotIndex);
                    }
                    continue;
                }
                if (net.minecraft.item.ItemStack.areItemStacksEqual(stack, (net.minecraft.item.ItemStack) queryStack)) {
                    return (T) this.getSlotAt(slotIndex);
                }
            }
        }
        return EmptyInventoryImpl.instance();
    }

    @Override
    public <T extends Inventory> T query(InventoryProperty<?, ?>... props) {
        // TODO Auto-generated method stub
        return EmptyInventoryImpl.instance();
    }

    @Override
    public <T extends Inventory> T query(Translatable... names) {
        // TODO Auto-generated method stub
        return EmptyInventoryImpl.instance();
    }

    @Override
    public <T extends Inventory> T query(String... names) {
        // TODO Auto-generated method stub
        return EmptyInventoryImpl.instance();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Object... args) {
        T combined = EmptyInventoryImpl.instance();
        for (Object arg : args) {
            Inventory result;
            if (arg instanceof Class) {
                result = this.query((Class<?>) arg);
            } else if (arg instanceof ItemType) {
                result = this.query((ItemType) arg);
            } else if (arg instanceof ItemStack) {
                result = this.query((ItemStack) arg);
            } else if (arg instanceof InventoryProperty) {
                result = this.query((InventoryProperty<?, ?>) arg);
            } else if (arg instanceof Translatable) {
                result = this.query((Translatable) arg);
            } else if (arg instanceof String) {
                result = this.query((String) arg);
            } else {
                // Ignore
                continue;
            }
            if (result == EmptyInventoryImpl.INSTANCE) {
                continue;
            }
            if (combined == EmptyInventoryImpl.INSTANCE) {
                combined = (T) new BaseInventory(this, this.container, 0, 0);
            }
            // TODO is the result a sub inventory?
            ((BaseInventory) combined).addSubInventory(result);
        }
        return combined;
    }

    protected <T extends Inventory> T addSubInventory(T inventory) {
        this.subInventories.add(inventory);
        return inventory;
    }

}
