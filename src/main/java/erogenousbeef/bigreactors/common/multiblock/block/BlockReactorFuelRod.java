package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorFuelRod;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockReactorFuelRod extends /*BlockTieredPart*/BlockPart {

	public BlockReactorFuelRod(String blockName) {

		super(PartType.ReactorFuelRod, blockName, Material.IRON);
		this.setLightLevel(0.9f);
		this.setLightOpacity(1);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityReactorFuelRod();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {

		if (BigReactors.CONFIG.disableReactorParticles)
			return;

		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityReactorFuelRod) {

			TileEntityReactorFuelRod fuelRod = (TileEntityReactorFuelRod) te;
			MultiblockReactor reactor = fuelRod.getReactorController();

			if (!fuelRod.isOccluded() && reactor != null && !reactor.isInteriorInvisible() && reactor.getActive() && reactor.getFuelConsumedLastTick() > 0) {
				WorldHelper.spawnVanillaParticles(world, BigReactors.VALENTINES_DAY ? EnumParticleTypes.HEART : EnumParticleTypes.CRIT,
						1, random.nextInt(4) + 1, pos.getX(), pos.getY(), pos.getZ(), 1, 1, 1);
			}
		}
	}

	@Override
	protected void buildBlockState(BlockStateContainer.Builder builder) {

		super.buildBlockState(builder);
		builder.add(Properties.FUELRODSTATE);
	}

	@Override
	protected IBlockState buildDefaultState(IBlockState state) {

		return super.buildDefaultState(state).withProperty(Properties.FUELRODSTATE, FuelRodState.Disassembled);
	}

	@Override
	protected IBlockState buildActualState(IBlockState state, IBlockAccess world, BlockPos position,
										   MultiblockTileEntityBase part) {

		state = super.buildActualState(state, world, position, part);

		if (part instanceof TileEntityReactorFuelRod) {

			boolean assembled = part.isConnected() && part.getMultiblockController().isAssembled();
			TileEntityReactorFuelRod fuelRod = (TileEntityReactorFuelRod)part;
			FuelRodState rodState = FuelRodState.Disassembled;

			if (assembled) {

				switch (fuelRod.getReactorController().getFuelRodsLayout().getAxis()) {

					case X:
						rodState = FuelRodState.AssembledEW;
						break;

					case Y:
						rodState = FuelRodState.AssembledUD;
						break;

					case Z:
						rodState = FuelRodState.AssembledSN;
						break;
				}
			}

			state = state.withProperty(Properties.FUELRODSTATE, rodState);
		}

		return state;
	}
}