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
package org.spongepowered.common.data.processor.common;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.data.ValueProcessor;
import org.spongepowered.common.data.util.DataUtil;

import java.util.Optional;

public abstract class AbstractSingleDataSingleTargetProcessor<Holder, T, V extends BaseValue<T>, M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>>
        extends AbstractSpongeDataProcessor<M, I> {

    private class Processor extends KeyValueProcessorBase<Holder, T, V> {

        @Override
        protected Optional<T> get(Holder holder) {
            return AbstractSingleDataSingleTargetProcessor.this.getVal(holder);
        }

        @Override
        protected boolean hasData(Holder holder) {
            return AbstractSingleDataSingleTargetProcessor.this.hasData(holder);
        }

        @Override
        public int getPriority() {
            return AbstractSingleDataSingleTargetProcessor.this.getPriority();
        }

        @Override
        protected boolean set(Holder holder, T value) {
            return AbstractSingleDataSingleTargetProcessor.this.set(holder, value);
        }

        @Override
        protected boolean remove(Holder holder) {
            return AbstractSingleDataSingleTargetProcessor.this.remove(holder);
        }

        @Override
        protected boolean supports(Holder holder) {
            return AbstractSingleDataSingleTargetProcessor.this.supports(holder);
        }

        @Override
        protected V constructValue(T value) {
            return AbstractSingleDataSingleTargetProcessor.this.constructValue(value);
        }

        @Override
        protected ImmutableValue<T> constructImmutableValue(T value) {
            return AbstractSingleDataSingleTargetProcessor.this.constructImmutableValue(value);
        }

        @Override
        protected Optional<V> constructValueForHolder(Holder holder) {
            return AbstractSingleDataSingleTargetProcessor.this.constructValueForHolder(holder);
        }

    }

    protected final Key<V> key;
    protected final Class<Holder> holderClass;
    private final ValueProcessor<T, V> valueProcessor;

    protected AbstractSingleDataSingleTargetProcessor(Key<V> key, Class<Holder> holderClass) {
        Processor processor = new Processor();
        registerValueProcessor(this.key = checkNotNull(key), this.holderClass = checkNotNull(holderClass), processor);
        this.valueProcessor = new TemporaryValueProcessor<>(key, processor, holderClass);
    }

    // TODO Rename to get(Holder)
    protected abstract Optional<T> getVal(Holder holder);

    protected boolean hasData(Holder holder) {
        return getVal(holder).isPresent();
    }

    protected abstract boolean set(Holder holder, T value);

    protected boolean remove(Holder holder) {
        return removeFrom((ValueContainer<?>) holder).isSuccessful();
    }

    // TODO This is backward compat
    public abstract DataTransactionResult removeFrom(ValueContainer<?> container);

    protected boolean supports(Holder holder) {
        return true;
    }

    protected abstract V constructValue(T value);

    protected abstract ImmutableValue<T> constructImmutableValue(T value);

    protected Optional<V> constructValueForHolder(Holder holder) {
        if (!supports(holder)) {
            return Optional.empty();
        }
        Optional<T> value = getVal(holder);
        if (value.isPresent()) {
            return Optional.of(constructValue(value.get()));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(DataHolder dataHolder) {
        return this.holderClass.isInstance(dataHolder) && supports((Holder) dataHolder);
    }

    // TODO TEMP
    public boolean supports(ValueContainer<?> container) {
        return this.holderClass.isInstance(container) && supports((Holder) container);
    }

    @SuppressWarnings("unchecked")
    private boolean supportsObj(Object object) {
        return this.holderClass.isInstance(object) && supports((Holder) object);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public DataTransactionResult set(DataHolder dataHolder, M manipulator, MergeFunction function) {
        if (supports(dataHolder)) {
            final DataTransactionResult.Builder builder = DataTransactionResult.builder();
            final Optional<M> old = from(dataHolder);
            final M merged = checkNotNull(function).merge(old.orElse(null), manipulator);
            final T newValue = merged.get(this.key).get();
            final V immutableValue = (V) ((Value) merged.getValue(this.key).get()).asImmutable();
            try {
                if (set((Holder) dataHolder, newValue)) {
                    if (old.isPresent()) {
                        builder.replace(old.get().getValues());
                    }
                    return builder.result(DataTransactionResult.Type.SUCCESS).success((ImmutableValue<?>) immutableValue).build();
                } else {
                    return builder.result(DataTransactionResult.Type.FAILURE).reject((ImmutableValue<?>) immutableValue).build();
                }
            } catch (Exception e) {
                SpongeImpl.getLogger().debug("An exception occurred when setting data: ", e);
                return builder.result(DataTransactionResult.Type.ERROR).reject((ImmutableValue<?>) immutableValue).build();
            }
        }
        return DataTransactionResult.failResult(manipulator.getValues());
    }

    @Override
    public Optional<M> fill(DataHolder dataHolder, M manipulator, MergeFunction overlap) {
        if (!supports(dataHolder)) {
            return Optional.empty();
        } else {
            final M merged = checkNotNull(overlap).merge(manipulator.copy(), from(dataHolder).orElse(null));
            return Optional.of(manipulator.set(this.key, merged.get(this.key).get()));
        }
    }

    @Override
    public Optional<M> fill(DataContainer container, M m) {
        m.set(this.key, DataUtil.getData(container, this.key));
        return Optional.of(m);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<I> with(Key<? extends BaseValue<?>> key, Object value, I immutable) {
        if (immutable.supports(key)) {
            return Optional.of(immutable.asMutable().set(this.key, (T) value).asImmutable());
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<M> from(DataHolder dataHolder) {
        if (!supports(dataHolder)) {
            return Optional.empty();
        } else {
            final Optional<T> optional = getVal((Holder) dataHolder);
            if (optional.isPresent()) {
                return Optional.of(createManipulator().set(this.key, optional.get()));
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    // TODO @Override
    public DataTransactionResult offerToStore(ValueContainer<?> container, T value) {
        final ImmutableValue<T> newValue = constructImmutableValue(value);
        if (supportsObj(container)) {
            final DataTransactionResult.Builder builder = DataTransactionResult.builder();
            final Optional<T> oldVal = getVal((Holder) container);
            try {
                if (set((Holder) container, value)) {
                    if (oldVal.isPresent()) {
                        builder.replace(constructImmutableValue(oldVal.get()));
                    }
                    return builder.result(DataTransactionResult.Type.SUCCESS).success(newValue).build();
                }
                return builder.result(DataTransactionResult.Type.FAILURE).reject(newValue).build();
            } catch (Exception e) {
                SpongeImpl.getLogger().debug("An exception occurred when setting data: ", e);
                return builder.result(DataTransactionResult.Type.ERROR).reject(newValue).build();
            }
        }
        return DataTransactionResult.failResult(newValue);
    }

    @Override
    public final DataTransactionResult remove(DataHolder dataHolder) {
        return removeFrom(dataHolder);
    }

    public Key<V> getKey() {
        return this.key;
    }

    public final ValueProcessor<T, V> asValueProcessor() {
        return this.valueProcessor;
    }
}
