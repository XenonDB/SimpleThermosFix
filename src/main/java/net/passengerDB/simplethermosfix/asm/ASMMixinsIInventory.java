package net.passengerDB.simplethermosfix.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.passengerDB.simplethermosfix.asm.interfaces.IMixinIInventory;

/**
 * 由於Thermos端對IInventory動了手腳，直接添加了一些原本不存在其中的方法來擴充它，所以根據Forge解讀的IInventory實做的類別，在Thermos端執行時有可能會出現錯誤導致崩服。
 * 錯誤的發生點會是在「使用投擲器對有實作IInventory的非原版Minecraft實體投擲物品」時崩潰。
 * 因為投擲器在其中呼叫了不存在原始類別中的getOwner()方法(返回類型InventoryHolder)，然而基於Forge框架開發的模組是不會去實作這個方法的，因此會導致AbstractMethodError。
 * 會引發該錯誤的實體的例子則有:魔法金屬的寵物、mekanism的Robit
 * 
 * 雖然在執行時期來說，呼叫一個方法時，無論該方法是從何而來(自行定義，來自父類別或有實作預設方法的interface)，只要「存在同樣簽署以及名子的方法」，都可以正常執行。
 * 例如，把一個沒有實作IClassTransformer的類別，定義了一個有實作內容的public byte[] transform(String name, String transformedName, byte[] data)
 * 那麼執行時期把這個類別強制轉型成IClassTransformer並呼叫其transform方法，一樣可以正常執行。
 * 然而，對於魔法金屬的寵物，會有一個致命的問題:
 * 寵物繼承了EntityTameable，其中有一個getOwner()方法(返回類型是Entity或EntityLivingBase，與上述的getOwner()不同)
 * 這導致寵物無法簡單的新增InventoryHolder getOwner()來適應修改後的類別，因為編譯器並不允許重載相同方法名、相同參數但返回類型不同的方法(儘管這在執行時期是可行的，因為執行時呼叫的方法簽署包含了返回類型，可以被辨認是不同的方法)
 * 結果就是這個問題絕對不可能在編譯時期解決。(其實可以，但必須要分兩次編譯，並分別將兩次編譯好的不同版本類別「混合」在一起，這麼做非常麻煩。)
 * 
 * 因此這邊透過asm在執行時期才做修改以繞過編譯器的檢查。
 * 將被修改後的IInventory中的getOwner()移除，令其繼承自定義的介面，以達到新增以預設方法實作內容的getOwner()
 * 
 * 補充1: 投擲器對於「方塊」的操作並不會報錯，因為方塊的基底類別裡已經實作了getOwner()。
 * 漏斗對於這些實體的操作也不會報錯，因為漏斗有對AbstractMethodError做處裡。
 * 
 * 補充2:
 * 透過修改為預設方法並不會影響到既有的沒有問題的內容。因為對於其他根據被修改後的IInventory的開發者而言，他們會看到沒有實作內容的getOwner()方法，從而在它們的實作類裡自行實作getOwner()，這樣會覆蓋掉預設方法。
 * 對於繼承了IInventory並且給予了getOwner新增預設方法的狀況:
 * 由於此解法混入的getOwner方法在繼承結構上是「更加抽象、不具體」的方法，因此這種情況下定義的getOwner方法一定是「更加具體」的方法，而會在執行時期呼叫原本的方法。
 * 
 */
public class ASMMixinsIInventory implements IClassTransformer {

	private static final String TARGET_CLASS = "net.minecraft.inventory.IInventory";
	private static final String TARGET_DESC = "()Lorg/bukkit/inventory/InventoryHolder;";
	private static final String TARGET_METHOD_NAME = "getOwner";

	public static boolean isTargetEnvironment() {

		try {
			// Class.forName("thermos.ThermosClassTransformer");
			Class.forName("org.bukkit.inventory.InventoryHolder");
			Class.forName("org.bukkit.inventory.Inventory");
			Class.forName("org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventory");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] data) {

		if (!transformedName.equals(TARGET_CLASS) || !isTargetEnvironment())
			return data;

		ClassNode clsNode = new ClassNode();
		ClassReader clsReader = new ClassReader(data);
		clsReader.accept(clsNode, 0);

		// 移除定義好的抽象方法getOwner()避免「getOwner()被重新定義成抽象方法」
		boolean foundTarget = clsNode.methods.removeIf(m -> {
			return (m.access == (Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT)) && TARGET_METHOD_NAME.equals(m.name) && TARGET_DESC.equals(m.desc);
		});

		if (!foundTarget)
			return data;

		// 令其繼承有定義預設getOwner()實作的interface來「將getOwner()新增預設實作」
		clsNode.interfaces.add(IMixinIInventory.class.getName().replace('.', '/'));
		ASMUtils.log("Successfully remove " + TARGET_METHOD_NAME + " method and add " + IMixinIInventory.class.getName() + " as a super interface for " + TARGET_CLASS);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		clsNode.accept(writer);

		return writer.toByteArray();
	}

}
