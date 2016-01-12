package org.spongepowered.common.data.processor.common;

import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.data.ValueProcessor;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor.KeyValueProcessorBase;

import java.util.Optional;

final class TemporaryValueProcessor<H, E, V extends BaseValue<E>> implements ValueProcessor<E, V> {

    private final KeyValueProcessorBase<H, E, V> processor;
    private final Key<? extends BaseValue<E>> key;
    private final Class<H> holderClass;

    public TemporaryValueProcessor(Key<V> key, KeyValueProcessorBase<H, E, V> processor, Class<H> holderClass) {
        this.processor = processor;
        this.key = key;
        this.holderClass = holderClass;
    }

    @Override
    public Key<? extends BaseValue<E>> getKey() {
        return this.key;
    }

    @Override
    public int getPriority() {
        return this.processor.getPriority();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<E> getValueFromContainer(ValueContainer<?> container) {
        if (!supports(container)) {
            return Optional.empty();
        } else {
            return this.processor.get((H) container);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<V> getApiValueFromContainer(ValueContainer<?> container) {
        if (!supports(container)) {
            return Optional.empty();
        }
        return this.processor.constructValueForHolder((H) container);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(ValueContainer<?> container) {
        return this.holderClass.isInstance(container) && this.processor.supports((H) container);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataTransactionResult offerToStore(ValueContainer<?> container, E value) {
        final ImmutableValue<E> newValue = this.processor.constructImmutableValue(value);
        if (supports(container)) {
            final DataTransactionResult.Builder builder = DataTransactionResult.builder();
            this.processor.constructValueForHolder((H) container);
            final Optional<E> oldVal = this.processor.get((H) container);
            try {
                if (this.processor.set((H) container, value)) {
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

    @SuppressWarnings("unchecked")
    @Override
    public DataTransactionResult removeFrom(ValueContainer<?> container) {
        if (!supports(container)) {
            return DataTransactionResult.failNoData();
        }
        Optional<E> oldValue = this.processor.get((H) container);
        if (!oldValue.isPresent()) {
            return DataTransactionResult.successNoData();
        }
        try {
            if (this.processor.remove((H) container)) {
                return DataTransactionResult.successRemove(this.processor.constructImmutableValue(oldValue.get()));
            }
            return DataTransactionResult.failNoData();
        } catch (Exception e) {
            SpongeImpl.getLogger().error("An exception occurred when removing data", e);
            return DataTransactionResult.builder().result(DataTransactionResult.Type.ERROR).build();
        }
    }

}
