package erogenousbeef.bigreactors.common.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockBROre extends ModBlock {

	public BlockBROre(@Nonnull final String blockName, @Nonnull final String oreDictionaryName) {

		super(blockName, Material.ROCK, oreDictionaryName);
        this.setCreativeTab(BigReactors.TAB);
        this.setHardness(2.0f);
	}

	@SuppressWarnings("ConstantConditions")
	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {

		final Block ore = state.getBlock();

		if (BrBlocks.oreAnglesite == ore) {
			return BrItems.mineralAnglesite;
		} else if (BrBlocks.oreBenitoite == ore) {
			return BrItems.mineralBenitoite;
		}

		return super.getItemDropped(state, rand, fortune);
	}

	/**
	 * Called when a user uses the creative pick block button on this block
	 *
	 * @param target The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
	 */
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return this.createItemStack();
	}
}
