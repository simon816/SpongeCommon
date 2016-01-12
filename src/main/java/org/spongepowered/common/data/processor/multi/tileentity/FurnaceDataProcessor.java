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
package org.spongepowered.common.data.processor.multi.tileentity;

import net.minecraft.tileentity.TileEntityFurnace;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableFurnaceData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.FurnaceData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.World;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.data.manipulator.mutable.tileentity.SpongeFurnaceData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.util.ImplementationRequiredForTest;
import org.spongepowered.common.data.value.SpongeValueFactory;

import java.util.Optional;

@ImplementationRequiredForTest
public class FurnaceDataProcessor extends AbstractSpongeDataProcessor<FurnaceData, ImmutableFurnaceData> {


    public FurnaceDataProcessor() {
        // time (int) the fuel item already burned
        registerValueProcessor(Keys.PASSED_BURN_TIME, TileEntityFurnace.class, new BurnTimeProcessor());
        // time (int) the fuel can burn until its depleted
        registerValueProcessor(Keys.MAX_BURN_TIME, TileEntityFurnace.class, new MaxBurnTimeProcessor());
        // time (int) the item already cooked
        registerValueProcessor(Keys.PASSED_COOK_TIME, TileEntityFurnace.class, new CookTimeProcessor());
        // time (int) the item have to cook
        registerValueProcessor(Keys.MAX_COOK_TIME, TileEntityFurnace.class, new MaxCookTimeProcessor());
    }

    private static Cause cause;
    private static TileEntityFurnace updateIfNeeded(TileEntityFurnace furnace, int maxBurnTime) {
        if (cause == null) { // lazy evaluation because of tests
            cause = Cause.source(SpongeImpl.getPlugin()).build();
        }
        final boolean needsUpdate = !furnace.isBurning() && maxBurnTime > 0 || furnace.isBurning() && maxBurnTime == 0;
        if (needsUpdate) {
            final World world = (World) furnace.getWorld();
            world.setBlockType(furnace.getPos().getX(), furnace.getPos().getY(),
                    furnace.getPos().getZ(), maxBurnTime > 0 ? BlockTypes.LIT_FURNACE : BlockTypes.FURNACE, cause);
            furnace = (TileEntityFurnace) furnace.getWorld().getTileEntity(furnace.getPos());
        }
        return furnace;
    }

    @Override
    public FurnaceData createManipulator() {
        return new SpongeFurnaceData();
    }

    @Override
    public Optional<FurnaceData> fill(DataContainer container, FurnaceData furnaceData) {
        if (!container.contains(Keys.PASSED_BURN_TIME.getQuery()) ||
                !container.contains(Keys.MAX_BURN_TIME.getQuery()) ||
                !container.contains(Keys.PASSED_COOK_TIME.getQuery()) ||
                !container.contains(Keys.MAX_COOK_TIME.getQuery())) {
            return Optional.empty();
        }

        final int passedBurnTime = container.getInt(Keys.PASSED_BURN_TIME.getQuery()).get();
        final int maxBurnTime = container.getInt(Keys.MAX_BURN_TIME.getQuery()).get();
        final int passedCookTime = container.getInt(Keys.PASSED_COOK_TIME.getQuery()).get();
        final int maxCookTime = container.getInt(Keys.MAX_COOK_TIME.getQuery()).get();

        furnaceData.set(Keys.PASSED_BURN_TIME, passedBurnTime);
        furnaceData.set(Keys.MAX_BURN_TIME, maxBurnTime);
        furnaceData.set(Keys.PASSED_COOK_TIME, passedCookTime);
        furnaceData.set(Keys.MAX_COOK_TIME, maxCookTime);

        return Optional.of(furnaceData);
    }

    private static class BurnTimeProcessor extends KeyValueProcessor<TileEntityFurnace, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(TileEntityFurnace holder) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.PASSED_BURN_TIME)
                    .minimum(0)
                    .maximum(1600)
                    .defaultValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(TileEntityFurnace container, Integer value) {
            if (value > container.getField(1)) { // value cannot be higher than
                                                 // the maximum
                return false;
            }
            container = updateIfNeeded(container, container.getField(1));
            container.setField(0, container.getField(1) - value);
            return true;
        }

        @Override
        protected Optional<Integer> get(TileEntityFurnace container) {
            // When the furnace is not burning, the value is 0
            return Optional.of(container.isBurning() ? container.getField(1) - container.getField(0) : 0);
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return SpongeValueFactory.boundedBuilder(Keys.PASSED_BURN_TIME)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .build()
                    .asImmutable();
        }
    }

    private static class MaxBurnTimeProcessor extends KeyValueProcessor<TileEntityFurnace, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(TileEntityFurnace holder) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.MAX_BURN_TIME)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .defaultValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(TileEntityFurnace container, Integer value) {
            container = updateIfNeeded(container, value);
            container.setField(1, value);
            return true;
        }

        @Override
        protected Optional<Integer> get(TileEntityFurnace container) {
            return Optional.of(container.getField(1));
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return SpongeValueFactory.boundedBuilder(Keys.MAX_BURN_TIME)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .defaultValue(1000)
                    .actualValue(value)
                    .build()
                    .asImmutable();
        }
    }

    private static class CookTimeProcessor extends KeyValueProcessor<TileEntityFurnace, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(TileEntityFurnace holder) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.PASSED_COOK_TIME)
                    .minimum(0)
                    .maximum(200) // TODO
                    .defaultValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(TileEntityFurnace container, Integer value) {
            // The passedCookTime of nothing cannot be set | Cannot be higher
            // than the maximum
            if (container.getStackInSlot(0) == null || value > container.getField(3)) {
                return false;
            }
            container = updateIfNeeded(container, container.getField(1));
            container.setField(2, value);
            return true;
        }

        @Override
        protected Optional<Integer> get(TileEntityFurnace container) {
            // The passedCookTime of nothing cannot be set
            return Optional.of(container.getStackInSlot(0) != null ? container.getField(2) : 0);
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return SpongeValueFactory.boundedBuilder(Keys.PASSED_COOK_TIME)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .defaultValue(200)
                    .actualValue(value)
                    .build()
                    .asImmutable();
        }

    }

    private static class MaxCookTimeProcessor extends KeyValueProcessor<TileEntityFurnace, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(TileEntityFurnace holder) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.MAX_COOK_TIME)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .defaultValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(TileEntityFurnace container, Integer value) {
            if (container.getStackInSlot(0) == null) {
                return false; // Item cannot be null, the time depends on it
            }
            container = updateIfNeeded(container, container.getField(1));
            container.setField(3, value);
            return true;
        }

        @Override
        protected Optional<Integer> get(TileEntityFurnace container) {
            // Item cannot be null, the time depends on it
            return Optional.of(container.getStackInSlot(0) != null ? container.getField(3) : 0);
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return SpongeValueFactory.boundedBuilder(Keys.MAX_COOK_TIME)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .defaultValue(200)
                    .actualValue(value)
                    .build()
                    .asImmutable();
        }

    }
}
