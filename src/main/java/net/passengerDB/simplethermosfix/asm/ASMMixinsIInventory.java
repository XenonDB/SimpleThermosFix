package net.passengerDB.simplethermosfix.asm;

import org.bukkit.inventory.InventoryHolder;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.passengerDB.simplethermosfix.asm.interfaces.IMixinIInventory;

/**
 * �ѩ�Thermos�ݹ�IInventory�ʤF��}�A�����K�[�F�@�ǭ쥻���s�b�䤤����k���X�R���A�ҥH�ھ�Forge��Ū��IInventory�갵�����O�A�bThermos�ݰ���ɦ��i��|�X�{���~�ɭP�Y�A�C
 * ���~���o���I�|�O�b�u�ϥΧ��Y���靈��@IInventory���D�쪩Minecraft������Y���~�v�ɱY��C
 * �]�����Y���b�䤤�I�s�F���s�b��l���O����getOwner()��k(��^����InventoryHolder)�A�M�Ӱ��Forge�ج[�}�o���ҲլO���|�h��@�o�Ӥ�k���A�]���|�ɭPAbstractMethodError�C
 * �|�޵o�ӿ��~�����骺�Ҥl�h��:�]�k���ݪ��d���Bmekanism��Robit
 * 
 * ���M�b����ɴ��ӻ��A�I�s�@�Ӥ�k�ɡA�L�׸Ӥ�k�O�q��Ө�(�ۦ�w�q�A�Ӧۤ����O�Φ���@�w�]��k��interface)�A�u�n�u�s�b�P��ñ�p�H�ΦW�l����k�v�A���i�H���`����C
 * �Ҧp�A��@�ӨS����@IClassTransformer�����O�A�w�q�F�@�Ӧ���@���e��public byte[] transform(String
 * name, String transformedName, byte[] data)
 * �������ɴ���o�����O�j���૬��IClassTransformer�éI�s��transform��k�A�@�˥i�H���`����C
 * �M�ӡA����]�k���ݪ��d���A�|���@�ӭP�R�����D:
 * �d���~�ӤFEntityTameable�A�䤤���@��getOwner()��k(��^�����OEntity��EntityLivingBase�A�P�W�z��getOwner()���P)
 * �o�ɭP�d���L�k²�檺�s�WInventoryHolder
 * getOwner()�ӾA���ק�᪺���O�A�]���sĶ���ä����\�����ۦP��k�W�B�ۦP�ѼƦ���^�������P����k(���޳o�b����ɴ��O�i�檺�A�]������ɩI�s����kñ�p�]�t�F��^�����A�i�H�Q��{�O���P����k)
 * ���G�N�O�o�Ӱ��D���藍�i��b�sĶ�ɴ��ѨM�C
 * 
 * �]���o��z�Lasm�b����ɴ��~���ק�H¶�L�sĶ�����ˬd�C
 * �N�Q�ק�᪺IInventory����getOwner()�����A�O���~�Ӧ۩w�q�������A�H�F��s�W�H�w�]��k��@���e��getOwner()
 * 
 * �ɥR1: ���Y�����u����v���ާ@�ä��|�����A�]������������O�̤w�g��@�FgetOwner()�C
 * �|����o�ǹ��骺�ާ@�]���|�����A�]���|�榳��AbstractMethodError���B�̡C
 * 
 * �ɥR2:
 * �z�L�קאּ�w�]��k�ä��|�v�T��J�����S�����D�����e�C�]������L�ھڳQ�ק�᪺IInventory���}�o�̦Ө��A�L�̷|�ݨ�S����@���e��getOwner()��k�A�q�Ӧb���̪���@���̦ۦ��@getOwner()�A�o�˷|�л\���w�]��k�C
 * ����~�ӤFIInventory�åB�����FgetOwner�s�W�w�]��k�����p:
 * �ѩ󦹸Ѫk�V�J��getOwner��k�b�~�ӵ��c�W�O�u��[��H�B������v����k�A�]���o�ر��p�U�w�q��getOwner��k�@�w�O�u��[����v����k�A�ӷ|�b����ɴ��I�s�쥻����k�C
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

		// �����w�q�n����H��kgetOwner()
		boolean foundTarget = clsNode.methods.removeIf(m -> {
			return (m.access == (Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT)) && TARGET_METHOD_NAME.equals(m.name)
					&& TARGET_DESC.equals(m.desc);
		});

		if (!foundTarget)
			return data;

		// �O���~�Ӧ��w�q�w�]getOwner()�갵��interface�ӡu�NgetOwner()�s�W�w�]��@�v
		clsNode.interfaces.add(IMixinIInventory.class.getName().replace('.', '/'));
		ASMUtils.log("Successfully remove " + TARGET_METHOD_NAME + " method and add " + IMixinIInventory.class.getName()
				+ " as a super interface for " + TARGET_CLASS);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		clsNode.accept(writer);

		return writer.toByteArray();
	}

}
