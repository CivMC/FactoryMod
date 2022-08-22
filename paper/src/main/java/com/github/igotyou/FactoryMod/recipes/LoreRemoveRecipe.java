package com.github.igotyou.FactoryMod.recipes;

import com.github.igotyou.FactoryMod.factories.FurnCraftChestFactory;
import com.github.igotyou.FactoryMod.utility.MultiInventoryWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vg.civcraft.mc.civmodcore.inventory.items.ItemMap;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;

public class LoreRemoveRecipe extends InputRecipe{

	private Component infoComponent = Component.text("This item has lore that will be removed!", NamedTextColor.GOLD).decoration(
			TextDecoration.ITALIC, false);
	private ItemMap itemToCleanse;
	private ItemStack exampleInput;
	private ItemStack exampleOutput;

	public LoreRemoveRecipe(String identifier, String name, int productionTime, ItemMap input, ItemMap itemToCleanse) {
		super(identifier, name, productionTime, input);
		this.itemToCleanse = itemToCleanse;
		this.exampleInput = itemToCleanse.getItemStackRepresentation().get(0);
		this.exampleOutput = itemToCleanse.getItemStackRepresentation().get(0);
		ItemUtils.addComponentLore(exampleInput, infoComponent);
	}

	@Override
	public boolean applyEffect(Inventory inputInv, Inventory outputInv, FurnCraftChestFactory fccf) {
		MultiInventoryWrapper combo = new MultiInventoryWrapper(inputInv, outputInv);
		logBeforeRecipeRun(combo, fccf);
		ItemStack toolio = itemToCleanse.getItemStackRepresentation().get(0);
		for (ItemStack is : inputInv.getContents()) {
			if (is != null && toolio.getType() == is.getType() && checkIfHasLore(is)) {
				if (input.removeSafelyFrom(inputInv)) {
					ItemMeta im = is.getItemMeta();
					if (im == null) {
						im = Bukkit.getItemFactory().getItemMeta(is.getType());
					}
					is.setItemMeta(Bukkit.getItemFactory().getItemMeta(is.getType()));
					logAfterRecipeRun(combo, fccf);
					return true;
				}
			}
		}
		logAfterRecipeRun(combo, fccf);
		return false;
	}

	@Override
	public String getTypeIdentifier() {
		return "REMOVELORE";
	}

	public ItemMap getItemToCleanse() {
		return itemToCleanse;
	}

	@Override
	public List<ItemStack> getInputRepresentation(Inventory i, FurnCraftChestFactory fccf) {
		if (i == null) {
			List<ItemStack> itemStackRepresentation = input.getItemStackRepresentation();
			List<ItemStack> inputs = new ArrayList<>(itemStackRepresentation.size() + 1);
			inputs.addAll(itemStackRepresentation);
			inputs.add(exampleInput.clone());
			return inputs;
		}
		List<ItemStack> returns = createLoredStacksForInfo(i);
		ItemStack toSt = itemToCleanse.getItemStackRepresentation().get(0);
		ItemUtils.addComponentLore(toSt, infoComponent);
		ItemUtils.addLore(toSt, ChatColor.GREEN + "Enough materials for " + new ItemMap(toSt).getMultiplesContainedIn(i)
				+ " runs");
		returns.add(toSt);
		return returns;
	}

	@Override
	public List<ItemStack> getOutputRepresentation(Inventory i, FurnCraftChestFactory fccf) {
		ItemStack is = exampleOutput.clone();
		if (i != null) {
			ItemUtils.addLore(
					is,
					ChatColor.GREEN
							+ "Enough materials for "
							+ String.valueOf(Math.min(itemToCleanse.getMultiplesContainedIn(i), input.getMultiplesContainedIn(i)))
							+ " runs");
		}
		List<ItemStack> stacks = new LinkedList<>();
		stacks.add(is);
		return stacks;
	}

	private boolean checkIfHasLore(ItemStack itemStack) {
		if (!itemStack.getItemMeta().hasLore()) {
			return false;
		}
		return !itemStack.getItemMeta().lore().isEmpty();
	}

	@Override
	public List<String> getTextualInputRepresentation(Inventory i, FurnCraftChestFactory fccf) {
		return Arrays.asList("1 " + ItemUtils.getItemName(exampleInput));
	}

	@Override
	public List<String> getTextualOutputRepresentation(Inventory i, FurnCraftChestFactory fccf) {
		return Arrays.asList("1 " + ItemUtils.getItemName(exampleOutput));
	}

	@Override
	public Material getRecipeRepresentationMaterial() {
		return exampleOutput.getType();
	}
}
