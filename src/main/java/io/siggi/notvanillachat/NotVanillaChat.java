package io.siggi.notvanillachat;

import io.siggi.cubecore.util.text.processor.CubeBuildersClassicTextProcessor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class NotVanillaChat extends JavaPlugin implements Listener {

    private static NotVanillaChat instance;
    public static final List<String> emptyList = List.of();
    public static final Set<String> blockedCommands = Set.of(
            "minecraft:me",
            "minecraft:msg",
            "minecraft:tell",
            "minecraft:whisper",
            "minecraft:w",
            "minecraft:teammsg",
            "teammsg"
    );
    private Function<String, List<BaseComponent>> textProcessor = (string) -> List.of(new TextComponent(string));

    public void onLoad() {
        instance = this;
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        PluginCommand me = getCommand("me");
        MeCommand meCommand = new MeCommand(this);
        assert me != null;
        me.setExecutor(meCommand);
        me.setTabCompleter(meCommand);

        PluginCommand whisper = getCommand("whisper");
        WhisperCommand whisperCommand = new WhisperCommand(this);
        assert whisper != null;
        whisper.setExecutor(whisperCommand);
        whisper.setTabCompleter(whisperCommand);

        Plugin cubeCoreP = getServer().getPluginManager().getPlugin("CubeCore");
        if (cubeCoreP != null) {
            CubeBuildersClassicTextProcessor processor = new CubeBuildersClassicTextProcessor(null, null);
            setTextProcessor(
                    (text) -> new ArrayList<>(
                            processor.process(text, ChatColor.WHITE, ChatColor.WHITE)
                                    .toTextComponents(false)
                    )
            );
        }
    }

    public static String combineString(String[] args, int from) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < args.length; i++) {
            if (i != from) sb.append(" ");
            sb.append(args[i]);
        }
        return sb.toString();
    }

    public static void setTextProcessor(Function<String, List<BaseComponent>> processor) {
        if (processor == null) {
            throw new NullPointerException();
        }
        instance.textProcessor = processor;
    }

    public void broadcast(BaseComponent message, Predicate<Player> players) {
        for (Player player : getServer().getOnlinePlayers()) {
            if (players != null && !players.test(player)) continue;
            player.spigot().sendMessage(message);
        }
        getLogger().info(message.toPlainText());
    }

    public List<BaseComponent> processText(String text) {
        return textProcessor.apply(text);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String commandName = event.getMessage();
        int start = 0;
        int end = commandName.indexOf(" ");
        if (end == -1) end = commandName.length();
        if (commandName.startsWith("/")) {
            start = 1;
        }
        commandName = commandName.substring(start, end);
        if (blockedCommands.contains(commandName)) {
            Player player = event.getPlayer();
            player.sendMessage("The vanilla message commands are not available on this server.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        if (message.startsWith("/")) return;
        event.setCancelled(true);

        TextComponent top = new TextComponent("");
        top.addExtra(event.getPlayer().getDisplayName());
        TextComponent nameMessageSeparator = new TextComponent(" » ");
        nameMessageSeparator.setColor(ChatColor.GRAY);
        top.addExtra(nameMessageSeparator);
        processText(message).forEach(top::addExtra);

        broadcast(top, null);
    }
}
