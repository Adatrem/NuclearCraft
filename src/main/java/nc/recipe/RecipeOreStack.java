package nc.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import nc.util.OreDictHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeOreStack implements IItemIngredient {
	
	public String oreName;
	public final List<ItemStack> cachedStackList;
	public int stackSize;

	public RecipeOreStack(String oreName, int stackSize) {
		this.oreName = oreName;
		cachedStackList = OreDictHelper.getPrioritisedStackList(oreName);
		this.stackSize = stackSize;
	}

	@Override
	public ItemStack getStack() {
		if (cachedStackList == null || cachedStackList.isEmpty()) return null;
		ItemStack item = cachedStackList.get(0).copy();
		item.setCount(stackSize);
		return item;
	}
	
	@Override
	public String getIngredientName() {
		return "ore:" + oreName;
	}
	
	@Override
	public String getIngredientNamesConcat() {
		return getIngredientName();
	}

	@Override
	public boolean matches(Object object, SorptionType type) {
		if (object instanceof RecipeOreStack) {
			RecipeOreStack oreStack = (RecipeOreStack)object;
			if (oreStack.oreName.equals(oreName) && type.checkStackSize(stackSize, oreStack.stackSize)) {
				return true;
			}
		}
		else if (object instanceof String) {
			return oreName.equals(object);
		}
		else if (object instanceof ItemStack && type.checkStackSize(stackSize, ((ItemStack) object).getCount())) {
			int oreID = OreDictionary.getOreID(oreName);
			for (int ID : OreDictionary.getOreIDs((ItemStack)object)) {
				if (oreID == ID) return true;
			}
		}
		else if (object instanceof RecipeItemStack) {
			if (matches(((RecipeItemStack) object).stack, type)) return true;
		}
		else if (object instanceof RecipeItemStackArray) {
			for (IItemIngredient ingredient : ((RecipeItemStackArray) object).ingredientList) if (!matches(ingredient, type)) return false;
			return true;
		}
		return false;
	}

	@Override
	public int getMaxStackSize() {
		return stackSize;
	}
	
	@Override
	public void setMaxStackSize(int stackSize) {
		this.stackSize = stackSize;
		for (ItemStack stack : cachedStackList) stack.setCount(stackSize);
	}

	@Override
	public List<ItemStack> getInputStackList() {
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		for (ItemStack item : cachedStackList) {
			ItemStack itemStack = item.copy();
			itemStack.setCount(stackSize);
			stackList.add(itemStack);
		}
		return stackList;
	}
	
	@Override
	public List<ItemStack> getOutputStackList() {
		if (cachedStackList == null || cachedStackList.isEmpty()) return new ArrayList<ItemStack>();
		return Lists.newArrayList(getStack());
	}
}
