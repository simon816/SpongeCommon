package org.spongepowered.common.mixin.core.item.crafting;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.types.GridInventory;
import org.spongepowered.api.item.recipe.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;

@Mixin(ShapelessRecipes.class)
public abstract class MixinShapelessRecipe implements ShapelessRecipe {

    private net.minecraft.item.ItemStack recipeOutput;

    public List<net.minecraft.item.ItemStack> recipeItems;

    @Shadow
    public abstract boolean matches(InventoryCrafting inv, World worldIn);

    @Shadow
    public abstract net.minecraft.item.ItemStack getCraftingResult(InventoryCrafting inv);

    @Override
    public List<ItemType> getResultTypes() {
        return ImmutableList.of((ItemType) this.recipeOutput.getItem());
    }

    @Override
    public boolean isValid(GridInventory grid) {
        // TODO GridInventory --> InventoryCrafting
        // return this.matches(grid, MinecraftServer.getServer().getEntityWorld());
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<List<ItemStack>> getResults(GridInventory grid) {
        if (!this.isValid(grid)) {
            return Optional.absent();
        }
        // TODO GridInventory --> InventoryCrafting
        // this.getCraftingResult(grid);
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<ItemStack> getIngredients() {
        return ImmutableList.copyOf((List<ItemStack>) (Object) this.recipeItems);
    }

    @Override
    public String toString() {
        return "ShapelessRecipe {inputs=" + this.recipeItems + ", output=" + this.recipeOutput + "}";
    }

}
