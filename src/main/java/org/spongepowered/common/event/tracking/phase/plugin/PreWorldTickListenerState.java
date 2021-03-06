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
package org.spongepowered.common.event.tracking.phase.plugin;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.entity.PlayerTracker;
import org.spongepowered.common.event.InternalNamedCauses;
import org.spongepowered.common.event.tracking.CauseTracker;
import org.spongepowered.common.event.tracking.PhaseContext;
import org.spongepowered.common.event.tracking.TrackingUtil;
import org.spongepowered.common.interfaces.IMixinChunk;
import org.spongepowered.common.interfaces.event.forge.IMixinWorldTickEvent;

import javax.annotation.Nullable;

final class PreWorldTickListenerState extends ListenerPhaseState {

    PreWorldTickListenerState() {
    }

    @Override
    public void associateAdditionalBlockChangeCauses(PhaseContext context, Cause.Builder builder, CauseTracker causeTracker) {
        context.getCapturedPlayer().ifPresent(player -> builder.named(NamedCause.notifier(player)));
    }

    @Override
    public void processPostTick(CauseTracker causeTracker, PhaseContext phaseContext) {
        final IMixinWorldTickEvent worldTickEvent = phaseContext
                .firstNamed(InternalNamedCauses.Tracker.TICK_EVENT, IMixinWorldTickEvent.class)
                .orElseThrow(TrackingUtil.throwWithContext("Expected to be capturing a WorldTickEvent but we're not!!!", phaseContext));
        final Object listener = phaseContext.getSource(Object.class)
                .orElseThrow(TrackingUtil.throwWithContext("Expected to be capturing a WorldTickEvent listener!", phaseContext));

        phaseContext.getCapturedBlockSupplier().ifPresentAndNotEmpty(blocks -> {
            if (causeTracker.getMinecraftWorld() != worldTickEvent.getWorld()
                && SpongeImpl.getGlobalConfig().getConfig().getCauseTracker().reportWorldTickDifferences()) {
                logWarningOfDifferentWorldchanges(causeTracker, worldTickEvent, listener);
            }
            TrackingUtil.processBlockCaptures(blocks, causeTracker, this, phaseContext);
        });
    }

    @Override
    public void associateNeighborBlockNotifier(PhaseContext context, @Nullable BlockPos sourcePos, Block block, BlockPos notifyPos,
            WorldServer minecraftWorld, PlayerTracker.Type notifier) {
        context.getCapturedPlayer().ifPresent(player ->
                ((IMixinChunk) minecraftWorld.getChunkFromBlockCoords(notifyPos))
                        .setBlockNotifier(notifyPos, player.getUniqueId())
        );
    }

    @Override
    public void capturePlayerUsingStackToBreakBlocks(PhaseContext context, EntityPlayerMP playerMP, @Nullable ItemStack stack) {
        context.getCapturedPlayerSupplier().addPlayer(playerMP);
    }

}
