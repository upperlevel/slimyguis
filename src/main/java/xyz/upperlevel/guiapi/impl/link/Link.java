package xyz.upperlevel.guiapi.impl.link;

import org.bukkit.entity.Player;

public interface Link {
    Link EMPTY = (p) -> {};

    void run(Player player);

    default Link and(Link after) {
        return (p) -> {
            this.run(p);
            after.run(p);
        };
    }


    static Link consoleCommand(String command) {
        return new ConsoleCommandLink(command);
    }

    static Link command(String command) {
        return new CommandLink(command);
    }

    static Link and(Link... links) {
        return (p) -> {
            for(Link link : links)
                link.run(p);
        };
    }
}
