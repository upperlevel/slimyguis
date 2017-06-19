package xyz.upperlevel.spigot.gui.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.spigot.gui.config.actions.BackGuiActionType;
import xyz.upperlevel.spigot.gui.config.actions.CloseGuiActionType;
import xyz.upperlevel.spigot.gui.config.actions.MessageActionType;

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
        types.put(type.getType(), type);
    }

    public static void removeActionType(ActionType<?> type) {
        types.remove(type.getType());
    }

    private static void registerDefaults() {
        addActionType(new BackGuiActionType());
        addActionType(new CloseGuiActionType());
        addActionType(new MessageActionType());
    }

    public static List<Action> deserialize(Collection<Map<String, Object>> config) {
        return config.stream().map(ActionType::deserialize).collect(Collectors.toList());
    }

    public static Action deserialize(Map<String, Object> config) {
        String type = (String) config.get("type");
        if(type == null)
            throw new IllegalArgumentException("Field \"type\" needed");
        ActionType t = types.get(type);
        if(t == null)
            throw new IllegalArgumentException("Cannot find action \"" + type + "\"");
        return t.load(config);
    }

    public static <T extends Action<T>> Map<String, Object> serialize(T action) {
        ActionType<T> type = action.getType();
        Map<String, Object> obj = type.save(action);
        obj.put("type", type.getType());
        return obj;
    }
}
