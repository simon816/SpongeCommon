package org.spongepowered.common.item.inventory;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import java.util.Map;
import java.util.Optional;

public class HumanInventoryImpl extends BaseInventory implements PlayerInventory {

    private static final Map<Integer, EquipmentType> EQUIPMENT_MAP = new ImmutableMap.Builder<Integer, EquipmentType>()
            .put(5, EquipmentTypes.HEADWEAR)
            .put(6, EquipmentTypes.CHESTPLATE)
            .put(7, EquipmentTypes.LEGGINGS)
            .put(8, EquipmentTypes.BOOTS)
            .build();

    private final Player player;
    private final Hotbar hotbar;

    public HumanInventoryImpl(Player player, InventoryPlayer inventory, ContainerPlayer container) {
        // 0 to 0 - Doesn't have any leaf nodes
        super(null, container, 0, 0);
        this.player = player;
        // 0 to 4 = crafting inventory
        super.addSubInventory(new CraftingInventoryImpl(this, container, 0, 1, 4));
        // 5 to 8 = equipment inventory
        super.addSubInventory(new EquipmentInventoryImpl(this, player, container, HumanInventoryImpl.EQUIPMENT_MAP));
        // 9 to 35 = main inventory
        super.addSubInventory(new GridInventoryImpl(this, container, 9, 9, 35));
        // 36 to 44 = hotbar inventory
        this.hotbar = super.addSubInventory(new HotbarInventoryImpl(this, container, 36, 44));
    }

    @Override
    public Optional<Player> getCarrier() {
        return Optional.ofNullable(this.player);
    }

    @Override
    public Hotbar getHotbar() {
        return this.hotbar;
    }

}
