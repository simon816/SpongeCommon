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
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableExperienceHolderData;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import org.spongepowered.api.data.value.immutable.ImmutableBoundedValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeExperienceHolderData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.processor.common.ExperienceHolderUtils;
import org.spongepowered.common.data.value.SpongeValueFactory;

import java.util.Optional;

public class ExperienceHolderDataProcessor extends AbstractSpongeDataProcessor<ExperienceHolderData, ImmutableExperienceHolderData> {

    public ExperienceHolderDataProcessor() {
        registerValueProcessor(Keys.EXPERIENCE_LEVEL, EntityPlayer.class, new XpLevelProcessor());
        registerValueProcessor(Keys.TOTAL_EXPERIENCE, EntityPlayer.class, new TotalXpProcessor());
        registerValueProcessor(Keys.EXPERIENCE_SINCE_LEVEL, EntityPlayer.class, new XpSinceLevelProcessor());
        registerValueProcessor(Keys.EXPERIENCE_FROM_START_OF_LEVEL, EntityPlayer.class, new XpFromLevelStartProcessor());
    }

    @Override
    protected ExperienceHolderData createManipulator() {
        return new SpongeExperienceHolderData();
    }

    @Override
    public Optional<ExperienceHolderData> fill(DataContainer container, ExperienceHolderData experienceHolderData) {
        experienceHolderData.set(Keys.EXPERIENCE_LEVEL, getData(container, Keys.EXPERIENCE_LEVEL));
        experienceHolderData.set(Keys.TOTAL_EXPERIENCE, getData(container, Keys.TOTAL_EXPERIENCE));
        experienceHolderData.set(Keys.EXPERIENCE_SINCE_LEVEL, getData(container, Keys.EXPERIENCE_SINCE_LEVEL));
        return Optional.of(experienceHolderData);
    }

    private static class XpLevelProcessor extends KeyValueProcessor<EntityPlayer, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(EntityPlayer entity) {
            return true;
        }

        @Override
        protected boolean set(EntityPlayer player, Integer value) {
            int totalExp = 0;
            for (int i = 0; i < value; i++) {
                totalExp += ExperienceHolderUtils.getExpBetweenLevels(i);
            }
            player.experienceTotal = totalExp;
            player.experience = 0;
            player.experienceLevel = value;
            return true;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.EXPERIENCE_LEVEL)
                    .defaultValue(0)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .actualValue(defaultValue)
                    .build();
        }

        @Override
        protected Optional<Integer> get(EntityPlayer entity) {
            return Optional.of(entity.experienceLevel);
        }

        @Override
        protected ImmutableBoundedValue<Integer> constructImmutableValue(Integer value) {
            return constructValue(value).asImmutable();
        }
    }

    private static class TotalXpProcessor extends KeyValueProcessor<EntityPlayer, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(EntityPlayer entity) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.TOTAL_EXPERIENCE)
                    .defaultValue(0)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .actualValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(EntityPlayer container, Integer value) {
            int level = -1;

            int experienceForCurrentLevel;
            int experienceAtNextLevel = -1;

            // We work iteratively to get the level. Remember, the level variable contains the CURRENT level and the method
            // calculates what we need to get to the NEXT level, so we work our way up, summing up all these intervals, until
            // we get an experience value that is larger than the value. This gives us our level.
            //
            // If the cumulative experience required for level+1 is still below that (or in the edge case, equal to) our
            // value, we need to go up a level. So, if the boundary is at 7 exp, and we have 7 exp, we need one more loop
            // to increment the level as we are at 100% and therefore should be at level+1.
            do {
                // We need this later.
                experienceForCurrentLevel = experienceAtNextLevel;

                // Increment level, as we know we are at least that level (in the first instance -1 -> 0)
                // and add the next amount of experience to the variable.
                experienceAtNextLevel += ExperienceHolderUtils.getExpBetweenLevels(++level);
            } while (experienceAtNextLevel <= value);

            // Once we're here, we have the correct level. The experience is the decimal fraction that we are through the
            // current level. This is why we require the experienceForCurrentLevel variable, we need the difference between
            // the current value and the beginning of the level.
            container.experience = (float)(value - experienceForCurrentLevel) / ExperienceHolderUtils.getExpBetweenLevels(level);
            container.experienceLevel = level;
            container.experienceTotal = value;
            return true;
        }

        @Override
        protected Optional<Integer> get(EntityPlayer entity) {
            return Optional.of(entity.experienceTotal);
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class XpSinceLevelProcessor extends KeyValueProcessor<EntityPlayer, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(EntityPlayer entity) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.EXPERIENCE_SINCE_LEVEL)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .defaultValue(0)
                    .actualValue(defaultValue)
                    .build();
        }

        @Override
        protected boolean set(EntityPlayer container, Integer value) {
            while (value >= container.xpBarCap()) {
                value -= container.xpBarCap();
            }
            container.experience = (float) value / container.xpBarCap();
            return true;
        }

        @Override
        protected Optional<Integer> get(EntityPlayer entity) {
            return Optional.of((int) (entity.experience * entity.xpBarCap()));
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return constructValue(value).asImmutable();
        }
    }

    private static class XpFromLevelStartProcessor extends KeyValueProcessor<EntityPlayer, Integer, ImmutableBoundedValue<Integer>> {

        @Override
        protected ImmutableBoundedValue<Integer> constructValue(Integer defaultValue) {
            return SpongeValueFactory.boundedBuilder(Keys.EXPERIENCE_FROM_START_OF_LEVEL)
                    .defaultValue(0)
                    .actualValue(defaultValue)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .build()
                    .asImmutable();
        }

        @Override
        protected boolean set(EntityPlayer container, Integer value) {
            return false;
        }

        @Override
        protected Optional<Integer> get(EntityPlayer entity) {
            return Optional.of(entity.xpBarCap());
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return constructValue(value);
        }

    }

}
