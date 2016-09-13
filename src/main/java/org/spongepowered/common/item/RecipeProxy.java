package org.spongepowered.common.item;

import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.Recipe;

import java.util.List;
import java.util.Optional;

public class RecipeProxy {

    public static class Sponge implements Recipe {

        public final IRecipe recipe;

        public Sponge(IRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public List<ItemType> getResultTypes() {
            net.minecraft.item.ItemStack output = this.recipe.getRecipeOutput();
            if (output == null) {
                return ImmutableList.of();
            }
            return ImmutableList.of((ItemType) output.getItem());
        }

        @Override
        public boolean isValid(GridInventory grid) {
            // TODO GridInventory --> InventoryCrafting
            // return this.recipe.matches(grid, MinecraftServer.getServer().getEntityWorld());
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<List<ItemStack>> getResults(GridInventory grid) {
            if (!this.isValid(grid)) {
                return Optional.empty();
            }
            // TODO GridInventory --> InventoryCrafting
            // this.recipe.getCraftingResult(grid);
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "RecipeProxy.Sponge(recipe=" + this.recipe + ")";
        }

    }

    public static class Minecraft implements IRecipe {

        public final Recipe recipe;

        public Minecraft(Recipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            // TODO InventoryCrafting --> GridInventory
            // return this.recipe.isValid(inv);
            throw new UnsupportedOperationException();
        }

        @Override
        public net.minecraft.item.ItemStack getCraftingResult(InventoryCrafting inv) {
            throw new UnsupportedOperationException(); // TODO
        }

        @Override
        public int getRecipeSize() {
            return 0; // Sponge API has no comparable concept
        }

        @Override
        public net.minecraft.item.ItemStack getRecipeOutput() {
            List<ItemType> items = this.recipe.getResultTypes();
            if (items.isEmpty()) {
                return null;
            }
            return new net.minecraft.item.ItemStack((Item) items.get(0));
        }

        @Override
        public net.minecraft.item.ItemStack[] getRemainingItems(InventoryCrafting inv) {
            throw new UnsupportedOperationException(); // TODO
        }

        @Override
        public String toString() {
            return "RecipeProxy.Minecraft(recipe=" + this.recipe + ")";
        }
    }
}
