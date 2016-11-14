package org.spongepowered.common.mixin.core.item.inventory;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ContainerWorkbench.class)
public abstract class MixinContainerWorkbench extends Container {

    @Shadow public InventoryCrafting craftMatrix;

    @Shadow public IInventory craftResult;

    @Shadow private World worldObj;

    @Override
    @Overwrite
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        ItemStack item = CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj);
        this.craftResult.setInventorySlotContents(0, item);
        for (Object crafter : this.listeners) {
            if (crafter instanceof EntityPlayerMP) {
                ((EntityPlayerMP) crafter).connection.sendPacket(new SPacketSetSlot(this.windowId, 0, item));
            }
        }
    }
}
