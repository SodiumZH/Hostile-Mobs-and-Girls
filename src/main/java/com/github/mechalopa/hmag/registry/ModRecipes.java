package com.github.mechalopa.hmag.registry;

import com.github.mechalopa.hmag.HMaG;
import com.github.mechalopa.hmag.world.item.crafting.EnchantmentUpgradeRecipe;
import com.github.mechalopa.hmag.world.item.crafting.ItemTagShapedRecipe;
import com.github.mechalopa.hmag.world.item.crafting.RemoveCurseRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes
{
	private static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, HMaG.MODID);

	public static final RegistryObject<RecipeSerializer<ShapedRecipe>> CRAFTING_ITEM_TAG_SHAPED = REGISTRY.register("crafting_item_tag_shaped", () -> new ItemTagShapedRecipe.Serializer());
	public static final RegistryObject<RecipeSerializer<RemoveCurseRecipe>> REMOVE_CURSE = REGISTRY.register("remove_curse", () -> new RemoveCurseRecipe.Serializer());
	public static final RegistryObject<RecipeSerializer<EnchantmentUpgradeRecipe>> ENCHANTMENT_UPGRADE = REGISTRY.register("enchantment_upgrade", () -> new EnchantmentUpgradeRecipe.Serializer());

	@SubscribeEvent
	public static void register(IEventBus eventBus)
	{
		REGISTRY.register(eventBus);
	}
}