package net.passengerDB.simplethermosfix;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLLog;

import java.util.logging.Logger;

import cpw.mods.fml.common.FMLCommonHandler;

@Mod(modid = SimpleThermosFix.MODID, version = SimpleThermosFix.VERSION, acceptableRemoteVersions = "*")
public class SimpleThermosFix {
	public static final String MODID = "simplethermosfix";
	public static final String VERSION = "1.0";
	public static final Logger log = Logger.getLogger("[SimpleThermosFix] ");

	@EventHandler
	public void init(FMLInitializationEvent event) {
	}
}
