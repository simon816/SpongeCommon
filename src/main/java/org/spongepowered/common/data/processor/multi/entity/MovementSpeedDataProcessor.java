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

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableMovementSpeedData;
import org.spongepowered.api.data.manipulator.mutable.entity.MovementSpeedData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeMovementSpeedData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.util.DataConstants;
import org.spongepowered.common.data.value.mutable.SpongeValue;

import java.util.Optional;

public class MovementSpeedDataProcessor extends AbstractSpongeDataProcessor<MovementSpeedData, ImmutableMovementSpeedData> {

    public MovementSpeedDataProcessor() {
        registerValueProcessor(Keys.WALKING_SPEED, EntityPlayer.class, new WalkSpeedProcessor());
        registerValueProcessor(Keys.FLYING_SPEED, EntityPlayer.class, new FlySpeedProcessor());
    }

    @Override
    public MovementSpeedData createManipulator() {
        return new SpongeMovementSpeedData();
    }

    @Override
    public Optional<MovementSpeedData> fill(DataContainer container, MovementSpeedData movementSpeedData) {
        movementSpeedData.set(Keys.WALKING_SPEED, getData(container, Keys.WALKING_SPEED));
        movementSpeedData.set(Keys.FLYING_SPEED, getData(container, Keys.FLYING_SPEED));
        return Optional.of(movementSpeedData);
    }

    private static class WalkSpeedProcessor extends KeyValueProcessor<EntityPlayer, Double, Value<Double>> {

        @Override
        protected boolean hasData(EntityPlayer entity) {
            return true;
        }

        @Override
        protected Value<Double> constructValue(Double defaultValue) {
            return new SpongeValue<>(Keys.WALKING_SPEED, 0.7D);
        }

        @Override
        protected ImmutableValue<Double> constructImmutableValue(Double value) {
            return constructValue(value).asImmutable();
        }

        @Override
        protected boolean set(EntityPlayer container, Double value) {
            container.capabilities.walkSpeed = value.floatValue();
            container.sendPlayerAbilities();
            return true;
        }

        @Override
        protected Optional<Double> get(EntityPlayer container) {
            return Optional.of(((double) container.capabilities.getWalkSpeed()));
        }

    }

    private static class FlySpeedProcessor extends KeyValueProcessor<EntityPlayer, Double, Value<Double>> {

        @Override
        protected boolean hasData(EntityPlayer entity) {
            return true;
        }

        @Override
        protected Value<Double> constructValue(Double value) {
            return new SpongeValue<>(Keys.FLYING_SPEED, DataConstants.DEFAULT_FLYING_SPEED, value);
        }

        @Override
        protected ImmutableValue<Double> constructImmutableValue(Double value) {
            return constructValue(value).asImmutable();
        }

        @Override
        protected boolean set(EntityPlayer container, Double value) {
            container.capabilities.flySpeed = value.floatValue();
            container.sendPlayerAbilities();
            return true;
        }

        @Override
        protected Optional<Double> get(EntityPlayer container) {
            return Optional.of(((double) container.capabilities.getFlySpeed()));
        }

    }

}
