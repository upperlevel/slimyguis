package xyz.upperlevel.spigot.gui.config.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.spigot.gui.link.Link;

@RequiredArgsConstructor
public abstract class Action<T extends Action<T>> implements Link {
    @Getter
    private final ActionType<T> type;
}
