package io.siggi.notvanillachat;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class WhisperCommand implements CommandExecutor, TabExecutor {

    private final NotVanillaChat plugin;
    public WhisperCommand(NotVanillaChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String argv0, String[] args) {
        if (args.length < 2) {
            return false;
        }
        String targetName = args[0];
        String message = NotVanillaChat.combineString(args, 1);
        Player targetPlayer = plugin.getServer().getPlayer(targetName);
        if (targetPlayer == null) {
            TextComponent top = new TextComponent("");
            TextComponent notFound = new TextComponent("Player not found: ");
            notFound.setColor(ChatColor.RED);
            top.addExtra(notFound);
            TextComponent playerName = new TextComponent(targetName);
            playerName.setColor(ChatColor.RED);
            top.addExtra(playerName);
            sender.spigot().sendMessage(top);
            return true;
        }

        String senderName = (sender instanceof Player p) ? p.getDisplayName() : "CONSOLE";
        String recipientName = targetPlayer.getDisplayName();

        TextComponent toSender = new TextComponent("");
        TextComponent toRecipient = new TextComponent("");

        TextComponent pmTo = new TextComponent("PM To ");
        pmTo.setColor(ChatColor.YELLOW);
        toSender.addExtra(pmTo);
        TextComponent pmFrom = new TextComponent("PM From ");
        pmFrom.setColor(ChatColor.YELLOW);
        toRecipient.addExtra(pmFrom);

        TextComponent recipientNameComponent = new TextComponent(recipientName);
        toSender.addExtra(recipientNameComponent);
        TextComponent senderNameComponent = new TextComponent(senderName);
        toRecipient.addExtra(senderNameComponent);

        TextComponent separator = new TextComponent(" » ");
        separator.setColor(ChatColor.GRAY);
        toSender.addExtra(separator);
        toRecipient.addExtra(separator);

        plugin.processText(message).forEach((component) -> {
            toSender.addExtra(component);
            toRecipient.addExtra(component);
        });

        sender.spigot().sendMessage(toSender);
        if (sender instanceof Player p) {
            p.playSound(p, Sound.UI_BUTTON_CLICK, 1.0f, 2.0f);
        }
        targetPlayer.spigot().sendMessage(toRecipient);
        targetPlayer.playSound(targetPlayer, Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String argv0, String[] args) {
        if (args.length != 1) {
            return NotVanillaChat.emptyList;
        }
        List<String> results = new ArrayList<>();
        String partialName = args[0].toLowerCase();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(partialName)) {
                results.add(player.getName());
            }
        }
        return results;
    }
}
