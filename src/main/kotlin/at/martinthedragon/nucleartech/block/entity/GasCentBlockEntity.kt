package at.martinthedragon.nucleartech.block.entity

import at.martinthedragon.nucleartech.LangKeys
import at.martinthedragon.nucleartech.SoundEvents
import at.martinthedragon.nucleartech.capability.item.AccessLimitedInputItemHandler
import at.martinthedragon.nucleartech.energy.EnergyStorageExposed
import at.martinthedragon.nucleartech.energy.transferEnergy
import at.martinthedragon.nucleartech.fluid.NTechFluidTank
import at.martinthedragon.nucleartech.item.FluidIdentifierItem
import at.martinthedragon.nucleartech.item.insertAllItemsStacked
import at.martinthedragon.nucleartech.item.upgrades.MachineUpgradeItem
import at.martinthedragon.nucleartech.item.upgrades.OverdriveUpgrade
import at.martinthedragon.nucleartech.item.upgrades.PowerSavingUpgrade
import at.martinthedragon.nucleartech.item.upgrades.SpeedUpgrade
import at.martinthedragon.nucleartech.menu.CentrifugeMenu
import at.martinthedragon.nucleartech.menu.NTechContainerMenu
import at.martinthedragon.nucleartech.menu.slots.data.FluidStackDataSlot
import at.martinthedragon.nucleartech.menu.slots.data.IntDataSlot
import at.martinthedragon.nucleartech.recipe.CentrifugeRecipe
import at.martinthedragon.nucleartech.recipe.GasCentRecipe
import at.martinthedragon.nucleartech.recipe.RecipeTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraftforge.energy.CapabilityEnergy
import kotlin.jvm.optionals.getOrNull

class GasCentBlockEntity (pos: BlockPos, state: BlockState) : RecipeMachineBlockEntity<GasCentRecipe>(BlockEntityTypes.centrifugeBlockEntityType.get(), pos, state),
    SpeedUpgradeableMachine, PowerSavingUpgradeableMachine{

    override val mainInventory: NonNullList<ItemStack> = NonNullList.withSize(7, ItemStack.EMPTY)

    val inputTank = NTechFluidTank(80_000)
    val outputTank = NTechFluidTank(80_000)
    private val tanks = arrayOf(inputTank, outputTank)

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = when (slot) {
        0 -> stack.getCapability(CapabilityEnergy.ENERGY).isPresent
        1 -> MachineUpgradeItem.isValidForBE(this, stack)
        3 -> stack.item is FluidIdentifierItem
        else -> true
    }
    override fun inventoryChanged(slot: Int) {
        super.inventoryChanged(slot)
        checkChangedUpgradeSlot(slot)
    }
    override val soundLoopEvent get() = SoundEvents.centrifugeOperate.get()

    override val defaultName = LangKeys.CONTAINER_GAS_CENTRIFUGE.get()

    override fun createMenu(windowID: Int, inventory: Inventory) = GasCentrifugeMenu(windowID, inventory, this)

    override fun trackContainerMenu(menu: NTechContainerMenu<*>) {
        menu.track(IntDataSlot.create(this::energy, this::energy::set))
        menu.track(FluidStackDataSlot.create(inputTank, isClientSide()))
        menu.track(FluidStackDataSlot.create(outputTank, isClientSide()))
    }
    override fun getRenderBoundingBox() = AABB(blockPos, blockPos.offset(1, 4, 1))

    val energyStorage = EnergyStorageExposed(CentrifugeBlockEntity.MAX_ENERGY)

    var energy: Int
        get() = energyStorage.energyStored
        set(value) { energyStorage.energy = value }

    override fun serverTick(level: Level, pos: BlockPos, state: BlockState) {
        super.serverTick(level, pos, state)

        val energyItem = mainInventory[0]
        if (!energyItem.isEmpty) transferEnergy(energyItem, energyStorage)
    }
    companion object {
        const val MAX_ENERGY = 100_000
    }
    override fun checkCanProgress() = super.checkCanProgress() && energy >= consumption

    override val maxSpeedUpgradeLevel = 3
    override var speedUpgradeLevel = 0

    override val maxPowerSavingUpgradeLevel = 3
    override var powerSavingUpgradeLevel = 0


    override fun resetUpgrades() {
        super<SpeedUpgradeableMachine>.resetUpgrades()
        super<PowerSavingUpgradeableMachine>.resetUpgrades()
    }

    override fun getUpgradeInfoForEffect(effect: MachineUpgradeItem.UpgradeEffect<*>) = when (effect) {
        is SpeedUpgrade -> listOf(LangKeys.UPGRADE_INFO_DELAY.format("÷${effect.tier + 1}"), LangKeys.UPGRADE_INFO_CONSUMPTION.format("+${effect.tier * 200}HE/t"))
        is PowerSavingUpgrade -> listOf(LangKeys.UPGRADE_INFO_CONSUMPTION.format("÷${effect.tier + 1}"))
        else -> emptyList()
    }
    override fun getSupportedUpgrades() = listOf(SpeedUpgrade(maxSpeedUpgradeLevel), PowerSavingUpgrade(powerSavingUpgradeLevel))

    override val maxProgress = 200

    override val progressSpeed: Int get() = (1 + speedUpgradeLevel)
    private val consumption get() = (200 + speedUpgradeLevel * 200) / (1 + powerSavingUpgradeLevel)
    override fun tickProgress() {
        MachineUpgradeItem.applyUpgrades(this, mainInventory.subList(0, 1))
        super.tickProgress()
        energy -= consumption + 1
    }

    override fun findPossibleRecipe() = levelUnchecked.recipeManager.getRecipeFor(RecipeTypes.GAS_CENTRIFUGE, this, levelUnchecked).getOrNull()

    override fun matchesRecipe(recipe: GasCentRecipe) = recipe.matches(this, levelUnchecked) && 
        override fun getContinuousUpdateTag() = super.getContinuousUpdateTag().apply {
        put("Tank1", inputTank1.writeToNBT(CompoundTag()))
        put("Tank2", inputTank2.writeToNBT(CompoundTag()))
    }



}
