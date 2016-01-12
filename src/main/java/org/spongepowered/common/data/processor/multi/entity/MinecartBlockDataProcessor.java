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

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableMinecartBlockData;
import org.spongepowered.api.data.manipulator.mutable.entity.MinecartBlockData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeMinecartBlockData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeValue;
import org.spongepowered.common.data.value.mutable.SpongeValue;

import java.util.Optional;

public class MinecartBlockDataProcessor extends AbstractSpongeDataProcessor<MinecartBlockData, ImmutableMinecartBlockData> {

    public MinecartBlockDataProcessor() {
        registerValueProcessor(Keys.REPRESENTED_BLOCK, EntityMinecart.class, new RepresentedBlockProcessor());
        registerValueProcessor(Keys.OFFSET, EntityMinecart.class, new OffsetProcessor());
    }

    @Override
    public MinecartBlockData createManipulator() {
        return new SpongeMinecartBlockData();
    }

    @Override
    public Optional<MinecartBlockData> fill(DataContainer container, MinecartBlockData data) {
        if (!container.contains(Keys.REPRESENTED_BLOCK.getQuery())
                || !container.contains(Keys.OFFSET.getQuery())) {
            return Optional.empty();
        }

        BlockState block = container.getSerializable(Keys.REPRESENTED_BLOCK.getQuery(), BlockState.class).get();
        int offset = container.getInt(Keys.OFFSET.getQuery()).get();

        data.set(Keys.REPRESENTED_BLOCK, block);
        data.set(Keys.OFFSET, offset);

        return Optional.of(data);
    }

    private static class RepresentedBlockProcessor extends KeyValueProcessor<EntityMinecart, BlockState, Value<BlockState>> {

        @Override
        protected boolean hasData(EntityMinecart entity) {
            return entity.hasDisplayTile();
        }

        @Override
        protected Value<BlockState> constructValue(BlockState value) {
            return new SpongeValue<>(Keys.REPRESENTED_BLOCK, (BlockState) Blocks.AIR.getDefaultState(), value);
        }

        @Override
        protected boolean set(EntityMinecart container, BlockState value) {
            container.setDisplayTile((IBlockState) value);
            return true;
        }

        @Override
        protected Optional<BlockState> get(EntityMinecart container) {
            if (!container.hasDisplayTile()) {
                return Optional.empty();
            }
            return Optional.of((BlockState) container.getDisplayTile());
        }

        @Override
        protected ImmutableValue<BlockState> constructImmutableValue(BlockState value) {
            return new ImmutableSpongeValue<>(Keys.REPRESENTED_BLOCK, (BlockState) Blocks.AIR.getDefaultState(), value);
        }

        @Override
        protected boolean remove(EntityMinecart cart) {
            cart.setHasDisplayTile(false);
            return true;
        }
    }

    private static class OffsetProcessor extends KeyValueProcessor<EntityMinecart, Integer, Value<Integer>> {

        @Override
        protected boolean hasData(EntityMinecart entity) {
            return entity.hasDisplayTile();
        }

        @Override
        protected Value<Integer> constructValue(Integer value) {
            return new SpongeValue<>(Keys.OFFSET, 6, value);
        }

        @Override
        protected boolean set(EntityMinecart container, Integer value) {
            if (!container.hasDisplayTile()) {
                return false;
            }
            container.setDisplayTileOffset(value);
            return true;
        }

        @Override
        protected Optional<Integer> get(EntityMinecart container) {
            return Optional.of(container.getDisplayTileOffset());
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return new ImmutableSpongeValue<>(Keys.OFFSET, 6, value);
        }

        @Override
        protected boolean remove(EntityMinecart cart) {
            cart.setHasDisplayTile(false);
            return true;
        }

    }
}
