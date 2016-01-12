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

import net.minecraft.tileentity.TileEntityBanner;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableBannerData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BannerData;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.PatternListValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.data.manipulator.mutable.tileentity.SpongeBannerData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.util.DataConstants;
import org.spongepowered.common.data.value.immutable.ImmutableSpongePatternListValue;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeValue;
import org.spongepowered.common.data.value.mutable.SpongePatternListValue;
import org.spongepowered.common.data.value.mutable.SpongeValue;
import org.spongepowered.common.interfaces.block.tile.IMixinBanner;

import java.util.List;
import java.util.Optional;

public class TileEntityBannerDataProcessor extends AbstractSpongeDataProcessor<BannerData, ImmutableBannerData> {

    public TileEntityBannerDataProcessor() {
        registerValueProcessor(Keys.BANNER_PATTERNS, TileEntityBanner.class, new PatternsProcessor());
        registerValueProcessor(Keys.BANNER_BASE_COLOR, TileEntityBanner.class, new BaseColorProcessor());
    }

    @Override
    public BannerData createManipulator() {
        return new SpongeBannerData();
    }

    @Override
    public Optional<BannerData> fill(DataContainer container, BannerData bannerData) {
        if (container.contains(Keys.BANNER_PATTERNS.getQuery()) || container.contains(Keys.BANNER_BASE_COLOR.getQuery())) {
            List<PatternLayer> layers = container.getSerializableList(Keys.BANNER_PATTERNS.getQuery(), PatternLayer.class).get();
            String colorId = container.getString(Keys.BANNER_BASE_COLOR.getQuery()).get();
            DyeColor color = Sponge.getRegistry().getType(DyeColor.class, colorId).get();
            bannerData.set(Keys.BANNER_BASE_COLOR, color);
            bannerData.set(Keys.BANNER_PATTERNS, layers);
            return Optional.of(bannerData);
        }
        return Optional.empty();
    }

    private static class PatternsProcessor extends KeyValueProcessor<TileEntityBanner, List<PatternLayer>, PatternListValue> {

        @Override
        protected boolean hasData(TileEntityBanner holder) {
            return true;
        }

        @Override
        protected PatternListValue constructValue(List<PatternLayer> actualValue) {
            return new SpongePatternListValue(Keys.BANNER_PATTERNS, actualValue);
        }

        @Override
        protected boolean set(TileEntityBanner container, List<PatternLayer> value) {
            if (!container.getWorld().isRemote) { // This avoids a client crash
                                                  // because clientside.
                ((IMixinBanner) container).setLayers(value);
                return true;
            }
            return false;
        }

        @Override
        protected Optional<List<PatternLayer>> get(TileEntityBanner container) {
            return Optional.of(((IMixinBanner) container).getLayers());
        }

        @Override
        protected ImmutableValue<List<PatternLayer>> constructImmutableValue(List<PatternLayer> value) {
            return new ImmutableSpongePatternListValue(Keys.BANNER_PATTERNS, value);
        }

    }

    private static class BaseColorProcessor extends KeyValueProcessor<TileEntityBanner, DyeColor, Value<DyeColor>> {

        @Override
        protected boolean hasData(TileEntityBanner holder) {
            return true;
        }

        @Override
        protected Value<DyeColor> constructValue(DyeColor actualValue) {
            return new SpongeValue<>(Keys.BANNER_BASE_COLOR, DataConstants.Catalog.DEFAULT_BANNER_BASE, actualValue);
        }

        @Override
        protected boolean set(TileEntityBanner container, DyeColor value) {
            if (!container.getWorld().isRemote) {
                ((IMixinBanner) container).setBaseColor(value);
                return true;
            }
            return false;
        }

        @Override
        protected Optional<DyeColor> get(TileEntityBanner container) {
            return Optional.of(((IMixinBanner) container).getBaseColor());
        }

        @Override
        protected ImmutableValue<DyeColor> constructImmutableValue(DyeColor value) {
            return ImmutableSpongeValue.cachedOf(Keys.BANNER_BASE_COLOR, DataConstants.Catalog.DEFAULT_BANNER_BASE, value);
        }

    }
}
