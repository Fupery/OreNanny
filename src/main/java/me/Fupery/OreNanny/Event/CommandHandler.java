package me.Fupery.OreNanny.Event;

import me.Fupery.OreNanny.OreNanny;
import me.Fupery.OreNanny.Utils.DataCompiler;
import me.Fupery.OreNanny.Utils.Lang;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

public class CommandHandler implements CommandExecutor {

    private HashMap<String, GenericCommand> commands;

    public CommandHandler() {
        commands = new HashMap<>();
        commands.put("top", new GenericCommand("orenanny.query", "/orenanny top") {
            @Override
            void onCommand(CommandSender sender, String[] args) {
                for (String string : new DataCompiler().getTopPlayers(OreNanny.getDataManager())) {
                    sender.sendMessage(string);
                }
            }
        });
        commands.put("info", new GenericCommand("orenanny.query", "/orenanny info <player>") {
            @Override
            void onCommand(CommandSender sender, String[] args) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                if (player != null && player.hasPlayedBefore()) {
                    List<String> strings = new DataCompiler().getPlayerData(OreNanny.getDataManager(), player);

                    if (strings != null) {
                        sender.sendMessage(strings.toArray(new String[strings.size()]));
                        return;
                    }
                }
                sender.sendMessage(String.format(Lang.PLAYER_NOT_FOUND.message(), args[1]));
            }
        });
        commands.put("clear", new GenericCommand("orenanny.query", "/orenanny clear <player>") {
            @Override
            void onCommand(CommandSender sender, String[] args) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                if (player != null && player.hasPlayedBefore()) {
                    List<String> strings = new DataCompiler().getPlayerData(OreNanny.getDataManager(), player);

                    if (strings != null) {
                        sender.sendMessage(strings.toArray(new String[strings.size()]));
                        return;
                    }
                }
                sender.sendMessage(String.format(Lang.PLAYER_NOT_FOUND.message(), args[1]));
            }
        });
        commands.put("help", new GenericCommand("orenanny.query", "orenanny [help]") {
            @Override
            void onCommand(CommandSender sender, String[] args) {
                sender.sendMessage(Lang.Array.HELP.messages());
            }
        });
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
        if (args == null || args.length == 0) {
            commands.get("help").handleCommand(sender, args);
            return true;
        }
        if (!commands.containsKey(args[0])) {
            sender.sendMessage(String.format(args[0], Lang.COMMAND_HELP.message()));
        } else {
            commands.get(args[0]).handleCommand(sender, args);
        }
        return true;
    }

    abstract class GenericCommand {
        private String permission;
        private String usage;
        private int minArgs;
        private int maxArgs;

        public GenericCommand(String permission, String usage) {
            this.permission = permission;
            if (usage == null) {
                throw new IllegalArgumentException("Usage must not be null");
            }
            String[] args = usage.replace("/orenanny ", "").split("\\s+");
            maxArgs = args.length;
            minArgs = maxArgs - StringUtils.countMatches(usage, "[");
            this.usage = usage;
        }

        abstract void onCommand(CommandSender sender, String[] args);

        void handleCommand(final CommandSender sender, final String[] args) {
            OreNanny.runAsync(new Runnable() {
                @Override
                public void run() {
                    if (!sender.hasPermission(permission)) {
                        sender.sendMessage(Lang.NO_PERMISSION.message());
                        return;
                    }
                    if (args.length < minArgs || args.length > maxArgs) {
                        sender.sendMessage(ChatColor.RED + usage);
                        return;
                    }
                    onCommand(sender, args);
                }
            });
        }
    }
}
