package erogenousbeef.bigreactors.common.multiblock.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorGlass;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartGlass;
import zero.mods.zerocore.api.multiblock.IMultiblockPart;
import zero.mods.zerocore.api.multiblock.MultiblockControllerBase;
import zero.mods.zerocore.api.multiblock.validation.ValidationError;

public class BlockMultiblockGlass extends BlockContainer { // TODO remove BlockContainer

	public static final int METADATA_REACTOR = 0;
	public static final int METADATA_TURBINE = 1;
	
	private static final String textureBaseName = "multiblockGlass";
	
	private static String[] subBlocks = new String[] { "reactor", "turbine" };
	// TODO blockstate
	/*
	private IIcon[][] icons = new IIcon[subBlocks.length][16];
	private IIcon transparentIcon;
	*/

	public BlockMultiblockGlass(Material material) {
		super(material);
		
		setStepSound(SoundType.GLASS);
		setHardness(2.0f);
		setRegistryName("brMultiblockGlass");
		setUnlocalizedName("brMultiblockGlass");
		// TODO blockstate
		//this.setBlockTextureName(BigReactors.TEXTURE_NAME_PREFIX + textureBaseName);
		setCreativeTab(BigReactors.TAB);
	}

	/**
	 * Called throughout the code as a replacement for block instanceof BlockContainer
	 * Moving this to the Block base class allows for mods that wish to extend vanilla
	 * blocks, and also want to have a tile entity on that block, may.
	 *
	 * Return true from this function to specify this block has a tile entity.
	 *
	 * @param state State of the current block
	 * @return True if block has a tile entity, false otherwise
	 */
	public boolean hasTileEntity(IBlockState state) {
		// TODO blockstate
		return true; // fix!
	}

	// TODO blockstate + use createTileEntity(World world, IBlockState state)
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		// TODO blockstate
		switch(metadata) {
		case METADATA_REACTOR:
			return new TileEntityReactorGlass();
		case METADATA_TURBINE:
			return new TileEntityTurbinePartGlass();
		default:
			throw new IllegalArgumentException("Unrecognized metadata");
		}
	}

	// TODO blockstate
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister)
	{
		this.transparentIcon = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + "tile." + textureBaseName + ".transparent");
		
		for(int metadata = 0; metadata < subBlocks.length; metadata++) {
			for(int i = 0; i < 16; ++i) {
				icons[metadata][i] = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + "tile." + textureBaseName + "." + subBlocks[metadata] + "." + Integer.toString(i));
			}
		}
	}

	@Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
		ForgeDirection[] dirsToCheck = StaticUtils.neighborsBySide[side];
		ForgeDirection dir;
		Block myBlock = blockAccess.getBlock(x, y, z);
		int myBlockMetadata = blockAccess.getBlockMetadata(x, y, z);
		
		// First check if we have a block in front of us of the same type - if so, just be completely transparent on this side
		ForgeDirection out = ForgeDirection.getOrientation(side);
		if(blockAccess.getBlock(x + out.offsetX, y + out.offsetY, z + out.offsetZ) == myBlock &&
				blockAccess.getBlockMetadata(x + out.offsetX, y + out.offsetY, z + out.offsetZ) == myBlockMetadata) {
			return transparentIcon;
		}
		
		// Calculate icon index based on whether the blocks around this block match it
		// Icons use a naming pattern so that the bits correspond to:
		// 1 = Connected on top, 2 = connected on bottom, 4 = connected on left, 8 = connected on right
		int iconIdx = 0;
		for(int i = 0; i < dirsToCheck.length; i++) {
			dir = dirsToCheck[i];
			// Same blockID and metadata on this side?
			if(blockAccess.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == myBlock &&
					blockAccess.getBlockMetadata(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) == myBlockMetadata) {
				// Connected!
				iconIdx |= 1 << i;
			}
		}
		
		return icons[myBlockMetadata][iconIdx];
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		return icons[metadata][0];
	}
	*/

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int damageDropped(IBlockState state) {
		// TODO fix metadata
		//return metadata;
		return super.damageDropped(state);
	}

	public ItemStack getItemStack(String name) {
		int metadata = -1;
		for(int i = 0; i < subBlocks.length; i++) {
			if(subBlocks[i].equals(name)) {
				metadata = i;
				break;
			}
		}
		
		if(metadata < 0) {
			throw new IllegalArgumentException("Unable to find a block with the name " + name);
		}
		return new ItemStack(this, 1, metadata);
	}

	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for(int i = 0; i < subBlocks.length; i++) {
			par3List.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		if(player.isSneaking()) {
			return false;
		}

		// If the player's hands are empty and they rightclick on a multiblock, they get a 
		// multiblock-debugging message if the machine is not assembled.
		if(!world.isRemote && heldItem == null) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof IMultiblockPart) {
				MultiblockControllerBase controller = ((IMultiblockPart)te).getMultiblockController();

				if(controller == null) {
					player.addChatMessage(new TextComponentString(String.format("SERIOUS ERROR - server part @ %d, %d, %d has no controller!", pos.getX(), pos.getY(), pos.getZ()))); //TODO Localize
				}
				else {
					ValidationError lastError = controller.getLastError();
					if(lastError != null) {
						player.addChatMessage(new TextComponentString(e.getMessage()));
						return true;
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
		return false;
	}
}
