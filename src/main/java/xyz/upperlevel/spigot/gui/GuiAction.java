package xyz.upperlevel.spigot.gui;

import xyz.upperlevel.spigot.gui.impl.link.Link;

public class GuiAction {

    public static Link close() {
        return GuiManager::close;
    }

    public static Link back() {
        return GuiManager::back;
    }

    public static Link change(Gui gui) {
        return (p) -> GuiManager.change(p, gui);
    }


    public static Link add(Link... links) {
        return (p) -> {
            for (Link link : links)
                link.run(p);
        };
    }
}
