package org.spongepowered.common.item.inventory;

import net.minecraft.inventory.Container;
import net.minecraft.item.crafting.CraftingManager;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingOutput;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.Recipe;
import org.spongepowered.api.item.recipe.RecipeRegistry;

import java.util.Optional;
import java.util.Set;

public class CraftingInventoryImpl extends GridInventoryImpl implements CraftingInventory {

    private final GridInventory grid;
    private final CraftingOutput outputSlot;

    public CraftingInventoryImpl(Inventory parent, Container container, int outputIndex, int startIndex, int endIndex) {
        // TODO Crafting inventory is not a grid?
        super(parent, container, 2, startIndex, endIndex);
        this.grid = this.addSubInventory(new GridInventoryImpl(this, container, 2, startIndex, endIndex));
        this.outputSlot = this.addSubInventory(new CraftingOutputImpl(this, container, outputIndex));

    }

    @Override
    public GridInventory getCraftingGrid() {
        return this.grid;
    }

    @Override
    public CraftingOutput getResult() {
        return this.outputSlot;
    }

    @Override
    public Optional<Recipe> getRecipe() {
        Set<Recipe> recipes = ((RecipeRegistry) CraftingManager.getInstance()).getRecipes();
        for (Recipe recipe : recipes) {
            if (recipe.isValid(this.grid)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

}
