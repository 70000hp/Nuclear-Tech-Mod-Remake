package at.martinthedragon.nucleartech.recipes

import at.martinthedragon.nucleartech.RegistriesAndLifecycle.RECIPE_SERIALIZERS
import at.martinthedragon.nucleartech.recipes.anvil.AnvilConstructingRecipe
import at.martinthedragon.nucleartech.recipes.anvil.AnvilRenameRecipe
import at.martinthedragon.nucleartech.recipes.anvil.AnvilSmithingRecipe
import net.minecraft.world.item.crafting.SimpleRecipeSerializer
import net.minecraftforge.registries.RegistryObject

object RecipeSerializers {
    val SMITHING: RegistryObject<AnvilSmithingRecipe.Serializer> = RECIPE_SERIALIZERS.register("anvil_smithing", AnvilSmithingRecipe::Serializer)
    val SMITHING_RENAMING: RegistryObject<SimpleRecipeSerializer<AnvilRenameRecipe>> = RECIPE_SERIALIZERS.register("anvil_smithing_special_renaming") { SimpleRecipeSerializer(::AnvilRenameRecipe) }
    val CONSTRUCTING: RegistryObject<AnvilConstructingRecipe.Serializer> = RECIPE_SERIALIZERS.register("anvil_constructing", AnvilConstructingRecipe::Serializer)
    val PRESSING: RegistryObject<PressingRecipe.Serializer> = RECIPE_SERIALIZERS.register("pressing", PressingRecipe::Serializer)
    val BLASTING: RegistryObject<BlastingRecipe.Serializer> = RECIPE_SERIALIZERS.register("blasting", BlastingRecipe::Serializer)
    val SHREDDING: RegistryObject<ShreddingRecipe.Serializer> = RECIPE_SERIALIZERS.register("shredding", ShreddingRecipe::Serializer)
    val ASSEMBLY: RegistryObject<AssemblyRecipe.Serializer> = RECIPE_SERIALIZERS.register("assembly", AssemblyRecipe::Serializer)
    val BATTERY: RegistryObject<BatteryRecipe.Serializer> = RECIPE_SERIALIZERS.register("battery", BatteryRecipe::Serializer)
}
