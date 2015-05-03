package org.spongepowered.common.mixin.core.item.inventory;

import net.minecraft.item.ItemStack;

import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ContainerWorkbench.class)
public abstract class MixinContainerWorkbench extends Container {

    @Shadow
    public InventoryCrafting craftMatrix;

    @Shadow
    public IInventory craftResult;

    @Shadow
    private World worldObj;

    @Override
    @Overwrite
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        ItemStack item = CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj);
        this.craftResult.setInventorySlotContents(0, item);
        if (!this.crafters.isEmpty()) {
            for (Object crafter : this.crafters) {
                if (crafter instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) crafter).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(windowId, 0, item));
                }
            }
        }
    }
}
