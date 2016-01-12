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

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableBreathingData;
import org.spongepowered.api.data.manipulator.mutable.entity.BreathingData;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeBreathingData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.value.SpongeValueFactory;
import org.spongepowered.common.interfaces.entity.IMixinEntityLivingBase;

import java.util.Optional;

public class BreathingDataProcessor extends AbstractSpongeDataProcessor<BreathingData, ImmutableBreathingData> {

    public BreathingDataProcessor() {
        registerValueProcessor(Keys.MAX_AIR, EntityLivingBase.class, new MaxAirProcessor());
        registerValueProcessor(Keys.REMAINING_AIR, EntityLivingBase.class, new RemainingAirProcessor());
    }

    @Override
    protected BreathingData createManipulator() {
        return new SpongeBreathingData(300, 300);
    }

    @Override
    public Optional<BreathingData> fill(DataContainer container, BreathingData breathingData) {
        breathingData.set(Keys.MAX_AIR, getData(container, Keys.MAX_AIR));
        breathingData.set(Keys.REMAINING_AIR, getData(container, Keys.REMAINING_AIR));
        return Optional.of(breathingData);
    }

    @Override
    public DataTransactionResult remove(DataHolder dataHolder) {
        return DataTransactionResult.failNoData();
    }

    private static class RemainingAirProcessor extends KeyValueProcessor2<EntityLivingBase, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(EntityLivingBase entity) {
            return entity.isInWater();
        }

        @Override
        protected Optional<Integer> get(EntityLivingBase entity) {
            return Optional.of(entity.getAir());
        }

        @Override
        protected boolean set(EntityLivingBase entity, Integer air) {
            entity.setAir(air);
            return true;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.REMAINING_AIR)
                    .defaultValue(300)
                    .minimum(-20)
                    .maximum(Integer.MAX_VALUE)
                    .actualValue(defaultValue)
                    .build();
        }

    }

    private static class MaxAirProcessor extends KeyValueProcessor2<EntityLivingBase, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(EntityLivingBase entity) {
            return entity.isInWater();
        }

        @Override
        protected Optional<Integer> get(EntityLivingBase entity) {
            return Optional.of(((IMixinEntityLivingBase) entity).getMaxAir());
        }

        @Override
        protected boolean set(EntityLivingBase entity, Integer maxAir) {
            ((IMixinEntityLivingBase) entity).setMaxAir(maxAir);
            return true;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.MAX_AIR)
                    .defaultValue(300)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .actualValue(defaultValue)
                    .build();
        }

    }
}
