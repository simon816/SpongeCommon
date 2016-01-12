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

import com.google.common.collect.Sets;
import net.minecraft.tileentity.TileEntityChest;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.block.ImmutableConnectedDirectionData;
import org.spongepowered.api.data.manipulator.mutable.block.ConnectedDirectionData;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.Direction;
import org.spongepowered.common.data.manipulator.mutable.block.SpongeConnectedDirectionData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.value.mutable.SpongeSetValue;
import org.spongepowered.common.data.value.mutable.SpongeValue;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TileConnectedDirectionDataProcessor
        extends AbstractSpongeDataProcessor<ConnectedDirectionData, ImmutableConnectedDirectionData> {

    public TileConnectedDirectionDataProcessor() {
        registerValueProcessor(Keys.CONNECTED_DIRECTIONS, TileEntityChest.class, new DirectionsProcessor());
        registerValueProcessor(Keys.CONNECTED_NORTH, TileEntityChest.class, new NorthProcessor());
        registerValueProcessor(Keys.CONNECTED_EAST, TileEntityChest.class, new EastProcessor());
        registerValueProcessor(Keys.CONNECTED_SOUTH, TileEntityChest.class, new SouthProcessor());
        registerValueProcessor(Keys.CONNECTED_WEST, TileEntityChest.class, new WestProcessor());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<ConnectedDirectionData> fill(DataContainer container, ConnectedDirectionData m) {
        Optional<List<?>> dirs = container.getList(Keys.CONNECTED_DIRECTIONS.getQuery());
        if (dirs.isPresent()) {
            m.set(Keys.CONNECTED_DIRECTIONS, Sets.newHashSet((List<Direction>) dirs.get()));
            return Optional.of(m);
        }
        return Optional.empty();
    }

    @Override
    protected ConnectedDirectionData createManipulator() {
        return new SpongeConnectedDirectionData();
    }

    private static class DirectionsProcessor extends KeyValueProcessor2<TileEntityChest, Set<Direction>, SetValue<Direction>> {

        @Override
        protected SetValue<Direction> constructValue(Set<Direction> value) {
            return new SpongeSetValue<>(Keys.CONNECTED_DIRECTIONS, Sets.newHashSet(), value);
        }

        @Override
        protected Optional<Set<Direction>> get(TileEntityChest chest) {
            Set<Direction> directions = Sets.newHashSet();
            chest.checkForAdjacentChests();
            if (chest.adjacentChestZNeg != null) {
                directions.add(Direction.NORTH);
            }
            if (chest.adjacentChestXPos != null) {
                directions.add(Direction.EAST);
            }
            if (chest.adjacentChestZPos != null) {
                directions.add(Direction.SOUTH);
            }
            if (chest.adjacentChestXNeg != null) {
                directions.add(Direction.WEST);
            }
            if (directions.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(directions);
        }
    }

    private static class NorthProcessor extends KeyValueProcessor2<TileEntityChest, Boolean, Value<Boolean>> {

        @Override
        protected Value<Boolean> constructValue(Boolean value) {
            return new SpongeValue<>(Keys.CONNECTED_NORTH, false, value);
        }

        @Override
        protected Optional<Boolean> get(TileEntityChest chest) {
            chest.checkForAdjacentChests();
            if (chest.adjacentChestZNeg != null) {
                return Optional.of(true);
            }
            return Optional.empty();
        }
    }

    private static class EastProcessor extends KeyValueProcessor2<TileEntityChest, Boolean, Value<Boolean>> {

        @Override
        protected Value<Boolean> constructValue(Boolean value) {
            return new SpongeValue<>(Keys.CONNECTED_EAST, false, value);
        }

        @Override
        protected Optional<Boolean> get(TileEntityChest chest) {
            chest.checkForAdjacentChests();
            if (chest.adjacentChestXPos != null) {
                return Optional.of(true);
            }
            return Optional.empty();
        }

    }

    private static class SouthProcessor extends KeyValueProcessor2<TileEntityChest, Boolean, Value<Boolean>> {

        @Override
        protected Value<Boolean> constructValue(Boolean value) {
            return new SpongeValue<>(Keys.CONNECTED_SOUTH, false, value);
        }

        @Override
        protected Optional<Boolean> get(TileEntityChest chest) {
            chest.checkForAdjacentChests();
            if (chest.adjacentChestZPos != null) {
                return Optional.of(true);
            }
            return Optional.empty();
        }

    }

    private static class WestProcessor extends KeyValueProcessor2<TileEntityChest, Boolean, Value<Boolean>> {

        @Override
        protected Value<Boolean> constructValue(Boolean value) {
            return new SpongeValue<>(Keys.CONNECTED_WEST, false, value);
        }

        @Override
        protected Optional<Boolean> get(TileEntityChest chest) {
            chest.checkForAdjacentChests();
            if (chest.adjacentChestXNeg != null) {
                return Optional.of(true);
            }
            return Optional.empty();
        }

    }

}
