package nc.container.processor;

import nc.container.SlotSpecificInput;
import nc.recipe.NCRecipes;
import nc.tile.processor.TileFluidProcessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

public class ContainerCentrifuge extends ContainerFluidProcessor {

	public ContainerCentrifuge(EntityPlayer player, TileFluidProcessor tileEntity) {
		super(tileEntity, NCRecipes.Type.CENTRIFUGE);
		
		addSlotToContainer(new SlotSpecificInput(tileEntity, 0, 132, 76, speedUpgrade));
		addSlotToContainer(new SlotFurnaceOutput(player, tileEntity, 1, 152, 76));
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player.inventory, j + 9*i + 9, 8 + 18*j, 96 + 18*i));
			}
		}
		
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + 18*i, 154));
		}
	}
}
