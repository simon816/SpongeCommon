package org.spongepowered.common.item.inventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.inventory.Container;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.transaction.InventoryOperationResult;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class EquipmentInventoryImpl extends OrderedInventoryImpl implements EquipmentInventory {

    public static final Map<Integer, EquipmentType> COMMON_INDEX_MAP = new ImmutableMap.Builder<Integer, EquipmentType>()
            .put(0, EquipmentTypes.EQUIPPED)
            .put(1, EquipmentTypes.BOOTS)
            .put(2, EquipmentTypes.LEGGINGS)
            .put(3, EquipmentTypes.CHESTPLATE)
            .put(4, EquipmentTypes.HEADWEAR)
            .build();
    private final ArmorEquipable carrier;

    private final Map<EquipmentType, int[]> slotsPerType = Maps.newHashMap();

    public EquipmentInventoryImpl(Inventory parent, ArmorEquipable carrier, Container container, Map<Integer, EquipmentType> equipmentMap) {
        super(parent, container, Collections.min(equipmentMap.keySet()), Collections.max(equipmentMap.keySet()));
        this.carrier = carrier;
        for (Map.Entry<Integer, EquipmentType> entry : equipmentMap.entrySet()) {
            int[] arr = this.slotsPerType.get(entry.getValue());
            if (arr == null) {
                arr = new int[] {};
                this.slotsPerType.put(entry.getValue(), arr);
            } else {
                System.arraycopy(new int[] {entry.getKey()}, 0, arr, arr.length - 1, 1);
            }
        }
    }

    @Override
    public Optional<ArmorEquipable> getCarrier() {
        return Optional.ofNullable(this.carrier);
    }

    @Override
    public Optional<ItemStack> poll(EquipmentSlotType equipmentType) {
        return this.poll(equipmentType.getValue());
    }

    @Override
    public Optional<ItemStack> poll(EquipmentSlotType equipmentType, int limit) {
        return this.poll(equipmentType.getValue(), limit);
    }

    @Override
    public Optional<ItemStack> poll(EquipmentType equipmentType) {
        return this.pollSlots(this.slotsPerType.get(equipmentType));
    }

    @Override
    public Optional<ItemStack> poll(EquipmentType equipmentType, int limit) {
        return this.pollSlots(this.slotsPerType.get(equipmentType), limit);
    }

    @Override
    public Optional<ItemStack> peek(EquipmentSlotType equipmentType) {
        return this.peek(equipmentType.getValue());
    }

    @Override
    public Optional<ItemStack> peek(EquipmentSlotType equipmentType, int limit) {
        return this.peek(equipmentType.getValue(), limit);
    }

    @Override
    public Optional<ItemStack> peek(EquipmentType equipmentType) {
        return this.peekSlots(this.slotsPerType.get(equipmentType));
    }

    @Override
    public Optional<ItemStack> peek(EquipmentType equipmentType, int limit) {
        return this.peekSlots(this.slotsPerType.get(equipmentType), limit);
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
        return this.getSlot(equipmentType.getValue());
    }

    @Override
    public Optional<Slot> getSlot(EquipmentType equipmentType) {
        int[] slots = this.slotsPerType.get(equipmentType);
        if (slots == null || slots.length == 0) {
            return Optional.empty();
        }
        return Optional.of(this.getSlotAt(slots[0]));
    }

}
