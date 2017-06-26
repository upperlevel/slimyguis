package xyz.upperlevel.spigot.gui.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.spigot.gui.config.actions.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class ActionType<T extends Action> {
    private static Map<String, ActionType> types = new HashMap<>();

    static {
        registerDefaults();
    }

    @Getter
    private final String type;

    public abstract T load(Map<String, Object> config);

    public abstract Map<String, Object> save(T action);


    public static void addActionType(ActionType<?> type) {
        types.put(type.getType().toLowerCase(), type);
    }

    public static void removeActionType(ActionType<?> type) {
        types.remove(type.getType());
    }

    private static void registerDefaults() {
        addActionType(new BackGuiActionType());
        addActionType(new BroadcastActionType());
        addActionType(new CloseGuiActionType());
        addActionType(new MessageActionType());
        addActionType(new OpenGuiActionType());
        addActionType(new RequireActionType());
    }

    public static List<Action> deserialize(Collection<Map<String, Object>> config) {
        return config.stream().map(ActionType::deserialize).collect(Collectors.toList());
    }

    public static Action deserialize(Object config) {
        if(config instanceof Map) {
            Map<String, Object> c = (Map<String, Object>) config;
            if (c.size() > 1)
                throw new InvalidGuiConfigurationException("cannot have more than one action for now");
            Map.Entry<String, Object> actiion = c.entrySet().iterator().next();
            String type = actiion.getKey();
            if (type == null)
                throw new IllegalArgumentException("Field \"type\" needed");
            ActionType t = types.get(type.toLowerCase());
            if (t == null)
                throw new IllegalArgumentException("Cannot find action \"" + type + "\"");
            return t.load((Map<String, Object>) actiion.getValue());
        } else if(config instanceof String) {
            String type = (String) config;
            ActionType t = types.get(type);
            if (t == null)
                throw new IllegalArgumentException("Cannot find action \"" + type + "\"");
            return t.load(null);//No argument
        } else
            throw new InvalidGuiConfigurationException("Invalid value type");
    }

    public static <T extends Action<T>> Map<String, Object> serialize(T action) {
        ActionType<T> type = action.getType();
        Map<String, Object> obj = type.save(action);
        obj.put("type", type.getType());
        return obj;
    }
}
