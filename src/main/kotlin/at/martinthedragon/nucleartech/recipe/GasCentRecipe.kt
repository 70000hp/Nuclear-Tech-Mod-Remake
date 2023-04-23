package at.martinthedragon.nucleartech.recipe

import at.martinthedragon.nucleartech.block.entity.GasCentBlockEntity
import net.minecraft.core.NonNullList
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraftforge.fluids.FluidStack

class GasCentRecipe (
    val recipeID: ResourceLocation,
    val resultsList: NonNullList<ItemStack>,
    val inputFluid: FluidStack,
    val outputFluid: FluidStack,

    ) : Recipe<GasCentBlockEntity> {
