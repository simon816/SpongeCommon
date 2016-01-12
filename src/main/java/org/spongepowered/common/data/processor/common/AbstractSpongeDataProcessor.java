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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.data.DataProcessor;
import org.spongepowered.common.data.SpongeDataManager;
import org.spongepowered.common.data.ValueProcessor;
import org.spongepowered.common.data.util.DataUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractSpongeDataProcessor<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>>
        implements DataProcessor<M, I> {

    protected static abstract class KeyValueProcessor<H, E, V extends BaseValue<E>> {


        /**
         * Gets the current value of this key on the holder, if exists. It is
         * safe to assume the holder is compatible at this point.
         *
         * @param holder
         * @return
         */
        protected Optional<E> get(H holder) {
            return Optional.empty();
        }

        // Override to avoid overhead of get()
        /**
         * Returns whether the holder has a value. Override this if a call to
         * avoid any overhead of {@link #get}. It is safe to assume the holder
         * is compatible at this point.
         *
         * @param holder
         * @return
         */
        protected boolean hasData(H holder) {
            return get(holder).isPresent();
        }

        /**
        *
        * @return
        */
        protected int getPriority() {
            return 100;
        }

        /**
         * Sets the value on the holder. It is safe to assume the holder is
         * compatible at this point.
         *
         * @param holder
         * @param value
         * @return
         */
        protected boolean set(H holder, E value) {
            return false;
        }

        /**
         * Removes the data from the holder. It is safe to assume the holder is
         * compatible and has data at this point.
         *
         * @param holder
         * @return
         */
        protected boolean remove(H holder) {
            return false;
        }

        /**
         * Returns whether the data holder can support this key.
         *
         * @param holder
         * @return
         */
        protected boolean supports(H holder) {
            return true;
        }

        /**
         * Builds a {@link Value} of the type produced by this processor from an
         * input, actual value.
         *
         * @param value The actual value
         * @return The constructed {@link Value}
         */
        protected abstract V constructValue(E value);

        protected Optional<V> constructValueForHolder(H holder) {
            if (!supports(holder)) {
                return Optional.empty();
            }
            Optional<E> value = get(holder);
            if (value.isPresent()) {
                return Optional.of(constructValue(value.get()));
            }
            return Optional.empty();
        }

        /**
         *
         * @param value
         * @return
         */
        protected abstract ImmutableValue<E> constructImmutableValue(E value);
    }

    protected static abstract class KeyValueProcessor2<H, E, V extends Value<E>> extends KeyValueProcessor<H, E, V> {

        @Override
        protected ImmutableValue<E> constructImmutableValue(E value) {
            return constructValue(value).asImmutable();
        }
    }

    private final HashMap<Class<?>, KeyValueProcessor<Object, Object, ?>> classToProcessorMap = Maps.newHashMap();
    private final BiMap<KeyValueProcessor<Object, Object, ?>, Key<?>> processorToKeyMap = HashBiMap.create();

    @SuppressWarnings("unchecked")
    protected final <H, E, V extends BaseValue<E>> void registerValueProcessor(Key<V> key, Class<H> holderClass,
            KeyValueProcessor<H, E, V> processor) {
        if (this.classToProcessorMap.containsKey(holderClass) || this.processorToKeyMap.containsKey(processor)) {
            throw new IllegalArgumentException();
        }
        this.classToProcessorMap.put(holderClass, (KeyValueProcessor<Object, Object, ?>) processor);
        this.processorToKeyMap.put((KeyValueProcessor<Object, Object, ?>) processor, key);
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public boolean supports(EntityType entityType) {
        return false;
    }

    protected abstract M createManipulator();

    @Override
    public Optional<M> createFrom(DataHolder dataHolder) {
        if (!supports(dataHolder)) {
            return Optional.empty();
        } else {
            Optional<M> optional = from(dataHolder);
            if (!optional.isPresent()) {
                return Optional.of(createManipulator());
            } else {
                return optional;
            }
        }
    }

    @Override
    public Optional<M> fill(DataHolder dataHolder, M manipulator, MergeFunction overlap) {
        if (!supports(dataHolder)) {
            return Optional.empty();
        } else {
            final M merged = checkNotNull(overlap).merge(manipulator.copy(), from(dataHolder).orElse(null));
            // return Optional.of(manipulator.set(this.key,
            // merged.get(this.key).get()));
            merged.getValues().forEach(manipulator::set);
            return Optional.of(manipulator);
        }
    }

    private ArrayList<KeyValueProcessor<Object, Object, ?>> getProcessorsFor(DataHolder dataHolder) {
        ArrayList<KeyValueProcessor<Object, Object, ?>> l = Lists.newArrayList();
        for (Entry<Class<?>, KeyValueProcessor<Object, Object, ?>> entry : this.classToProcessorMap.entrySet()) {
            if (entry.getKey().isInstance(dataHolder) && entry.getValue().supports(dataHolder)) {
                l.add(entry.getValue());
            }
        }
        return l;
    }

    @Override
    public boolean supports(DataHolder dataHolder) {
        return !getProcessorsFor(dataHolder).isEmpty();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Optional<M> from(DataHolder dataHolder) {
        List<KeyValueProcessor<Object, Object, ?>> processors = getProcessorsFor(dataHolder)
                .stream().filter(p -> p.hasData(dataHolder)).collect(Collectors.toList());
        if (processors.isEmpty()) {
            return Optional.empty();
        }
        final M manipulator = createManipulator();
        for (KeyValueProcessor<Object, Object, ?> processor : processors) {
            Optional<?> optValue = processor.get(dataHolder);
            if (optValue.isPresent()) {
                manipulator.set((Key) this.processorToKeyMap.get(processor), optValue.get());
            }
        }
        return Optional.of(manipulator);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Optional<M> fill(DataContainer container, M m) {
        // TODO
        for (Key key : this.processorToKeyMap.values()) {
            m.set(key, DataUtil.getData(container, key));
        }
        return Optional.of(m);
    }

    @Override
    public DataTransactionResult set(DataHolder dataHolder, M manipulator, MergeFunction function) {
        ArrayList<KeyValueProcessor<Object, Object, ?>> processors = getProcessorsFor(dataHolder);
        if (processors.isEmpty()) {
            return DataTransactionResult.failResult(manipulator.getValues());
        }
        final DataTransactionResult.Builder builder = DataTransactionResult.builder().result(DataTransactionResult.Type.FAILURE);
        final Optional<M> old = from(dataHolder);
        final M merged = checkNotNull(function).merge(old.orElse(null), manipulator);
        try {
            for (ImmutableValue<?> value : merged.getValues()) {
                KeyValueProcessor<Object, Object, ?> processor = this.processorToKeyMap.inverse().get(value.getKey());
                if (processor != null && processor.set(dataHolder, value.get())) {
                    builder.result(DataTransactionResult.Type.SUCCESS).success(value);
                    if (old.isPresent()) {
                        builder.replace(old.get().getValues()); // TODO
                    }
                } else {
                    builder.reject(value);
                }
            }
        } catch (Exception e) {
            SpongeImpl.getLogger().debug("An exception occurred when setting data: ", e);
            builder.result(DataTransactionResult.Type.ERROR);
        }
        return builder.build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Optional<I> with(Key<? extends BaseValue<?>> key, Object value, I immutable) {
        if (immutable.supports(key)) {
            return Optional.of((I) immutable.asMutable().set((Key) key, value).asImmutable());
        }
        return Optional.empty();
    }

    @Override
    public DataTransactionResult remove(DataHolder dataHolder) {
        ArrayList<KeyValueProcessor<Object, Object, ?>> processors = getProcessorsFor(dataHolder);
        if (processors.isEmpty()) {
            return DataTransactionResult.failNoData();
        }
        DataTransactionResult.Builder builder = DataTransactionResult.builder().result(DataTransactionResult.Type.FAILURE);
        for (KeyValueProcessor<Object, Object, ?> processor : processors) {
            Optional<Object> oldValue = processor.get(dataHolder);
            if (!oldValue.isPresent()) {
                builder.result(DataTransactionResult.Type.SUCCESS);
                continue;
            }
            try {
                if (processor.remove(dataHolder)) {
                    builder.replace(processor.constructImmutableValue(oldValue.get())).result(DataTransactionResult.Type.SUCCESS);
                }
            } catch (Exception e) {
                SpongeImpl.getLogger().error("An exception occurred when removing data", e);
                return DataTransactionResult.builder().result(DataTransactionResult.Type.ERROR).build();
            }
        }
        return builder.build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void registerValueProcessors(SpongeDataManager manager) {
        for (Entry<KeyValueProcessor<Object, Object, ?>, Key<?>> entry : this.processorToKeyMap.entrySet()) {
            manager.registerValueProcessor((Key) entry.getValue(), new TemporaryValueProcessor(entry.getValue(), entry.getKey()));
        }
    }

    private static class TemporaryValueProcessor<E, V extends BaseValue<E>> implements ValueProcessor<E, V> {

        private final KeyValueProcessor<Object, E, V> processor;
        private final Key<? extends BaseValue<E>> key;

        public TemporaryValueProcessor(Key<? extends BaseValue<E>> key, KeyValueProcessor<Object, E, V> processor) {
            this.processor = processor;
            this.key = key;
        }

        @Override
        public Key<? extends BaseValue<E>> getKey() {
            return this.key;
        }

        @Override
        public int getPriority() {
            return this.processor.getPriority();
        }

        @Override
        public Optional<E> getValueFromContainer(ValueContainer<?> container) {
            if (!supports(container)) {
                return Optional.empty();
            } else {
                return this.processor.get(container);
            }
        }

        @Override
        public Optional<V> getApiValueFromContainer(ValueContainer<?> container) {
            return this.processor.constructValueForHolder(container);
        }

        @Override
        public boolean supports(ValueContainer<?> container) {
            return this.processor.supports(container);
        }

        @Override
        public DataTransactionResult offerToStore(ValueContainer<?> container, E value) {
            final ImmutableValue<E> newValue = this.processor.constructImmutableValue(value);
            if (supports(container)) {
                final DataTransactionResult.Builder builder = DataTransactionResult.builder();
                this.processor.constructValueForHolder(container);
                final Optional<E> oldVal = this.processor.get(container);
                try {
                    if (this.processor.set(container, value)) {
                        if (oldVal.isPresent()) {
                            builder.replace(this.processor.constructImmutableValue(oldVal.get()));
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
        public DataTransactionResult removeFrom(ValueContainer<?> container) {
            Optional<E> oldValue = this.processor.get(container);
            if (!oldValue.isPresent()) {
                return DataTransactionResult.successNoData();
            }
            try {
                if (this.processor.remove(container)) {
                    return DataTransactionResult.successRemove(this.processor.constructImmutableValue(oldValue.get()));
                }
                return DataTransactionResult.failNoData();
            } catch (Exception e) {
                SpongeImpl.getLogger().error("An exception occurred when removing data", e);
                return DataTransactionResult.builder().result(DataTransactionResult.Type.ERROR).build();
            }
        }

    }

}
