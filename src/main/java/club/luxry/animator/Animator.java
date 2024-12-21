package club.luxry.animator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Animator extends JavaPlugin {

    private static final Map<String, AnimationData> animations = new HashMap<>();
    private static final Random random = new Random();

    @Override
    public void onEnable() {
        UpdateChecker updateChecker = new UpdateChecker(this, 6596);
        String asciiArt =
                "                                         \n"+
                        "                                         \n"+
                        "                                         \n"+
                        "                  &5 |  \\                                        \n" +
                        "                  &5 | $$ __    __  __    __   ______   __    __ \n" +
                        "                  &5 | $$|  \\  |  \\|  \\  /  \\ /      \\ |  \\  |  \\\n" +
                        "                  &5 | $$| $$  | $$ \\$$\\/  $$|  $$$$$$\\| $$  | $$\n" +
                        "                  &5 | $$| $$  | $$  >$$  $$ | $$   \\$$| $$  | $$\n" +
                        "                  &5 | $$| $$__/ $$ /  $$$$\\ | $$      | $$__/ $$\n" +
                        "                  &5 | $$ \\$$    $$|  $$ \\$$\\| $$       \\$$    $$\n" +
                        "                  &5  \\$$  \\$$$$$$  \\$$   \\$$ \\$$       _\\$$$$$$$\n" +
                        "                  &5                                   |  \\__| $$\n" +
                        "                  &5                                    \\$$    $$\n" +
                        "                  &5                                     \\$$$$$$ \n" +
                        "                                         \n"+
                        "                      &bAnimator is running on Spigot - "+((JavaPlugin)this).getDescription().getVersion()+"\n"+
                        "                        &6Check more resources at luxry.club  \n"+
                        "                                         \n"+
                        "                            "+updateChecker.getCheckingMessage()+
                        "           "+updateChecker.getIsUpdatedMessage()+
                        "                                         ";

                        String[] lines = asciiArt.split("\n");
        for (String line : lines) {
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&5"+line));
        }
        saveDefaultConfig();
        loadAnimations();
        getCommand("animatorreload").setExecutor(this);
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getServer().getConsoleSender().sendMessage("      §cFailed to hook with PlaceholderAPI plugin, the server will shutdown");
            Bukkit.shutdown();
            return;
        }
        new AnimatedPlaceholderExpansion().register();
    }

    private void loadAnimations() {
        FileConfiguration config = getConfig();
        for (String key : config.getConfigurationSection("placeholders").getKeys(false)) {
            String path = "placeholders." + key;
            List<String> texts = config.getStringList(path + ".text");
            int speed = config.getInt(path + ".speed", 50);
            String type = config.getString(path + ".type", "cycle");
            animations.put(key, new AnimationData(texts, speed, type));
        }
    }
    private void reloadPlugin() {
        reloadConfig();
        animations.clear();
        loadAnimations();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender.hasPermission("animator.reload")) {
                    reloadPlugin();
                    sender.sendMessage(ChatColor.GREEN + "Animator configuration reloaded!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                }
                return true;
    }

    public static Map<String, AnimationData> getAnimations(){
        return animations;
    }
    public static Random getRandom(){
        return random;
    }

    public static class AnimationData {
        List<String> texts;
        int speed;
        String type;
        int currentIndex = 0;
        long lastUpdate = 0;

        AnimationData(List<String> texts, int speed, String type) {
            this.texts = texts;
            this.speed = speed;
            this.type = type;
        }
    }
    public static String translateHexColorCodes(String message) {
        final Pattern hexPattern = Pattern.compile("&#" + "([A-Fa-f0-9]{6})" + "");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x"
                    + ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR + group.charAt(1)
                    + ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR + group.charAt(3)
                    + ChatColor.COLOR_CHAR + group.charAt(4) + ChatColor.COLOR_CHAR + group.charAt(5)
            );
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }
}