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

import static com.google.common.base.Preconditions.checkArgument;
import static org.spongepowered.common.data.util.DataUtil.getData;

import net.minecraft.entity.Entity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableIgniteableData;
import org.spongepowered.api.data.manipulator.mutable.entity.IgniteableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeIgniteableData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.util.DataConstants;
import org.spongepowered.common.data.value.SpongeValueFactory;

import java.util.Optional;

public class IgniteableDataProcessor extends AbstractSpongeDataProcessor<IgniteableData, ImmutableIgniteableData> {

    public IgniteableDataProcessor() {
        registerValueProcessor(Keys.FIRE_TICKS, Entity.class, new FireTickProcessor());
        registerValueProcessor(Keys.FIRE_DAMAGE_DELAY, Entity.class, new DamageDelayProcessor());
    }

    @Override
    public Optional<IgniteableData> fill(DataContainer container, IgniteableData igniteableData) {
        igniteableData.set(Keys.FIRE_TICKS, getData(container, Keys.FIRE_TICKS));
        igniteableData.set(Keys.FIRE_DAMAGE_DELAY, getData(container, Keys.FIRE_DAMAGE_DELAY));
        return Optional.of(igniteableData);
    }

    @Override
    public IgniteableData createManipulator() {
        return new SpongeIgniteableData();
    }

    private static class DamageDelayProcessor extends KeyValueProcessor<Entity, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(Entity entity) {
            return entity.fire > 0;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.FIRE_DAMAGE_DELAY)
                    .defaultValue(20)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .actualValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(Entity container, Integer value) {
            checkArgument(value >= 0, "Fire tick delay must be equal to or greater than zero!");
            container.fireResistance = value;
            return true;
        }

        @Override
        protected Optional<Integer> get(Entity container) {
            return Optional.of(container.fireResistance);
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return constructValue(value).asImmutable();
        }
    }

    private static class FireTickProcessor extends KeyValueProcessor<Entity, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(Entity entity) {
            return entity.fire > 0;
        }

        @Override
        protected boolean remove(Entity entity) {
            if (entity.fire >= DataConstants.MINIMUM_FIRE_TICKS) {
                entity.extinguish();
                return true;
            }
            return false;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.FIRE_TICKS)
                    .defaultValue(DataConstants.DEFAULT_FIRE_TICKS)
                    .minimum(DataConstants.MINIMUM_FIRE_TICKS)
                    .maximum(Integer.MAX_VALUE)
                    .actualValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(Entity container, Integer value) {
            container.fire = value;
            return true;
        }

        @Override
        protected Optional<Integer> get(Entity container) {
            if (container.fire > 0) {
                return Optional.of(container.fire);
            }
            return Optional.empty();
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return constructValue(value).asImmutable();
        }

    }

}
