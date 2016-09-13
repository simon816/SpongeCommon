package org.spongepowered.common.mixin.core.item.crafting;

import com.flowpowered.math.vector.Vector2i;
import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Mixin(ShapedRecipes.class)
public abstract class MixinShapedRecipe implements ShapedRecipe {

    @Shadow
    private net.minecraft.item.ItemStack recipeOutput;

    @Shadow
    public net.minecraft.item.ItemStack[] recipeItems;

    @Shadow
    public int recipeWidth;

    @Shadow
    public int recipeHeight;

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
            return Optional.empty();
        }
        // TODO GridInventory --> InventoryCrafting
        // this.getCraftingResult(grid);
        throw new UnsupportedOperationException();
    }

    @Override
    public int getWidth() {
        return this.recipeWidth;
    }

    @Override
    public int getHeight() {
        return this.recipeHeight;
    }

    @Override
    public Optional<ItemStack> getIngredient(int x, int y) {
        return Optional.ofNullable((ItemStack) this.recipeItems[x + x * y]);
    }

    @Override
    public Optional<ItemStack> getIngredient(Vector2i pos) {
        return this.getIngredient(pos.getX(), pos.getY());
    }

    @Override
    public String toString() {
        return "ShapedRecipe(" + this.recipeWidth + "x" + this.recipeHeight + ") {inputs=" + Arrays.toString(this.recipeItems) + ", output="
                + this.recipeOutput + "}";
    }
}
