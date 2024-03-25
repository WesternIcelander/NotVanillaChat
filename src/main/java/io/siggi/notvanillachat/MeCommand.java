package io.siggi.notvanillachat;

import java.util.List;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class MeCommand implements CommandExecutor, TabExecutor {

    private final NotVanillaChat plugin;
    public MeCommand(NotVanillaChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String argv0, String[] args) {
        if (args.length < 1) {
            return false;
        }
        String senderName = (sender instanceof Player p) ? p.getDisplayName() : "CONSOLE";
        String message = NotVanillaChat.combineString(args, 0);

        TextComponent top = new TextComponent("");
        TextComponent senderNameComponent = new TextComponent("*" + senderName + " ");
        top.addExtra(senderNameComponent);
        plugin.processText(message).forEach(top::addExtra);

        plugin.broadcast(top, null);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String argv0, String[] args) {
        return NotVanillaChat.emptyList;
    }
}
