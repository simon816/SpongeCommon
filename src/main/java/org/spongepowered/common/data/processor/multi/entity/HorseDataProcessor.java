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

import net.minecraft.entity.passive.EntityHorse;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableHorseData;
import org.spongepowered.api.data.manipulator.mutable.entity.HorseData;
import org.spongepowered.api.data.type.HorseColor;
import org.spongepowered.api.data.type.HorseColors;
import org.spongepowered.api.data.type.HorseStyle;
import org.spongepowered.api.data.type.HorseStyles;
import org.spongepowered.api.data.type.HorseVariant;
import org.spongepowered.api.data.type.HorseVariants;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeHorseData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.processor.common.HorseUtils;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeValue;
import org.spongepowered.common.data.value.mutable.SpongeValue;
import org.spongepowered.common.entity.SpongeHorseColor;
import org.spongepowered.common.entity.SpongeHorseStyle;
import org.spongepowered.common.entity.SpongeHorseVariant;

import java.util.Optional;

public class HorseDataProcessor extends AbstractSpongeDataProcessor<HorseData, ImmutableHorseData> {

    public HorseDataProcessor() {
        registerValueProcessor(Keys.HORSE_COLOR, EntityHorse.class, new ColorProcessor());
        registerValueProcessor(Keys.HORSE_STYLE, EntityHorse.class, new StyleProcessor());
        registerValueProcessor(Keys.HORSE_VARIANT, EntityHorse.class, new VariantProcessor());
    }

    @Override
    public HorseData createManipulator() {
        return new SpongeHorseData();
    }

    @Override
    public Optional<HorseData> fill(DataContainer container, HorseData horseData) {
        horseData.set(Keys.HORSE_COLOR, HorseUtils.getHorseColor(container));
        horseData.set(Keys.HORSE_STYLE, HorseUtils.getHorseStyle(container));
        horseData.set(Keys.HORSE_VARIANT, HorseUtils.getHorseVariant(container));

        return Optional.of(horseData);
    }

    private static class ColorProcessor extends KeyValueProcessor<EntityHorse, HorseColor, Value<HorseColor>> {

        @Override
        protected boolean hasData(EntityHorse holder) {
            return true;
        }

        @Override
        protected Value<HorseColor> constructValue(HorseColor defaultValue) {
            return new SpongeValue<>(Keys.HORSE_COLOR, defaultValue);
        }

        @Override
        protected boolean set(EntityHorse container, HorseColor value) {
            final SpongeHorseStyle style = (SpongeHorseStyle) HorseUtils.getHorseStyle(container);
            container.setHorseVariant(HorseUtils.getInternalVariant((SpongeHorseColor) value, style));
            return true;
        }

        @Override
        protected Optional<HorseColor> get(EntityHorse container) {
            return Optional.of(HorseUtils.getHorseColor(container));
        }

        @Override
        protected ImmutableValue<HorseColor> constructImmutableValue(HorseColor value) {
            return ImmutableSpongeValue.cachedOf(Keys.HORSE_COLOR, HorseColors.WHITE, value);
        }
    }

    private static class StyleProcessor extends KeyValueProcessor<EntityHorse, HorseStyle, Value<HorseStyle>> {

        @Override
        protected boolean hasData(EntityHorse holder) {
            return true;
        }

        @Override
        protected Value<HorseStyle> constructValue(HorseStyle defaultValue) {
            return new SpongeValue<>(Keys.HORSE_STYLE, defaultValue);
        }

        @Override
        protected boolean set(EntityHorse container, HorseStyle value) {
            SpongeHorseColor color = (SpongeHorseColor) HorseUtils.getHorseColor(container);
            container.setHorseVariant(HorseUtils.getInternalVariant(color, (SpongeHorseStyle) value));
            return true;
        }

        @Override
        protected Optional<HorseStyle> get(EntityHorse container) {
            return Optional.of(HorseUtils.getHorseStyle(container));
        }

        @Override
        protected ImmutableValue<HorseStyle> constructImmutableValue(HorseStyle value) {
            return ImmutableSpongeValue.cachedOf(Keys.HORSE_STYLE, HorseStyles.NONE, value);
        }
    }

    private static class VariantProcessor extends KeyValueProcessor<EntityHorse, HorseVariant, Value<HorseVariant>> {

        @Override
        protected boolean hasData(EntityHorse holder) {
            return true;
        }

        @Override
        protected Value<HorseVariant> constructValue(HorseVariant defaultValue) {
            return new SpongeValue<>(Keys.HORSE_VARIANT, defaultValue);
        }

        @Override
        protected boolean set(EntityHorse container, HorseVariant value) {
            container.setType(((SpongeHorseVariant) value).getType());
            return true;
        }

        @Override
        protected Optional<HorseVariant> get(EntityHorse container) {
            return Optional.of(HorseUtils.getHorseVariant(container.getType()));
        }

        @Override
        protected ImmutableValue<HorseVariant> constructImmutableValue(HorseVariant value) {
            return ImmutableSpongeValue.cachedOf(Keys.HORSE_VARIANT, HorseVariants.HORSE, value);
        }
    }
}
