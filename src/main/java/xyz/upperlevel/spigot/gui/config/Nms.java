package xyz.upperlevel.spigot.gui.config;

import net.wesjd.anvilgui.version.impl.FallbackWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Nms {
    private static final String version;

    private static final Method asNmsCopy;
    private static final Method itemStackHasTag;
    private static final Method itemStackGetTag;
    private static final Method nbtTagCompoundHasKey;

    private static final Method nbtTagCompoundRemove;

    private static final Class<?> nbtTagCompound;
    private static final Method nbtTagCompoundSet;
    private static final Method itemStackSetTag;
    private static final Constructor nbtTagByteConstructor;
    private static final Method asBukkitCopy;

    private static final Field bukkitCommandMap;

    static {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            final Class<?> craftItemStack = getCraftClass("inventory.CraftItemStack");
            final Class<?> itemStack = getNMSClass("ItemStack");
            nbtTagCompound = getNMSClass("NBTTagCompound");

            asNmsCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
            itemStackHasTag = itemStack.getMethod("hasTag");
            itemStackGetTag = itemStack.getMethod("getTag");
            nbtTagCompoundHasKey = nbtTagCompound.getMethod("hasKey", String.class);

            nbtTagCompoundRemove = nbtTagCompound.getMethod("remove", String.class);

            nbtTagCompoundSet = nbtTagCompound.getMethod("set", String.class, getNMSClass("NBTBase"));
            itemStackSetTag = itemStack.getMethod("setTag", nbtTagCompound);
            nbtTagByteConstructor = getNMSClass("NBTTagByte").getConstructor(Byte.TYPE);
            asBukkitCopy = craftItemStack.getMethod("asBukkitCopy", itemStack);

            bukkitCommandMap  = Bukkit.getServer().getClass().getDeclaredField("commandMap");;
            bukkitCommandMap.setAccessible(true);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new UnsupportedVersionException(version, e);
        }
    }

    public static boolean hasTag(ItemStack item, String key) {
        /*
          itemStack = CraftItemStack.asNMSCopy(item);
          return itemStack.hasTag() && itemStack.getTag().hasKey(key);
         */
        try {
            Object itemStack = asNmsCopy.invoke(null, item);
            return ((Boolean)itemStackHasTag.invoke(itemStack)) && ((Boolean)nbtTagCompoundHasKey.invoke(itemStackGetTag.invoke(itemStack, key)));
        } catch (Exception e) {
           handleException(e);
        }
        return false;
    }

    public static boolean removeTag(ItemStack item, String key) {
        /*
          itemStack = CraftItemStack.asNMSCopy(item);
          if(itemStack.hasTag()) {
            NBTTagCompound tag = itemStack.getTag();
            if(tag.hasKey(key))) {
              tag.remove(key);
              return true;
            }
          }
          return false;
         */
        try {
            Object itemStack = asNmsCopy.invoke(null, item);
            if((Boolean)itemStackHasTag.invoke(itemStack)) {
                Object tag = itemStackGetTag.invoke(itemStack);
                if((Boolean)nbtTagCompoundHasKey.invoke(tag, key)) {
                    nbtTagCompoundRemove.invoke(tag, key);
                    return true;
                }
            }
        } catch (Exception e) {
            handleException(e);
        }
        return false;
    }

    public static ItemStack setTag(ItemStack item, String key) {
        /*
          itemStack = CraftItemStack.asNMSCopy(item);
          NBTTagCompound tag = itemStack.hasTag() ? itemStack.getTag() : new NBTTagCompound();
          tag.set(key, new NBTTagByte((byte)1);
          itemStack.setTag(tag);
          return CraftItemStack.asBukkitCopy(itemStack);
         */
        try {
            Object itemStack = asNmsCopy.invoke(null, item);
            Object tag;
            if((Boolean)itemStackHasTag.invoke(itemStack)) {
                tag = itemStackGetTag.invoke(itemStack);
            } else {
                tag = nbtTagCompound.newInstance();
            }
            nbtTagCompoundSet.invoke(tag, key, nbtTagByteConstructor.newInstance((byte)1));
            itemStackSetTag.invoke(itemStack, tag);
            return (ItemStack) asBukkitCopy.invoke(null, itemStack);
        } catch (Exception e) {
            handleException(e);
        }
        return item;
    }

    public static CommandMap getCommandMap(Server server) {
        try {
            return (CommandMap) bukkitCommandMap.get(server);
        } catch (IllegalAccessException e) {
            handleException(e);
        }
        return null;
    }



    protected static void handleException(Exception e) {
        throw new FallbackWrapper.UnsupportedVersionException(version, e);
    }


    private static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class<?> getCraftClass(String path) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + path);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class UnsupportedVersionException extends RuntimeException {
        private final String version;

        public UnsupportedVersionException(String version, Exception e) {
            super("Unsupported version \"" + version + "\", report this to the developers", e);
            this.version = version;
        }

        public String getVersion() {
            return version;
        }
    }


    private Nms(){}
}
