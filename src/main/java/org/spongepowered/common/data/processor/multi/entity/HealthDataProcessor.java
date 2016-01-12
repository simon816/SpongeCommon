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

import static org.spongepowered.common.data.util.ComparatorUtil.doubleComparator;
import static org.spongepowered.common.data.util.DataUtil.getData;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableHealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.value.immutable.ImmutableBoundedValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeHealthData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.value.SpongeValueFactory;
import org.spongepowered.common.registry.type.event.DamageSourceRegistryModule;

import java.util.Optional;

public class HealthDataProcessor extends AbstractSpongeDataProcessor<HealthData, ImmutableHealthData> {

    public HealthDataProcessor() {
        registerValueProcessor(Keys.MAX_HEALTH, EntityLivingBase.class, new MaxHealthProcessor());
        registerValueProcessor(Keys.HEALTH, EntityLivingBase.class, new HealthProcessor());
    }

    @Override
    public HealthData createManipulator() {
        return new SpongeHealthData(20, 20);
    }

    @Override
    public Optional<HealthData> fill(DataContainer container, HealthData healthData) {
        if (!container.contains(Keys.MAX_HEALTH.getQuery()) || !container.contains(Keys.HEALTH.getQuery())) {
            return Optional.empty();
        }
        healthData.set(Keys.MAX_HEALTH, getData(container, Keys.MAX_HEALTH));
        healthData.set(Keys.HEALTH, getData(container, Keys.HEALTH));
        return Optional.of(healthData);
    }

    private static class MaxHealthProcessor extends KeyValueProcessor<EntityLivingBase, Double, MutableBoundedValue<Double>> {

        @Override
        protected boolean hasData(EntityLivingBase holder) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Double> constructValue(Double defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.MAX_HEALTH)
                    .defaultValue(20D)
                    .minimum(1D)
                    .maximum(((Float) Float.MAX_VALUE).doubleValue())
                    .actualValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(EntityLivingBase container, Double value) {
            container.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((value).floatValue());
            return true;
        }

        @Override
        protected Optional<Double> get(EntityLivingBase container) {
            return Optional.of((double) container.getMaxHealth());
        }

        @Override
        protected ImmutableBoundedValue<Double> constructImmutableValue(Double value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class HealthProcessor extends KeyValueProcessor<EntityLivingBase, Double, MutableBoundedValue<Double>> {

        @Override
        protected boolean hasData(EntityLivingBase holder) {
            return true;
        }

        @Override
        protected Optional<MutableBoundedValue<Double>> constructValueForHolder(EntityLivingBase entity) {
            final double maxHealth = ((EntityLivingBase) entity).getMaxHealth();
            return Optional.of(SpongeValueFactory.boundedBuilder(Keys.HEALTH)
                    .comparator(doubleComparator())
                    .minimum(0D)
                    .maximum(maxHealth)
                    .defaultValue(maxHealth)
                    .actualValue((double) ((EntityLivingBase) entity).getHealth())
                    .build());
        }

        @Override
        protected MutableBoundedValue<Double> constructValue(Double value) {
            return SpongeValueFactory.boundedBuilder(Keys.HEALTH)
                    .comparator(doubleComparator())
                    .minimum(0D)
                    .maximum(((Float) Float.MAX_VALUE).doubleValue())
                    .defaultValue(20D)
                    .actualValue(value)
                    .build();
        }

        @Override
        protected boolean set(EntityLivingBase entity, Double health) {
            entity.setHealth(health.floatValue());
            if (health == 0) {
                entity.attackEntityFrom(DamageSourceRegistryModule.IGNORED_DAMAGE_SOURCE, 10000F);
            }
            return true;
        }
        protected DataTransactionResult set2(EntityLivingBase entity, Double value) {
            final DataTransactionResult.Builder builder = DataTransactionResult.builder();
            final double maxHealth = entity.getMaxHealth();
            final ImmutableBoundedValue<Double> newHealthValue = SpongeValueFactory.boundedBuilder(Keys.HEALTH)
                    .defaultValue(maxHealth)
                    .minimum(0D)
                    .maximum(maxHealth)
                    .actualValue(value)
                    .build()
                    .asImmutable();
            final ImmutableBoundedValue<Double> oldHealthValue = constructValueForHolder(entity).get().asImmutable();
            if (value > maxHealth) {
                return DataTransactionResult.errorResult(newHealthValue);
            }
            try {
                entity.setHealth(value.floatValue());
            } catch (Exception e) {
                return DataTransactionResult.errorResult(newHealthValue);
            }
            if (value.floatValue() <= 0.0F) {
                entity.onDeath(DamageSource.generic);
            }
            return builder.success(newHealthValue).replace(oldHealthValue).result(DataTransactionResult.Type.SUCCESS).build();
        }


        @Override
        protected Optional<Double> get(EntityLivingBase container) {
            return Optional.of((double) container.getHealth());
        }

        @Override
        protected ImmutableBoundedValue<Double> constructImmutableValue(Double value) {
            return constructValue(value).asImmutable();
        }

    }

}
