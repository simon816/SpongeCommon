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
package org.spongepowered.common.data.processor.multi.block;

import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
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

import java.util.Set;

public class ConnectedDirectionDataProcessor extends
        AbstractSpongeDataProcessor<ConnectedDirectionData, ImmutableConnectedDirectionData> {

    public ConnectedDirectionDataProcessor() {
        registerValueProcessor(Keys.CONNECTED_DIRECTIONS, ItemStack.class, new DirectionsProcessor());
        registerValueProcessor(Keys.CONNECTED_EAST, ItemStack.class, new EastProcessor());
        registerValueProcessor(Keys.CONNECTED_NORTH, ItemStack.class, new NorthProcessor());
        registerValueProcessor(Keys.CONNECTED_SOUTH, ItemStack.class, new SouthProcessor());
        registerValueProcessor(Keys.CONNECTED_WEST, ItemStack.class, new WestProcessor());

    }

    @Override
    protected ConnectedDirectionData createManipulator() {
        return new SpongeConnectedDirectionData();
    }

    private static class DirectionsProcessor extends KeyValueProcessor2<ItemStack, Set<Direction>, SetValue<Direction>> {

        @Override
        public SetValue<Direction> constructValue(Set<Direction> defaultValue) {
            return new SpongeSetValue<>(Keys.CONNECTED_DIRECTIONS, Sets.newHashSet(), defaultValue);
        }
    }

    private static class EastProcessor extends KeyValueProcessor2<ItemStack, Boolean, Value<Boolean>> {

        @Override
        public Value<Boolean> constructValue(Boolean value) {
            return new SpongeValue<>(Keys.CONNECTED_EAST, false, value);
        }

    }

    private static class NorthProcessor extends KeyValueProcessor2<ItemStack, Boolean, Value<Boolean>> {

        @Override
        public Value<Boolean> constructValue(Boolean value) {
            return new SpongeValue<>(Keys.CONNECTED_NORTH, false, value);
        }

    }

    private static class SouthProcessor extends KeyValueProcessor2<ItemStack, Boolean, Value<Boolean>> {

        @Override
        public Value<Boolean> constructValue(Boolean value) {
            return new SpongeValue<>(Keys.CONNECTED_SOUTH, false, value);
        }

    }

    private static class WestProcessor extends KeyValueProcessor2<ItemStack, Boolean, Value<Boolean>> {

        @Override
        public Value<Boolean> constructValue(Boolean value) {
            return new SpongeValue<>(Keys.CONNECTED_WEST, false, value);
        }

    }

}
