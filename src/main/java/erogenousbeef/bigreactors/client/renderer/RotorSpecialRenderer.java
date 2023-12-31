package erogenousbeef.bigreactors.client.renderer;

import erogenousbeef.bigreactors.client.ClientProxy;
import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.RotorBladeState;
import erogenousbeef.bigreactors.common.multiblock.RotorShaftState;
import erogenousbeef.bigreactors.common.multiblock.helpers.RotorInfo;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorFuelRod;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorBearing;
import erogenousbeef.bigreactors.init.BrBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

public class RotorSpecialRenderer extends TileEntitySpecialRenderer<TileEntityTurbineRotorBearing> {

	@Override
	public void render(final TileEntityTurbineRotorBearing bearing, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		final MultiblockTurbine turbine = bearing.getTurbine();
		final RotorInfo rotorInfo = bearing.getRotorInfo();

		if (null == rotorInfo || null == turbine || !turbine.isAssembledAndActive() || turbine.isInteriorInvisible())
			return;

		float angle = RotorSpecialRenderer.getRotorAngle(bearing, turbine);
		final int dX = rotorInfo.rotorDirection.getXOffset();
		final int dY = rotorInfo.rotorDirection.getYOffset();
		final int dZ = rotorInfo.rotorDirection.getZOffset();
		final float rotationOffsetX = 0 == dX ? 0.5f : 0.0f;
		final float rotationOffsetY = 0 == dY ? 0.5f : 0.0f;
		final float rotationOffsetZ = 0 == dZ ? 0.5f : 0.0f;

		Integer displayList = bearing.getDisplayList();

		if (displayList == null) {

			float brightness = bearing.getWorld().getLightBrightness(bearing.getPos().offset(rotorInfo.rotorDirection));

			bearing.setDisplayList(displayList = RotorSpecialRenderer.generateRotor(rotorInfo, brightness));
		}

		GlStateManager.pushMatrix();

		// translate to the tile entity position
		x += dX;
		y += dY;
		z += dZ;
		GlStateManager.translate(x, y, z);

		// rotate the rotor from it's center point
		GlStateManager.translate(rotationOffsetX, rotationOffsetY, rotationOffsetZ);
		GlStateManager.rotate(angle, dX, dY, dZ);
		GlStateManager.translate(-rotationOffsetX, -rotationOffsetY, -rotationOffsetZ);

		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.callList(displayList);

		GlStateManager.translate(-x, -y, -z);
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(TileEntityTurbineRotorBearing te) {
		return true;
	}

	private static float getRotorAngle(final TileEntityTurbineRotorBearing bearing, final MultiblockTurbine turbine) {

		final long elapsedTime = Minecraft.getSystemTime() - ClientProxy.lastRenderTime;
		final float speed = turbine.getRotorSpeed() / 10;
		float angle = bearing.getAngle();

		if (speed > 0.001f) {

			angle += speed * ((float)elapsedTime / 60000f) * 360f; // RPM * time in minutes * 360 degrees per rotation
			angle = angle % 360f;
			bearing.setAngle(angle);
		}

		return angle;
	}

	private static int generateRotor(RotorInfo info, float brightness) {

		final int list = GLAllocation.generateDisplayLists(1);

		GlStateManager.glNewList(list, GL11.GL_COMPILE);
		RotorSpecialRenderer.renderRotor(info, brightness);
		GlStateManager.glEndList();

		return list;
	}

	private static void renderRotor(RotorInfo info, float brightness) {

		final BlockRendererDispatcher renderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		final IBlockState defaultShaftState = BrBlocks.turbineRotorShaft.getDefaultState();
		final EnumFacing[] bladeDirections = RotorShaftState.getBladesDirections(info.rotorDirection.getAxis());
		final int dX = info.rotorDirection.getXOffset();
		final int dY = info.rotorDirection.getYOffset();
		final int dZ = info.rotorDirection.getZOffset();

		RotorBladeState[] currentBladeStates;
		int[] currentBladeLengths;

		for (int shaftIdx = 0; shaftIdx < info.rotorLength; ++shaftIdx) {

			final IBlockState shaftState = defaultShaftState.withProperty(Properties.ROTORSHAFTSTATE, info.shaftStates[shaftIdx]);

			GlStateManager.pushMatrix();
			GlStateManager.translate(dX * shaftIdx, dY * shaftIdx, dZ * shaftIdx);
			GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f); // cancel out the rotation done in render.renderBlockBrightness
			renderer.renderBlockBrightness(shaftState, brightness);
			GlStateManager.popMatrix();

			currentBladeLengths = info.bladeLengths[shaftIdx];
			currentBladeStates = info.bladeStates[shaftIdx];

			for (int bladeIdx = 0; bladeIdx < bladeDirections.length; ++bladeIdx) {

				final int bladeLength = currentBladeLengths[bladeIdx];
				final RotorBladeState bladeState = currentBladeStates[bladeIdx];

				if (bladeLength > 0 && null != bladeState)
					renderRotorBlade2b(bladeDirections[bladeIdx], bladeLength, bladeState, dX * shaftIdx, dY * shaftIdx,
										dZ * shaftIdx, renderer, brightness);
			}
		}
	}

	private static void renderRotorBlade2b(final EnumFacing bladeDir, final int bladeLength, final RotorBladeState bladeState,
										   final int shaftOffsetX, final int shaftOffsetY, final int shaftOffsetZ,
										   final BlockRendererDispatcher renderer, final float brightness) {

		final IBlockState blockState = BrBlocks.turbineRotorBlade.getDefaultState().withProperty(Properties.ROTORBLADESTATE, bladeState);
		final int dX = bladeDir.getXOffset();
		final int dY = bladeDir.getYOffset();
		final int dZ = bladeDir.getZOffset();

		for (int bladeIdx = 0; bladeIdx < bladeLength; ++bladeIdx) {

			final int offsetX = shaftOffsetX + dX * (bladeIdx + 1);
			final int offsetY = shaftOffsetY + dY * (bladeIdx + 1);
			final int offsetZ = shaftOffsetZ + dZ * (bladeIdx + 1);

			GlStateManager.pushMatrix();
			GlStateManager.translate(offsetX, offsetY, offsetZ);
			GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f); // cancel out the rotation done in render.renderBlockBrightness
			renderer.renderBlockBrightness(blockState, brightness);
			GlStateManager.popMatrix();
		}
	}
}
