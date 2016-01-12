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

import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.ImmutableCommandData;
import org.spongepowered.api.data.manipulator.mutable.CommandData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.text.Text;
import org.spongepowered.common.data.manipulator.mutable.SpongeCommandData;
import org.spongepowered.common.data.processor.common.AbstractSpongeDataProcessor;
import org.spongepowered.common.data.value.SpongeValueFactory;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeValue;
import org.spongepowered.common.data.value.mutable.SpongeOptionalValue;
import org.spongepowered.common.data.value.mutable.SpongeValue;
import org.spongepowered.common.text.SpongeTexts;

import java.util.Optional;

public class EntityCommandDataProcessor extends AbstractSpongeDataProcessor<CommandData, ImmutableCommandData> {

    public EntityCommandDataProcessor() {
        registerValueProcessor(Keys.LAST_COMMAND_OUTPUT, EntityMinecartCommandBlock.class, new LastCommandProcessor());
        registerValueProcessor(Keys.SUCCESS_COUNT, EntityMinecartCommandBlock.class, new SuccessCountProcessor());
        registerValueProcessor(Keys.COMMAND, EntityMinecartCommandBlock.class, new CommandProcessor());
        registerValueProcessor(Keys.TRACKS_OUTPUT, EntityMinecartCommandBlock.class, new TracksOutputProcessor());
    }

    @Override
    public Optional<CommandData> fill(DataContainer container, CommandData commandData) {
        if (!container.contains(
                Keys.LAST_COMMAND_OUTPUT.getQuery(),
                Keys.SUCCESS_COUNT.getQuery(),
                Keys.COMMAND.getQuery(),
                Keys.TRACKS_OUTPUT.getQuery())) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        Optional<Text> lastCommandOutput = (Optional<Text>) container.get(Keys.LAST_COMMAND_OUTPUT.getQuery()).get();
        int successCount = container.getInt(Keys.SUCCESS_COUNT.getQuery()).get();
        String command = container.getString(Keys.COMMAND.getQuery()).get();
        boolean tracksOutput = container.getBoolean(Keys.TRACKS_OUTPUT.getQuery()).get();

        commandData.set(Keys.LAST_COMMAND_OUTPUT, lastCommandOutput);
        commandData.set(Keys.SUCCESS_COUNT, successCount);
        commandData.set(Keys.COMMAND, command);
        commandData.set(Keys.TRACKS_OUTPUT, tracksOutput);
        return Optional.of(commandData);
    }

    @Override
    public DataTransactionResult remove(DataHolder dataHolder) {
        return DataTransactionResult.failNoData();
    }

    @Override
    protected CommandData createManipulator() {
        return new SpongeCommandData();
    }

    private static class LastCommandProcessor extends KeyValueProcessor<EntityMinecartCommandBlock, Optional<Text>, OptionalValue<Text>> {

        @Override
        protected boolean hasData(EntityMinecartCommandBlock entity) {
            return true;
        }

        @Override
        protected OptionalValue<Text> constructValue(Optional<Text> actualValue) {
            return new SpongeOptionalValue<>(Keys.LAST_COMMAND_OUTPUT, actualValue);
        }

        @Override
        protected boolean set(EntityMinecartCommandBlock container, Optional<Text> value) {
            container.getCommandBlockLogic().setLastOutput(SpongeTexts.toComponent(value.orElse(Text.of())));
            container.onUpdate();
            return true;
        }

        @Override
        protected Optional<Optional<Text>> get(EntityMinecartCommandBlock container) {
            ITextComponent output = container.getCommandBlockLogic().getLastOutput();
            Optional<Text> text = output != null ? Optional.of(SpongeTexts.toText(output)) : Optional.empty();
            return Optional.of(text); // #OptionalWrapping o.o
        }

        @Override
        protected ImmutableValue<Optional<Text>> constructImmutableValue(Optional<Text> value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class SuccessCountProcessor extends KeyValueProcessor<EntityMinecartCommandBlock, Integer, MutableBoundedValue<Integer>> {

        @Override
        protected boolean hasData(EntityMinecartCommandBlock entity) {
            return true;
        }

        @Override
        protected MutableBoundedValue<Integer> constructValue(Integer actualValue) {
            return SpongeValueFactory.boundedBuilder(Keys.SUCCESS_COUNT)
                    .minimum(0)
                    .maximum(Integer.MAX_VALUE)
                    .defaultValue(0)
                    .actualValue(actualValue)
                    .build();
        }

        @Override
        protected boolean set(EntityMinecartCommandBlock container, Integer value) {
            container.getCommandBlockLogic().successCount = value;
            container.onUpdate();
            return true;
        }

        @Override
        protected Optional<Integer> get(EntityMinecartCommandBlock container) {
            return Optional.of(container.getCommandBlockLogic().successCount);
        }

        @Override
        protected ImmutableValue<Integer> constructImmutableValue(Integer value) {
            return constructValue(value).asImmutable();
        }
    }

    private static class CommandProcessor extends KeyValueProcessor<EntityMinecartCommandBlock, String, Value<String>> {

        @Override
        protected boolean hasData(EntityMinecartCommandBlock entity) {
            return true;
        }

        @Override
        protected Value<String> constructValue(String actualValue) {
            return new SpongeValue<>(Keys.COMMAND, actualValue);
        }

        @Override
        protected boolean set(EntityMinecartCommandBlock container, String value) {
            container.getCommandBlockLogic().commandStored = value;
            container.onUpdate();
            return true;
        }

        @Override
        protected Optional<String> get(EntityMinecartCommandBlock container) {
            return Optional.ofNullable(container.getCommandBlockLogic().commandStored);
        }

        @Override
        protected ImmutableValue<String> constructImmutableValue(String value) {
            return constructValue(value).asImmutable();
        }

    }

    private static class TracksOutputProcessor extends KeyValueProcessor<EntityMinecartCommandBlock, Boolean, Value<Boolean>> {

        @Override
        protected boolean hasData(EntityMinecartCommandBlock entity) {
            return true;
        }

        @Override
        protected Value<Boolean> constructValue(Boolean actualValue) {
            return new SpongeValue<>(Keys.TRACKS_OUTPUT, actualValue);
        }

        @Override
        protected boolean set(EntityMinecartCommandBlock container, Boolean value) {
            container.getCommandBlockLogic().setTrackOutput(value);
            container.onUpdate();
            return true;
        }

        @Override
        protected Optional<Boolean> get(EntityMinecartCommandBlock container) {
            return Optional.of(container.getCommandBlockLogic().shouldTrackOutput());
        }

        @Override
        protected ImmutableValue<Boolean> constructImmutableValue(Boolean value) {
            return ImmutableSpongeValue.cachedOf(Keys.TRACKS_OUTPUT, false, value);
        }

    }

}
