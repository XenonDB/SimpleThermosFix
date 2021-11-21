package net.passengerDB.simplethermosfix.asm;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.FMLLog;

public class ASMUtils {

	private static String PREFIX = "[STF_ASM] ";
	// private static final Logger log = Logger.getLogger("[STF_ASM] ");

	public static void log(String format, Object... data) {
		System.out.printf(PREFIX + format + "\n", data);
		// log.info(String.format(format, data));
	}

	public static void warn(String format, Object... data) {
		System.err.printf(PREFIX + format + "\n", data);
		// log.warning(String.format(format, data));
	}

}
