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
package org.spongepowered.common.registry.type.text;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.AdditionalRegistration;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.chat.ChatVisibilities;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.common.interfaces.IMixinEnumChatVisibility;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RegistrationDependency(ChatTypeRegistryModule.class)
public final class ChatVisibilityRegistryModule implements CatalogRegistryModule<ChatVisibility> {

    @RegisterCatalog(ChatVisibilities.class)
    public final Map<String, ChatVisibility> chatVisibilityMap = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        this.setChatTypes();

        this.chatVisibilityMap.put("full", (ChatVisibility) (Object) EntityPlayer.EnumChatVisibility.FULL);
        this.chatVisibilityMap.put("system", (ChatVisibility) (Object) EntityPlayer.EnumChatVisibility.SYSTEM);
        this.chatVisibilityMap.put("hidden", (ChatVisibility) (Object) EntityPlayer.EnumChatVisibility.HIDDEN);
    }

    private void setChatTypes() {
        // We can't do this in the EnumChatVisibility constructor, since the registry isn't initialized then
        EntityPlayer.EnumChatVisibility FULL = EntityPlayer.EnumChatVisibility.FULL;
        EntityPlayer.EnumChatVisibility SYSTEM = EntityPlayer.EnumChatVisibility.SYSTEM;
        EntityPlayer.EnumChatVisibility HIDDEN = EntityPlayer.EnumChatVisibility.HIDDEN;

        ((IMixinEnumChatVisibility) (Object) FULL).setChatTypes(ImmutableSet.copyOf(ChatTypeRegistryModule.chatTypeMappings.values()));
        ((IMixinEnumChatVisibility) (Object) SYSTEM).setChatTypes(ImmutableSet.of(ChatTypes.SYSTEM, ChatTypes.ACTION_BAR));
        ((IMixinEnumChatVisibility) (Object) HIDDEN).setChatTypes(ImmutableSet.of());
    }

    @Override
    public Optional<ChatVisibility> getById(String id) {
        return Optional.ofNullable(this.chatVisibilityMap.get(checkNotNull(id, "id").toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public Collection<ChatVisibility> getAll() {
        return ImmutableSet.copyOf((ChatVisibility[]) (Object[]) EntityPlayer.EnumChatVisibility.values());
    }

    @AdditionalRegistration
    public void customRegistration() {
        for (EntityPlayer.EnumChatVisibility visibility : EntityPlayer.EnumChatVisibility.values()) {
            if (!this.chatVisibilityMap.containsKey(visibility.name().toLowerCase(Locale.ENGLISH))) {
                this.chatVisibilityMap.put(visibility.name().toLowerCase(Locale.ENGLISH), (ChatVisibility) (Object) visibility);
            }
        }
    }

}
