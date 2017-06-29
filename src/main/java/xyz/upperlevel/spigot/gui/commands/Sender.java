package xyz.upperlevel.spigot.gui.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public enum Sender {
    PLAYER {
        @Override
        public boolean isCorrect(CommandSender sender) {
            return sender instanceof Player;
        }

        @Override
        public String toString() {
            return "Player";
        }
    },
    CONSOLE {
        @Override
        public boolean isCorrect(CommandSender sender) {
            return sender instanceof ConsoleCommandSender;
        }

        @Override
        public String toString() {
            return "Console";
        }
    },
    ALL {
        @Override
        public boolean isCorrect(CommandSender sender) {
            return true;
        }

        @Override
        public String toString() {
            return "All";
        }
    };

    public abstract boolean isCorrect(CommandSender sender);
}
