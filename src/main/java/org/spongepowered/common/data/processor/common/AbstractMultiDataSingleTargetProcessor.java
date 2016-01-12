package org.spongepowered.common.data.processor.common;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;

public abstract class AbstractMultiDataSingleTargetProcessor<Holder, T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>>
        extends AbstractSpongeDataProcessor<T, I> {

    protected final Class<Holder> holderClass;

    public AbstractMultiDataSingleTargetProcessor(Class<Holder> holderClass) {
        this.holderClass = holderClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean supports(DataHolder dataHolder) {
        return this.holderClass.isInstance(dataHolder) && supports((Holder) dataHolder);
    }

    public boolean supports(Holder holder) {
        return true;
    }

}
