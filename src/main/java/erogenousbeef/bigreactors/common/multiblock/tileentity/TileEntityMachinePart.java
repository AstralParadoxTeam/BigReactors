package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.block.BlockTieredPart;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public abstract class TileEntityMachinePart extends RectangularMultiblockTileEntityBase implements IActivateable {
    /*
    @Override
    public void onPostMachineAssembled(MultiblockControllerBase controller) {

        super.onPostMachineAssembled(controller);

        // Re-render this block on the client
        if(worldObj.isRemote) {
            WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
        }
    }

    @Override
    public void onPostMachineBroken() {

        super.onPostMachineBroken();

        // Re-render this block on the client
        if(worldObj.isRemote) {
            WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
        }
    }*/

    @Override
    public void onMachineActivated() {
    }

    @Override
    public void onMachineDeactivated() {
    }

    public PartTier getPartTier() {

        IBlockState state = this.worldObj.getBlockState(this.getWorldPosition());
        Block block = state.getBlock();

        return block instanceof BlockTieredPart ? ((BlockTieredPart)block).getTierFromState(state) : null;
    }

}
