package xyz.upperlevel.spigot.gui.config.placeholders;

public final class PlaceHolderUtil {


    public static PlaceholderValue<Long> parseLong(Object obj) {
        if(obj instanceof Number) {
            final long value = ((Number) obj).longValue();
            return p -> value;
        } else if(obj instanceof String)
            return PlaceholderValue.longValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Integer> parseInt(Object obj) {
        if(obj instanceof Number) {
            final int value = ((Number) obj).intValue();
            return p -> value;
        } else if(obj instanceof String)
            return PlaceholderValue.intValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Short> parseShort(Object obj) {
        if(obj instanceof Number) {
            final short value = ((Number) obj).shortValue();
            return p -> value;
        } else if(obj instanceof String)
            return PlaceholderValue.shortValue((String) obj);
        else return null;
    }


    public static PlaceholderValue<Byte> parseByte(Object obj) {
        if(obj instanceof Number) {
            final byte value = ((Number) obj).byteValue();
            return p -> value;
        } else if(obj instanceof String)
            return PlaceholderValue.byteValue((String) obj);
        else return null;
    }


    private PlaceHolderUtil(){}
}
