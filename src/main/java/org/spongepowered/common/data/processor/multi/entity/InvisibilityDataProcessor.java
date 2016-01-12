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

import net.minecraft.entity.Entity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableInvisibilityData;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeInvisibilityData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeValue;
import org.spongepowered.common.data.value.mutable.SpongeValue;
import org.spongepowered.common.entity.EntityUtil;
import org.spongepowered.common.interfaces.entity.IMixinEntity;

import java.util.Optional;

public class InvisibilityDataProcessor
        extends AbstractSpongeDataProcessor<InvisibilityData, ImmutableInvisibilityData> {

    public InvisibilityDataProcessor() {
        registerValueProcessor(Keys.INVISIBLE, Entity.class, new InvisibleProcessor());
        registerValueProcessor(Keys.VANISH, Entity.class, new VanishProcessor());
        registerValueProcessor(Keys.VANISH_IGNORES_COLLISION, Entity.class, new IgnoresCollisionProcessor());
        registerValueProcessor(Keys.VANISH_PREVENTS_TARGETING, Entity.class, new PreventsTargetingProcessor());
    }

    @Override
    protected InvisibilityData createManipulator() {
        return new SpongeInvisibilityData();
    }

    @Override
    public Optional<InvisibilityData> fill(DataContainer container, InvisibilityData invisibilityData) {
        final boolean vanished = container.getBoolean(Keys.VANISH.getQuery()).orElse(false);
        final boolean invisible = container.getBoolean(Keys.INVISIBLE.getQuery()).orElse(false);
        final boolean collision = container.getBoolean(Keys.VANISH_IGNORES_COLLISION.getQuery()).orElse(false);
        final boolean targeting = container.getBoolean(Keys.VANISH_PREVENTS_TARGETING.getQuery()).orElse(false);
        return Optional.of(invisibilityData
                .set(Keys.VANISH, vanished)
                .set(Keys.INVISIBLE, invisible)
                .set(Keys.VANISH_IGNORES_COLLISION, collision)
                .set(Keys.VANISH_PREVENTS_TARGETING, targeting));
    }

    private static class VanishProcessor extends KeyValueProcessor<Entity, Boolean, Value<Boolean>> {

        @Override
        protected Value<Boolean> constructValue(Boolean actualValue) {
            return new SpongeValue<>(Keys.VANISH, false, actualValue);
        }

        @Override
        protected ImmutableValue<Boolean> constructImmutableValue(Boolean value) {
            return ImmutableSpongeValue.cachedOf(Keys.VANISH, false, value);
        }

        @Override
        protected Optional<Boolean> get(Entity container) {
            return Optional.of(((IMixinEntity) container).isVanished());
        }

        @Override
        protected boolean set(Entity container, Boolean value) {
            if (!container.worldObj.isRemote) {
                EntityUtil.toMixin(container).setVanished(value);
                return true;
            }
            return false;
        }
    }

    private static class InvisibleProcessor extends KeyValueProcessor<Entity, Boolean, Value<Boolean>> {

        @Override
        protected boolean hasData(Entity holder) {
            return true;
        }

        @Override
        protected Value<Boolean> constructValue(Boolean actualValue) {
            return new SpongeValue<>(Keys.INVISIBLE, false, actualValue);
        }

        @Override
        protected boolean set(Entity container, Boolean value) {
            if (!container.worldObj.isRemote) {
                container.setInvisible(value);
                return true;
            }
            return false;
        }

        @Override
        protected Optional<Boolean> get(Entity container) {
            return Optional.of(container.isInvisible());
        }

        @Override
        protected ImmutableValue<Boolean> constructImmutableValue(Boolean value) {
            return ImmutableSpongeValue.cachedOf(Keys.INVISIBLE, false, value);
        }

    }

    private static class IgnoresCollisionProcessor extends KeyValueProcessor<Entity, Boolean, Value<Boolean>> {

        @Override
        protected boolean hasData(Entity holder) {
            return true;
        }

        @Override
        protected Value<Boolean> constructValue(Boolean actualValue) {
            return new SpongeValue<>(Keys.VANISH_IGNORES_COLLISION, false, actualValue);
        }

        @Override
        protected boolean set(Entity container, Boolean value) {
            if (!container.worldObj.isRemote) {
                if (!((IMixinEntity) container).isVanished()) {
                    return false;
                }
                ((IMixinEntity) container).setIgnoresCollision(value);
                return true;
            }
            return false;
        }

        @Override
        protected Optional<Boolean> get(Entity container) {
            return Optional.of(((IMixinEntity) container).ignoresCollision());
        }

        @Override
        protected ImmutableValue<Boolean> constructImmutableValue(Boolean value) {
            return ImmutableSpongeValue.cachedOf(Keys.VANISH_IGNORES_COLLISION, false, value);
        }
    }

    private static class PreventsTargetingProcessor extends KeyValueProcessor<Entity, Boolean, Value<Boolean>> {

        @Override
        protected boolean hasData(Entity holder) {
            return true;
        }

        @Override
        protected Value<Boolean> constructValue(Boolean actualValue) {
            return new SpongeValue<>(Keys.VANISH_PREVENTS_TARGETING, false, actualValue);
        }

        @Override
        protected boolean set(Entity container, Boolean value) {
            if (!container.worldObj.isRemote) {
                if (!((IMixinEntity) container).isVanished()) {
                    return false;
                }
                ((IMixinEntity) container).setUntargetable(value);
                return true;
            }
            return false;
        }

        @Override
        protected Optional<Boolean> get(Entity container) {
            return Optional.of(((IMixinEntity) container).isUntargetable());
        }

        @Override
        protected ImmutableValue<Boolean> constructImmutableValue(Boolean value) {
            return ImmutableSpongeValue.cachedOf(Keys.VANISH_PREVENTS_TARGETING, false, value);
        }

    }
}
