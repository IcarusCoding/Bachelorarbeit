package de.intelligence.bachelorarbeit.demoapplications.realapp.simplifx;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import com.jfoenix.skins.JFXTextFieldSkin;

import sun.misc.Unsafe;

import de.intelligence.bachelorarbeit.simplifx.SimpliFX;

public final class Core {

    public static void main(String[] args) throws Exception {
        addOpensReflectively(List.of("java.lang.reflect"), "java.base", JFXTextFieldSkin.class.getModule());
        SimpliFX.launchWithPreloader();
    }

    public static void addOpensReflectively(List<String> fullyQualifiedPackageNames, String module, Module currentModule) throws Exception {
        final Class<?> moduleImpl = Class.forName("java.lang.Module");
        final Field unsafeFieldLocal = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeFieldLocal.setAccessible(true);
        final Unsafe unsafe = (Unsafe) unsafeFieldLocal.get(null);
        if (unsafe == null) {
            return; // Error -- No Unsafe
        }
        final Class<?> moduleLayerImpl = Class.forName("java.lang.ModuleLayer");
        final ModuleLayer bootModuleLayer = (ModuleLayer) moduleLayerImpl.getDeclaredMethod("boot").invoke(null);
        final Optional<Module> moduleOpt = bootModuleLayer.findModule(module);
        if (moduleOpt.isEmpty()) {
            return; // Error -- Module not found
        }
        final Module fMod = moduleOpt.get();
        final Method addOpensMethodImpl = moduleImpl.getDeclaredMethod("implAddOpens", String.class, moduleImpl);
        long firstFieldOffset = unsafe.objectFieldOffset(OffsetProvider.class.getDeclaredField("firstField"));
        unsafe.putBooleanVolatile(addOpensMethodImpl, firstFieldOffset, true);
        for (final String pkgName : fullyQualifiedPackageNames) {
            addOpensMethodImpl.invoke(fMod, pkgName, currentModule);
        }
    }

    public static class OffsetProvider {
        int firstField;
    }

}
