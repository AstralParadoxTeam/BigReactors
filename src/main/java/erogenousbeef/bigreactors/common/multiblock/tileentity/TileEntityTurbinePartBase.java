package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.interfaces.IBeefDebuggableTile;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.block.BlockTieredPart;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public abstract class TileEntityTurbinePartBase extends RectangularMultiblockTileEntityBase implements IActivateable, IBeefDebuggableTile {

	@Override
	public MultiblockControllerBase createNewMultiblock() {
		return new MultiblockTurbine(worldObj);
	}
	
	@Override
	public Class<? extends MultiblockControllerBase> getMultiblockControllerType() {
		return MultiblockTurbine.class;
	}

	@Override
	public void onMachineAssembled(MultiblockControllerBase controller) {
		super.onMachineAssembled(controller);
		
		// Re-render this block on the client
		if(worldObj.isRemote) {
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
		}
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();
		
		// Re-render this block on the client
		if(worldObj.isRemote) {
			WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
		}
	}

	@Override
	public void onMachineActivated() {
	}

	@Override
	public void onMachineDeactivated() {
	}

	public MultiblockTurbine getTurbine() {
		return (MultiblockTurbine)getMultiblockController();
	}
	
	// IActivateable
	@Override
	public BlockPos getReferenceCoord() {
		if(isConnected()) {
			return getMultiblockController().getReferenceCoord();
		}
		else {
			return this.getPos();
		}
	}
	
	@Override
	public boolean getActive() {
		if(isConnected()) {
			return getTurbine().getActive();
		}
		else {
			return false;
		}
	}
	
	@Override
	public void setActive(boolean active) {
		if(isConnected()) {
			getTurbine().setActive(active);
		}
		else {
			BlockPos position = this.getPos();
			BRLog.error("Received a setActive command at %d, %d, %d, but not connected to a multiblock controller!", position.getX(), position.getY(), position.getZ());
		}
	}
	
	@Override
	public String getDebugInfo() {
		MultiblockTurbine t = getTurbine();
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().toString()).append("\n");
		if(t == null) {
			sb.append("Not attached to controller!");
			return sb.toString();
		}
		sb.append(t.getDebugInfo());
		return sb.toString();
	}

	public PartTier getPartTier() {

		IBlockState state = this.worldObj.getBlockState(this.getWorldPosition());
		Block block = state.getBlock();

		return block instanceof BlockTieredPart ? ((BlockTieredPart)block).getTierFromState(state) : null;
	}
}
