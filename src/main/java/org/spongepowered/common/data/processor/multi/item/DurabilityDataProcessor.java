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
package org.spongepowered.common.data.processor.multi.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableDurabilityData;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.data.manipulator.mutable.item.SpongeDurabilityData;
import org.spongepowered.common.data.processor.common.AbstractItemDataProcessor;
import org.spongepowered.common.data.util.NbtDataUtil;
import org.spongepowered.common.data.value.SpongeValueFactory;

import java.util.Optional;

public class DurabilityDataProcessor extends AbstractItemDataProcessor<DurabilityData, ImmutableDurabilityData> {

    public DurabilityDataProcessor() {
        super(input -> input.getItem().isDamageable());
        registerValueProcessor(Keys.ITEM_DURABILITY, ItemStack.class, new DurabilityProcessor());
        registerValueProcessor(Keys.UNBREAKABLE, ItemStack.class, new UnbreakableProcessor());
    }

    @Override
    public DurabilityData createManipulator() {
        return new SpongeDurabilityData();
    }

    @Override
    public Optional<DurabilityData> fill(DataContainer container, DurabilityData durabilityData) {
        final Optional<Integer> durability = container.getInt(Keys.ITEM_DURABILITY.getQuery());
        final Optional<Boolean> unbreakable = container.getBoolean(Keys.UNBREAKABLE.getQuery());
        if (durability.isPresent() && unbreakable.isPresent()) {
            durabilityData.set(Keys.ITEM_DURABILITY, durability.get());
            durabilityData.set(Keys.UNBREAKABLE, unbreakable.get());
            return Optional.of(durabilityData);
        }
        return Optional.empty();
    }

    private static class DurabilityProcessor extends KeyValueProcessor<ItemStack, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(ItemStack itemStack) {
            return itemStack.getItem().isDamageable();
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.ITEM_DURABILITY)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .defaultValue(60)
                    .actualValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(ItemStack container, Integer value) {
            container.setItemDamage(container.getMaxDamage() - value);
            return true;
        }

        @Override
        protected Optional<Integer> get(ItemStack container) {
            if (container.getItem().isDamageable()) {
                return Optional.of(container.getMaxDamage() - container.getItemDamage());
            }
            return Optional.empty();
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class UnbreakableProcessor extends KeyValueProcessor<ItemStack, Boolean, Value<Boolean>> {

        @Override
        protected boolean hasData(ItemStack itemStack) {
            return itemStack.getItem().isDamageable();
        }

        @Override
        protected boolean supports(ItemStack container) {
            return container.getItem().isDamageable();
        }

        @Override
        protected Value<Boolean> constructValue(Boolean defaultValue) {
            return SpongeValueFactory.getInstance().createValue(Keys.UNBREAKABLE, defaultValue, false);
        }

        @Override
        protected boolean set(ItemStack container, Boolean value) {
            if (value) {
                container.setItemDamage(0);
            }
            if (!container.hasTagCompound()) {
                container.setTagCompound(new NBTTagCompound());
            }
            container.getTagCompound().setBoolean(NbtDataUtil.ITEM_UNBREAKABLE, value);
            return true;
        }

        @Override
        protected Optional<Boolean> get(ItemStack container) {
            if (container.hasTagCompound() && container.getTagCompound().hasKey(NbtDataUtil.ITEM_UNBREAKABLE)) {
                return Optional.of(container.getTagCompound().getBoolean(NbtDataUtil.ITEM_UNBREAKABLE));
            }
            return Optional.of(false);
        }

        @Override
        protected ImmutableValue<Boolean> constructImmutableValue(Boolean value) {
            return constructValue(value).asImmutable();
        }

    }
}
