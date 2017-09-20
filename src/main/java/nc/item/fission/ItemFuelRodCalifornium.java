package nc.item.fission;

import java.util.List;

import nc.Global;
import nc.config.NCConfig;
import nc.handler.EnumHandler.CaliforniumFuelRodTypes;
import nc.util.NCInfo;
import nc.util.NCMath;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class ItemFuelRodCalifornium extends Item implements IFissionableItem {

	public ItemFuelRodCalifornium(String unlocalizedName, String registryName) {
		setUnlocalizedName(unlocalizedName);
		setRegistryName(new ResourceLocation(Global.MOD_ID, registryName));
		setHasSubtypes(true);
	}

	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
		for (int i = 0; i < CaliforniumFuelRodTypes.values().length; i++) {
			items.add(new ItemStack(item, 1, i));
		}
	}
	
	public String getUnlocalizedName(ItemStack stack) {
		for (int i = 0; i < CaliforniumFuelRodTypes.values().length; i++) {
			if (stack.getItemDamage() == i) {
				return getUnlocalizedName() + "." + CaliforniumFuelRodTypes.values()[i].getName();
			} else {
				continue;
			}
		}
		return this.getUnlocalizedName() + "." + CaliforniumFuelRodTypes.values()[0].getName();
	}
	
	public double getBaseTime(ItemStack stack) {
		return CaliforniumFuelRodTypes.values()[stack.getItemDamage()].getBaseTime();
	}

	public double getBasePower(ItemStack stack) {
		return CaliforniumFuelRodTypes.values()[stack.getItemDamage()].getBasePower();
	}

	public double getBaseHeat(ItemStack stack) {
		return CaliforniumFuelRodTypes.values()[stack.getItemDamage()].getBaseHeat();
	}
	
	public String getFuelName(ItemStack stack) {
		return CaliforniumFuelRodTypes.values()[stack.getItemDamage()].getName();
	}
	
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(itemStack, player, tooltip, advanced);
        if (INFO().length != 0) if (INFO()[itemStack.getMetadata()].length > 0) NCInfo.infoFull(tooltip, INFO()[itemStack.getMetadata()]);
    }
	
	public final static String[][] INFO() {
		String[][] info = new String[CaliforniumFuelRodTypes.values().length][];
		for (int i = 0; i < CaliforniumFuelRodTypes.values().length; i++) {
			info[i] = new String[] {I18n.translateToLocalFormatted("item.fuel_rod.base_time.des0") + " " + NCMath.Round(NCConfig.fission_californium_fuel_time[i]/1200D, 1) + " " + I18n.translateToLocalFormatted("item.fuel_rod.base_time.des1"), I18n.translateToLocalFormatted("item.fuel_rod.base_power.des0") + " " + NCConfig.fission_californium_power[i] + " RF/t", I18n.translateToLocalFormatted("item.fuel_rod.base_heat.des0") + " " + NCConfig.fission_californium_heat_generation[i] + " H/t"};
		}
		
		return info;
	}
}