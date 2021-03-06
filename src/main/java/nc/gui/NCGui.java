package nc.gui;

import java.util.List;

import com.google.common.collect.Lists;

import nc.tile.energy.ITileEnergy;
import nc.tile.internal.fluid.Tank;
import nc.util.Lang;
import nc.util.UnitHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

public abstract class NCGui extends GuiContainer {

	public NCGui(Container inventory) {
		super(inventory);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
		renderTooltips(mouseX, mouseY);
	}
	
	public void renderTooltips(int mouseX, int mouseY) {}
	
	public void drawTooltip(List<String> text, int mouseX, int mouseY, int x, int y, int width, int height) {
		int xPos = x + guiLeft; int yPos = y + guiTop;
		if (mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height) {
			drawHoveringText(text, mouseX, mouseY);
		}
	}
	
	public void drawTooltip(String text, int mouseX, int mouseY, int x, int y, int width, int height) {
		List<String> stringList = Lists.newArrayList(text);
		drawTooltip(stringList, mouseX, mouseY, x, y, width, height);
	}
	
	public List<String> fluidInfo(FluidStack fluid, Tank tank) {
		String fluidName = fluid.getLocalizedName();
		String fluidAmount = UnitHelper.prefix(fluid.amount, tank.getCapacity(), 5, "B", -1);
		return Lists.newArrayList(TextFormatting.GREEN + fluidName + TextFormatting.WHITE + " [" + fluidAmount + "]", TextFormatting.ITALIC + Lang.localise("gui.container.shift_clear_tank"));
	}
	
	public void drawFluidTooltip(FluidStack fluid, Tank tank, int mouseX, int mouseY, int x, int y, int width, int height) {
		if (fluid == null) return;
		if (fluid.amount <= 0) return;
		drawTooltip(fluidInfo(fluid, tank), mouseX, mouseY, x, y, width, height + 1);
	}
	
	public List<String> energyInfo(ITileEnergy tile) {
		String energy = UnitHelper.prefix(tile.getEnergyStorage().getEnergyStored(), tile.getEnergyStorage().getMaxEnergyStored(), 5, "RF");
		return Lists.newArrayList(TextFormatting.LIGHT_PURPLE + Lang.localise("gui.container.energy_stored") + TextFormatting.WHITE + " " + energy);
	}
	
	public List<String> noEnergyInfo() {
		return Lists.newArrayList(TextFormatting.RED + Lang.localise("gui.container.no_energy"));
	}
	
	public void drawEnergyTooltip(ITileEnergy tile, int mouseX, int mouseY, int x, int y, int width, int height) {
		drawTooltip(energyInfo(tile), mouseX, mouseY, x, y, width, height);
	}
	
	public void drawNoEnergyTooltip(int mouseX, int mouseY, int x, int y, int width, int height) {
		drawTooltip(noEnergyInfo(), mouseX, mouseY, x, y, width, height);
	}
	
	protected int width(String string) {
		return fontRenderer.getStringWidth(string);
	}
	
	protected int widthHalf(String string) {
		return width(string)/2;
	}
}
