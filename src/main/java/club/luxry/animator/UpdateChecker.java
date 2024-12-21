package club.luxry.animator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class UpdateChecker
        implements Listener {
    private final JavaPlugin plugin;
    @Getter
    private final String currentVersion;
    @Getter private String latestVersion = null;
    @Getter private boolean isUpToDate = true;
    @Getter private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.resourceId = resourceId;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        this.latestVersion = this.fetchLatestVersion();
        this.isUpToDate = this.checkIfLatestVersion(this.currentVersion);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.latestVersion == null || this.isUpToDate) {
            return;
        }

        if (!event.getPlayer().hasPermission("animator.update")) {
            return;
        }
        event.getPlayer().sendMessage(this.getIsUpdatedMessage());
    }

    @Nullable
    private String fetchLatestVersion() {
        BufferedReader reader = null;
        try {
            // Construct the URL
            URI uri = new URI("https://api.polymart.org/v1/getResourceInfoSimple/?resource_id=" + this.resourceId + "&key=version");
            URL url = uri.toURL();

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10 seconds timeout
            connection.setReadTimeout(10000);

            // Check if the response code is 200 (OK)
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Read the response
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (Exception e) {
            // Handle exception gracefully
            return null;
        } finally {
            // Close the reader if it was opened
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }
    public String getCheckingMessage(){
        if (this.isUpToDate) {
            return "";
        }
        return Animator.translateHexColorCodes("&eChecking for updates...")+"\n";
    }
    public String getIsUpdatedMessage(){
        if (this.isUpToDate) {
            return "";
        }
        return  Animator.translateHexColorCodes("&aA new version of Animator is now available &7(&c%current_version% &8- &b%latest_version%&7)").replace("%current_version%", this.currentVersion).replace("%latest_version%", this.latestVersion)+"\n";
    }
    private boolean checkIfLatestVersion(String current) {
        return this.compareVersions(this.latestVersion, current) <= 0;
    }

    private int compareVersions(String version1, String version2) {
        if (version1 == null || version2 == null) {
            return 0;
        }
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");
        int v1Major = Integer.parseInt(v1Parts[0]);
        int v1Minor = Integer.parseInt(v1Parts[1]);
        int v1Patch = Integer.parseInt(v1Parts[2]);
        int v2Major = Integer.parseInt(v2Parts[0]);
        int v2Minor = Integer.parseInt(v2Parts[1]);
        int v2Patch = Integer.parseInt(v2Parts[2]);
        if (v1Major != v2Major) {
            return v1Major - v2Major;
        }
        if (v1Minor != v2Minor) {
            return v1Minor - v2Minor;
        }
        return v1Patch - v2Patch;
    }
}
