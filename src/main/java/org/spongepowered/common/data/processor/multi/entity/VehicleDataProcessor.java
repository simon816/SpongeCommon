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

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableVehicleData;
import org.spongepowered.api.data.manipulator.mutable.entity.VehicleData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeVehicleData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeValue;
import org.spongepowered.common.data.value.mutable.SpongeValue;

import java.util.Optional;

public class VehicleDataProcessor extends AbstractSpongeDataProcessor<VehicleData, ImmutableVehicleData> {

    public VehicleDataProcessor() {
        registerValueProcessor(Keys.VEHICLE, net.minecraft.entity.Entity.class, new VehicleProcessor());
        registerValueProcessor(Keys.BASE_VEHICLE, net.minecraft.entity.Entity.class, new BaseVehicleProcessor());
    }

    @Override
    public Optional<VehicleData> fill(DataContainer container, final VehicleData vehicleData) {
        if (!container.contains(Keys.VEHICLE.getQuery(), Keys.BASE_VEHICLE.getQuery())) {
            return Optional.empty();
        } else {
            EntitySnapshot vehicle = container.getSerializable(Keys.VEHICLE.getQuery(), EntitySnapshot.class).get();
            EntitySnapshot baseVehicle = container.getSerializable(Keys.BASE_VEHICLE.getQuery(), EntitySnapshot.class).get();
            return Optional.of(vehicleData.set(Keys.VEHICLE, vehicle).set(Keys.BASE_VEHICLE, baseVehicle));
        }
    }

    @Override
    public VehicleData createManipulator() {
        return new SpongeVehicleData();
    }

    private static class VehicleProcessor extends KeyValueProcessor<net.minecraft.entity.Entity, EntitySnapshot, Value<EntitySnapshot>> {

        @Override
        protected boolean hasData(net.minecraft.entity.Entity entity) {
            return entity.ridingEntity != null;
        }

        @Override
        protected boolean remove(net.minecraft.entity.Entity entity) {
            if (entity.isRiding()) {
                entity.dismountRidingEntity();
                return true;
            }
            return false;
        }

        @Override
        protected Value<EntitySnapshot> constructValue(EntitySnapshot defaultValue) {
            return new SpongeValue<>(Keys.VEHICLE, defaultValue);
        }

        @Override
        protected boolean set(net.minecraft.entity.Entity container, EntitySnapshot value) {
            return ((Entity) container).setVehicle(value.restore().orElse(null)).isSuccessful();
        }

        @Override
        protected Optional<EntitySnapshot> get(net.minecraft.entity.Entity container) {
            Entity entity = (Entity) container.ridingEntity;
            if (entity == null) {
                return Optional.empty();
            } else {
                return Optional.of(entity.createSnapshot());
            }
        }

        @Override
        protected ImmutableValue<EntitySnapshot> constructImmutableValue(EntitySnapshot value) {
            return new ImmutableSpongeValue<>(Keys.VEHICLE, value);
        }

    }

    private static class BaseVehicleProcessor extends KeyValueProcessor<net.minecraft.entity.Entity, EntitySnapshot, Value<EntitySnapshot>> {

        @Override
        protected boolean hasData(net.minecraft.entity.Entity entity) {
            return entity.isRiding();
        }

        @Override
        protected Value<EntitySnapshot> constructValue(EntitySnapshot defaultValue) {
            return new SpongeValue<>(Keys.BASE_VEHICLE, defaultValue);
        }

        @Override
        protected boolean set(net.minecraft.entity.Entity container, EntitySnapshot value) {
            return ((Entity) container).setVehicle(value.restore().orElse(null)).isSuccessful();
        }

        @Override
        protected Optional<EntitySnapshot> get(net.minecraft.entity.Entity container) {
            return Optional.ofNullable(container.getLowestRidingEntity() == null ? null : ((Entity) container.getLowestRidingEntity()).createSnapshot());
        }

        @Override
        protected ImmutableValue<EntitySnapshot> constructImmutableValue(EntitySnapshot value) {
            return new ImmutableSpongeValue<>(Keys.BASE_VEHICLE, value);
        }

    }
}
