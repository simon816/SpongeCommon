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
package org.spongepowered.common.data.processor.multi.entity;

import static org.spongepowered.common.data.util.DataUtil.getData;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFallingBlockData;
import org.spongepowered.api.data.manipulator.mutable.entity.FallingBlockData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeFallingBlockData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.util.DataConstants;
import org.spongepowered.common.data.value.SpongeValueFactory;
import org.spongepowered.common.data.value.mutable.SpongeValue;

import java.util.Optional;

public class FallingBlockDataProcessor extends AbstractSpongeDataProcessor<FallingBlockData, ImmutableFallingBlockData> {

    public FallingBlockDataProcessor() {
        registerValueProcessor(Keys.FALL_DAMAGE_PER_BLOCK, EntityFallingBlock.class, new FallDamageBlockProcessor());
        registerValueProcessor(Keys.MAX_FALL_DAMAGE, EntityFallingBlock.class, new MaxFallDamageProcessor());
        registerValueProcessor(Keys.FALLING_BLOCK_STATE, EntityFallingBlock.class, new FallingBlockProcessor());
        registerValueProcessor(Keys.CAN_PLACE_AS_BLOCK, EntityFallingBlock.class, new PlaceAsBlockProcessor());
        registerValueProcessor(Keys.CAN_DROP_AS_ITEM, EntityFallingBlock.class, new DropAsItemProcessor());
        registerValueProcessor(Keys.FALL_TIME, EntityFallingBlock.class, new FallTimeProcessor());
        registerValueProcessor(Keys.FALLING_BLOCK_CAN_HURT_ENTITIES, EntityFallingBlock.class, new BlockHurtEntitiesProcessor());
    }

    @Override
    public FallingBlockData createManipulator() {
        return new SpongeFallingBlockData();
    }

    @Override
    public Optional<FallingBlockData> fill(DataContainer container, FallingBlockData fallingBlockData) {
        fallingBlockData.set(Keys.FALL_DAMAGE_PER_BLOCK, getData(container, Keys.FALL_DAMAGE_PER_BLOCK));
        fallingBlockData.set(Keys.MAX_FALL_DAMAGE, getData(container, Keys.MAX_FALL_DAMAGE));
        fallingBlockData.set(Keys.FALLING_BLOCK_STATE, getData(container, Keys.FALLING_BLOCK_STATE));
        fallingBlockData.set(Keys.CAN_PLACE_AS_BLOCK, getData(container, Keys.CAN_PLACE_AS_BLOCK));
        fallingBlockData.set(Keys.CAN_DROP_AS_ITEM, getData(container, Keys.CAN_DROP_AS_ITEM));
        fallingBlockData.set(Keys.FALL_TIME, getData(container, Keys.FALL_TIME));
        fallingBlockData.set(Keys.FALLING_BLOCK_CAN_HURT_ENTITIES, getData(container, Keys.FALLING_BLOCK_CAN_HURT_ENTITIES));
        return Optional.of(fallingBlockData);
    }

    private static class FallDamageBlockProcessor extends KeyValueProcessor<EntityFallingBlock, Double, MutableBoundedValue<Double>> {

        @Override
        protected boolean hasData(EntityFallingBlock holder) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Double> constructValue(Double value) {
            return SpongeValueFactory.boundedBuilder(Keys.FALL_DAMAGE_PER_BLOCK)
                    .actualValue(value)
                    .defaultValue(DataConstants.DEFAULT_FALLING_BLOCK_FALL_DAMAGE_PER_BLOCK)
                    .minimum(0d)
                    .maximum(Double.MAX_VALUE)
                    .build();
        }

        @Override
        protected boolean set(EntityFallingBlock container, Double value) {
            container.fallHurtAmount = value.floatValue();
            return true;
        }

        @Override
        protected Optional<Double> get(EntityFallingBlock container) {
            return Optional.of((double) container.fallHurtAmount);
        }

        @Override
        protected ImmutableValue<Double> constructImmutableValue(Double value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class MaxFallDamageProcessor extends KeyValueProcessor<EntityFallingBlock, Double, MutableBoundedValue<Double>> {

        @Override
        protected boolean hasData(EntityFallingBlock holder) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Double> constructValue(Double value) {
            return SpongeValueFactory.boundedBuilder(Keys.MAX_FALL_DAMAGE)
                    .actualValue(value)
                    .defaultValue(DataConstants.DEFAULT_FALLING_BLOCK_MAX_FALL_DAMAGE)
                    .minimum(0d)
                    .maximum(Double.MAX_VALUE)
                    .build();
        }

        @Override
        protected boolean set(EntityFallingBlock container, Double value) {
            container.fallHurtMax = value.intValue();
            return true;
        }

        @Override
        protected Optional<Double> get(EntityFallingBlock container) {
            return Optional.of((double) container.fallHurtMax);
        }

        @Override
        protected ImmutableValue<Double> constructImmutableValue(Double value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class FallingBlockProcessor extends KeyValueProcessor<EntityFallingBlock, BlockState, Value<BlockState>> {

        @Override
        protected boolean hasData(EntityFallingBlock holder) {
            return true;
        }

        @Override
        protected Value<BlockState> constructValue(BlockState value) {
            return new SpongeValue<>(Keys.FALLING_BLOCK_STATE, DataConstants.Catalog.DEFAULT_FALLING_BLOCK_BLOCKSTATE, value);
        }

        @Override
        protected boolean set(EntityFallingBlock container, BlockState value) {
            container.fallTile = (IBlockState) value;
            return true;
        }

        @Override
        protected Optional<BlockState> get(EntityFallingBlock container) {
            return Optional.of((BlockState) container.fallTile);
        }

        @Override
        protected ImmutableValue<BlockState> constructImmutableValue(BlockState value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class PlaceAsBlockProcessor extends KeyValueProcessor<EntityFallingBlock, Boolean, Value<Boolean>> {

        @Override
        protected boolean hasData(EntityFallingBlock holder) {
            return true;
        }

        @Override
        protected Value<Boolean> constructValue(Boolean value) {
            return new SpongeValue<>(Keys.CAN_PLACE_AS_BLOCK, DataConstants.DEFAULT_FALLING_BLOCK_CAN_PLACE_AS_BLOCK, value);
        }

        @Override
        protected boolean set(EntityFallingBlock container, Boolean value) {
            container.canSetAsBlock = value;
            return true;
        }

        @Override
        protected Optional<Boolean> get(EntityFallingBlock container) {
            return Optional.of(container.canSetAsBlock);
        }

        @Override
        protected ImmutableValue<Boolean> constructImmutableValue(Boolean value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class DropAsItemProcessor extends KeyValueProcessor<EntityFallingBlock, Boolean, Value<Boolean>> {

        @Override
        protected boolean hasData(EntityFallingBlock holder) {
            return true;
        }

        @Override
        protected Value<Boolean> constructValue(Boolean value) {
            return new SpongeValue<>(Keys.CAN_DROP_AS_ITEM, DataConstants.DEFAULT_FALLING_BLOCK_CAN_DROP_AS_ITEM, value);
        }

        @Override
        protected boolean set(EntityFallingBlock container, Boolean value) {
            container.shouldDropItem = value;
            return true;
        }

        @Override
        protected Optional<Boolean> get(EntityFallingBlock container) {
            return Optional.of(container.shouldDropItem);
        }

        @Override
        protected ImmutableValue<Boolean> constructImmutableValue(Boolean value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class FallTimeProcessor extends KeyValueProcessor<EntityFallingBlock, Integer, Value<Integer>> {

        @Override
        protected boolean hasData(EntityFallingBlock holder) {
            return true;
        }

        @Override
        protected Value<Integer> constructValue(Integer value) {
            return new SpongeValue<>(Keys.FALL_TIME, DataConstants.DEFAULT_FALLING_BLOCK_FALL_TIME, value);
        }

        @Override
        protected boolean set(EntityFallingBlock container, Integer value) {
            container.fallTime = value;
            return true;
        }

        @Override
        protected Optional<Integer> get(EntityFallingBlock container) {
            return Optional.of(container.fallTime);
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class BlockHurtEntitiesProcessor extends KeyValueProcessor<EntityFallingBlock, Boolean, Value<Boolean>> {

        @Override
        protected boolean hasData(EntityFallingBlock holder) {
            return true;
        }

        @Override
        protected Value<Boolean> constructValue(Boolean value) {
            return new SpongeValue<>(Keys.FALLING_BLOCK_CAN_HURT_ENTITIES, DataConstants.DEFAULT_FALLING_BLOCK_CAN_HURT_ENTITIES, value);
        }

        @Override
        protected boolean set(EntityFallingBlock container, Boolean value) {
            container.hurtEntities = value;
            return true;
        }

        @Override
        protected Optional<Boolean> get(EntityFallingBlock container) {
            return Optional.of(container.hurtEntities);
        }

        @Override
        protected ImmutableValue<Boolean> constructImmutableValue(Boolean value) {
            return constructValue(value).asImmutable();
        }

    }

}
