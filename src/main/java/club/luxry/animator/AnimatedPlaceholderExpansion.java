package club.luxry.animator;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimatedPlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "animator";
    }

    @Override
    public String getAuthor() {
        return "Luxry";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        Animator.AnimationData data = Animator.getAnimations().get(identifier);
        if (data == null) return null;
        long currentTime = System.currentTimeMillis();
        if (currentTime - data.lastUpdate >= data.speed) {
            switch (data.type) {
                case "cycle":
                    data.currentIndex = (data.currentIndex + 1) % data.texts.size();
                    break;
                case "random":
                    data.currentIndex = Animator.getRandom().nextInt(data.texts.size());
                    break;
                case "blink":
                    data.currentIndex = (data.currentIndex + 1) % 2;
                    break;
            }
            data.lastUpdate = currentTime;
        }

        String text = data.texts.get(data.currentIndex);
        return Animator.translateHexColorCodes(text);
    }

}