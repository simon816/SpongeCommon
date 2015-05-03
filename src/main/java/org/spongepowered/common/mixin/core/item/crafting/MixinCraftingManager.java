package org.spongepowered.common.mixin.core.item.crafting;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import org.spongepowered.api.item.recipe.Recipe;
import org.spongepowered.api.item.recipe.RecipeRegistry;
import org.spongepowered.api.item.recipe.ShapedRecipe;
import org.spongepowered.api.item.recipe.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.item.RecipeProxy;

import java.util.List;
import java.util.Set;


@Mixin(CraftingManager.class)
public class MixinCraftingManager implements RecipeRegistry {

    @Shadow
    private List<IRecipe> recipes;

    private final BiMap<IRecipe, Recipe> recipeMap = HashBiMap.create();

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onConstructed(CallbackInfo ci) {
        this.syncRecipeMap();
    }

    @Override
    public void register(Recipe recipe) {
        RecipeProxy.Minecraft mcRecipe = new RecipeProxy.Minecraft(recipe);
        this.recipeMap.inverse().put(recipe, mcRecipe);
        this.recipes.add(mcRecipe);
    }

    @Inject(method = "addRecipe(Lnet/minecraft/item/crafting/IRecipe;)V", at = @At("RETURN"))
    public void onAddRecipe(IRecipe recipe, CallbackInfo ci) {
        this.recipeMap.put(recipe, this.toSpongeRecipe(recipe));
    }

    @ModifyArg(method = "addRecipe(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)Lnet/minecraft/item/crafting/ShapedRecipes;", at = @At(
            value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    public Object onAddShaped(Object recipe) {
        this.recipeMap.put((ShapedRecipes) recipe, this.toSpongeRecipe((ShapedRecipes) recipe));
        return recipe;
    }

    @ModifyArg(method = "addShapelessRecipe(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)V", at = @At(value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    public Object onAddShapeless(Object recipe) {
        this.recipeMap.put((ShapelessRecipes) recipe, this.toSpongeRecipe((ShapelessRecipes) recipe));
        return recipe;
    }

    @Override
    public void remove(Recipe recipe) {
        this.recipes.remove(this.recipeMap.inverse().get(recipe));
        this.recipeMap.inverse().remove(recipe);
    }

    @Override
    public Set<Recipe> getRecipes() {
        this.syncRecipeMap();
        return ImmutableSet.copyOf(this.recipeMap.values());
    }

    private Recipe toSpongeRecipe(IRecipe recipe) {
        if (recipe instanceof ShapedRecipes) {
            return (ShapedRecipe) recipe;
        }
        else if (recipe instanceof ShapelessRecipes) {
            return (ShapelessRecipe) recipe;
        } else {
            return new RecipeProxy.Sponge(recipe);
        }
    }

    private void syncRecipeMap() {
        if (this.recipeMap.size() == this.recipes.size()) {
            return;
        }
        Set<Recipe> toDelete = Sets.newHashSet(this.recipeMap.values());
        for (IRecipe recipe : this.recipes) {
            if (this.recipeMap.get(recipe) == null) {
                this.recipeMap.put(recipe, this.toSpongeRecipe(recipe));
            }
            toDelete.remove(this.recipeMap.get(recipe));
        }
        for (Recipe recipe : toDelete) {
            this.recipeMap.inverse().remove(recipe);
        }
    }

}
