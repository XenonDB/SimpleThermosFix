package net.passengerDB.simplethermosfix.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.*;
import java.util.Map;

@TransformerExclusions("net.passengerDB.simplethermosfix.asm")
@MCVersion("1.7.10")
@SortingIndex(1100)
public class ASMEntryPoint implements IFMLLoadingPlugin {

	public String[] getASMTransformerClass() {
		return new String[] { ASMMixinsIInventory.class.getName() };
	}

	public String getModContainerClass() {
		return null;
	}

	public String getSetupClass() {
		return null;
	}

	public void injectData(Map<String, Object> paramMap) {

	}

	public String getAccessTransformerClass() {
		return null;
	}

}
