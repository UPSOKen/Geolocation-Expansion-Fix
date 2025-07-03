package me.itsnathang.placeholders;

import me.clip.placeholderapi.expansion.Cleanable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class Geolocation extends PlaceholderExpansion implements Cleanable {
    private final String VERSION = getClass().getPackage().getImplementationVersion();
    private Map<UUID, LocationInfo> cache = new ConcurrentHashMap<>();
    private Set<UUID> pending = ConcurrentHashMap.newKeySet();

    @Override
    public String getAuthor() {
        return "NathanG";
    }

    @Override
    public String getIdentifier() {
        return "geolocation";
    }

    @Override
    public String getPlugin() {
        return null;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        UUID id = player.getUniqueId();

        if (cache.containsKey(id))
            return cache.get(id).getData(identifier);

        if (!pending.contains(id)) {
            pending.add(id);
            InetSocketAddress ip = player.getAddress();

            Bukkit.getScheduler().runTaskAsynchronously(
                    Bukkit.getPluginManager().getPlugin("PlaceholderAPI"),
                    () -> {
                        LocationInfo info = new LocationInfo(ip);

                        if (info.isValid())
                            cache.put(id, info);

                        pending.remove(id);
                    }
            );
        }

        return "Retrieving...";
    }

    @Override
    public void cleanup(Player p) {
        cache.remove(p.getUniqueId());
    }
}
