/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypeWorn;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.common.item.inventory.IndexMappedInventory;

import java.util.Optional;

import javax.annotation.Nullable;

// All implementors of ArmorEquipable
//@Mixin({EntityArmorStand.class, EntityGiantZombie.class, EntityPlayerMP.class, EntitySkeleton.class, EntityZombie.class, EntityHuman.class})
@Implements(@Interface(iface = ArmorEquipable.class, prefix = "equipable$"))
public abstract class MixinArmorEquipable extends EntityLivingBase {

    private static class Getter implements IndexMappedInventory.Getter {

        private EntityLivingBase entity;

        public Getter(EntityLivingBase armorEquipable) {
            this.entity = armorEquipable;
        }

        @Override
        public ItemStack getItem(Integer index) {
            // TODO slot index
            return (ItemStack) this.entity.getItemStackFromSlot(EntityEquipmentSlot.values()[index]);
        }
    }

    private static class Setter implements IndexMappedInventory.Setter {

        private EntityLivingBase entity;

        public Setter(EntityLivingBase armorEquipable) {
            this.entity = armorEquipable;
        }

        @Override
        public void setItem(Integer index, ItemStack item) {
            // TODO slot index
            this.entity.setItemStackToSlot(EntityEquipmentSlot.values()[index], (net.minecraft.item.ItemStack) item);
        }
    }

    private final EquipmentInventory armorInventory = new IndexMappedInventory(ImmutableMap.<Integer, EquipmentType>builder()
            .put(EntityEquipmentSlot.MAINHAND.getSlotIndex(), EquipmentTypes.EQUIPPED)
            .put(EntityEquipmentSlot.FEET.getSlotIndex(), EquipmentTypes.BOOTS)
            .put(EntityEquipmentSlot.LEGS.getSlotIndex(), EquipmentTypes.LEGGINGS)
            .put(EntityEquipmentSlot.CHEST.getSlotIndex(), EquipmentTypes.CHESTPLATE)
            .put(EntityEquipmentSlot.HEAD.getSlotIndex(), EquipmentTypes.HEADWEAR)
            .build(), new Getter(this), new Setter(this));

    public MixinArmorEquipable(World worldIn) {
        super(worldIn);
    }

    public Optional<ItemStack> equipable$getHelmet() {
        @Nullable final net.minecraft.item.ItemStack itemStack = this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        //final net.minecraft.item.ItemStack itemStack = this.getEquipmentInSlot(SLOT_HELMET);
        return Optional.ofNullable(itemStack == null ? null : (ItemStack) itemStack.copy());
    }

    public void equipable$setHelmet(ItemStack helmet) {
        this.armorInventory.set(EquipmentTypes.HEADWEAR, helmet);
        if (helmet == null || helmet.getItem() == ItemTypes.NONE) {
            this.setItemStackToSlot(EntityEquipmentSlot.HEAD, null);
        } else {
            this.setItemStackToSlot(EntityEquipmentSlot.HEAD, (net.minecraft.item.ItemStack) helmet.copy());
        }
    }

    public Optional<ItemStack> equipable$getChestplate() {
        @Nullable final net.minecraft.item.ItemStack itemStack = this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        return Optional.ofNullable(itemStack == null ? null : ((ItemStack) itemStack.copy()));
    }

    public void equipable$setChestplate(ItemStack chestplate) {
        if (chestplate == null || chestplate.getItem() == ItemTypes.NONE) {
            this.setItemStackToSlot(EntityEquipmentSlot.CHEST, null);
        } else {
            this.setItemStackToSlot(EntityEquipmentSlot.CHEST, (net.minecraft.item.ItemStack) chestplate.copy());
        }
    }

    public Optional<ItemStack> equipable$getLeggings() {
        @Nullable final net.minecraft.item.ItemStack itemStack = this.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        return Optional.ofNullable(itemStack == null ? null : ((ItemStack) itemStack.copy()));
    }

    public void equipable$setLeggings(ItemStack leggings) {
        if (leggings == null || leggings.getItem() == ItemTypes.NONE) {
            this.setItemStackToSlot(EntityEquipmentSlot.LEGS, null);
        } else {
            this.setItemStackToSlot(EntityEquipmentSlot.LEGS, ((net.minecraft.item.ItemStack) leggings.copy()));
        }
    }

    public Optional<ItemStack> equipable$getBoots() {
        @Nullable final net.minecraft.item.ItemStack itemStack = this.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        return Optional.ofNullable(itemStack == null ? null : ((ItemStack) itemStack.copy()));
    }

    public void equipable$setBoots(ItemStack boots) {
        if (boots == null || boots.getItem() == ItemTypes.NONE) {
            this.setItemStackToSlot(EntityEquipmentSlot.FEET, null);
        } else {
            this.setItemStackToSlot(EntityEquipmentSlot.FEET, ((net.minecraft.item.ItemStack) boots.copy()));
        }
    }

    public Optional<ItemStack> equipable$getItemInHand(HandType handType) {
        checkNotNull(handType, "HandType cannot be null!");
        @Nullable final net.minecraft.item.ItemStack itemStack = this.getHeldItem((EnumHand) (Object) handType);
        return Optional.ofNullable(itemStack == null ? null : ((ItemStack) itemStack.copy()));
    }

    public void equipable$setItemInHand(HandType handType, @Nullable ItemStack itemInHand) {
        checkNotNull(handType, "HandType cannot be null!");
        if (itemInHand == null || itemInHand.getItem() == ItemTypes.NONE) {
            this.setHeldItem((EnumHand) (Object) handType, null);
        } else {
            this.setHeldItem((EnumHand) (Object) handType, ((net.minecraft.item.ItemStack) itemInHand.copy()));
        }
    }

    public boolean equipable$canEquip(EquipmentType type) {
        return type instanceof EquipmentTypeWorn;
    }

    public boolean equipable$canEquip(EquipmentType type, ItemStack equipment) {
        // TODO Auto-generated method stub
        return this.equipable$canEquip(type) && (equipment == null || equipment.getItem() instanceof ItemArmor);
    }

    public Optional<ItemStack> equipable$getEquipped(EquipmentType type) {
        if (type == EquipmentTypes.HEADWEAR) {
            return this.equipable$getHelmet();
        } else if (type == EquipmentTypes.CHESTPLATE) {
            return this.equipable$getChestplate();
        } else if (type == EquipmentTypes.LEGGINGS) {
            return this.equipable$getLeggings();
        } else if (type == EquipmentTypes.BOOTS) {
            return this.equipable$getBoots();
        } else if (type == EquipmentTypes.EQUIPPED) {
            return this.equipable$getItemInHand(HandTypes.MAIN_HAND);
        }
        return Optional.empty();
    }

    public boolean equipable$equip(EquipmentType type, ItemStack equipment) {
        if (type == EquipmentTypes.HEADWEAR) {
            this.equipable$setHelmet(equipment);
            return true;
        } else if (type == EquipmentTypes.CHESTPLATE) {
            this.equipable$setChestplate(equipment);
            return true;
        } else if (type == EquipmentTypes.LEGGINGS) {
            this.equipable$setLeggings(equipment);
            return true;
        } else if (type == EquipmentTypes.BOOTS) {
            this.equipable$setBoots(equipment);
            return true;
        } else if (type == EquipmentTypes.EQUIPPED) {
            this.equipable$setItemInHand(HandTypes.MAIN_HAND, equipment);
            return true;
        }
        return false;
    }

    public CarriedInventory<? extends Carrier> equipable$getInventory() {
        return this.armorInventory;
    }
}
