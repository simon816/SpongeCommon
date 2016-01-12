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
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFoodData;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeFoodData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.util.DataConstants;
import org.spongepowered.common.data.value.SpongeValueFactory;

import java.util.Optional;

public class FoodDataProcessor extends AbstractSpongeDataProcessor<FoodData, ImmutableFoodData> {

    public FoodDataProcessor() {
        registerValueProcessor(Keys.FOOD_LEVEL, EntityPlayer.class, new FoodLevelProcessor());
        registerValueProcessor(Keys.SATURATION, EntityPlayer.class, new SaturationProcessor());
        registerValueProcessor(Keys.EXHAUSTION, EntityPlayer.class, new ExhaustionProcessor());
    }

    @Override
    public FoodData createManipulator() {
        return new SpongeFoodData(20, 20, 0);
    }

    @Override
    public Optional<FoodData> fill(DataContainer container, FoodData foodData) {
        foodData.set(Keys.FOOD_LEVEL, getData(container, Keys.FOOD_LEVEL));
        foodData.set(Keys.SATURATION, getData(container, Keys.SATURATION));
        foodData.set(Keys.EXHAUSTION, getData(container, Keys.EXHAUSTION));
        return Optional.of(foodData);
    }

    private static class FoodLevelProcessor extends KeyValueProcessor<EntityPlayer, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(EntityPlayer holder) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.FOOD_LEVEL)
                    .defaultValue(DataConstants.DEFAULT_FOOD_LEVEL)
                    .minimum(0)
                    .maximum(20)
                    .actualValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(EntityPlayer container, Integer value) {
            container.getFoodStats().setFoodLevel(value);
            return true;
        }

        @Override
        protected Optional<Integer> get(EntityPlayer container) {
            return Optional.of(container.getFoodStats().getFoodLevel());
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class SaturationProcessor extends KeyValueProcessor<EntityPlayer, Double, MutableBoundedValue<Double>> {

        @Override
        protected boolean hasData(EntityPlayer holder) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Double> constructValue(Double defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.SATURATION)
                    .defaultValue(DataConstants.DEFAULT_SATURATION)
                    .minimum(0D)
                    .maximum(5.0)
                    .actualValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(EntityPlayer container, Double value) {
            container.getFoodStats().foodSaturationLevel = value.floatValue();
            return true;
        }

        @Override
        protected Optional<Double> get(EntityPlayer container) {
            return Optional.of((double) container.getFoodStats().getSaturationLevel());
        }

        @Override
        protected ImmutableValue<Double> constructImmutableValue(Double value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class ExhaustionProcessor extends KeyValueProcessor<EntityPlayer, Double, MutableBoundedValue<Double>> {

        @Override
        protected boolean hasData(EntityPlayer holder) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Double> constructValue(Double defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.EXHAUSTION)
                    .defaultValue(DataConstants.DEFAULT_EXHAUSTION)
                    .minimum(0D)
                    .maximum(4.0)
                    .actualValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(EntityPlayer container, Double value) {
            container.getFoodStats().foodExhaustionLevel = value.floatValue();
            return true;
        }

        @Override
        protected Optional<Double> get(EntityPlayer container) {
            return Optional.of((double) container.getFoodStats().foodExhaustionLevel);
        }

        @Override
        protected ImmutableValue<Double> constructImmutableValue(Double value) {
            return constructValue(value).asImmutable();
        }

    }

}
