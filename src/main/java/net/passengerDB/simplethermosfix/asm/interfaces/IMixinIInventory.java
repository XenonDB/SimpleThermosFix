package net.passengerDB.simplethermosfix.asm.interfaces;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import net.passengerDB.simplethermosfix.asm.ASMUtils;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventory;

public interface IMixinIInventory extends InventoryHolder {

	default public InventoryHolder getOwner() {
		return this;
	}

	@Override
	default public Inventory getInventory() {
		try {
			// CraftInventory的唯一一個建構式中的參數，其類型即是IInventory
			return (Inventory) CraftInventory.class.getConstructors()[0].newInstance(this);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			ASMUtils.warn("An exception occure in getInventory() method. This may be a bug. Please report this to mod author.");
			throw new RuntimeException(e);
		}
	}

}
