package fr.rudy.hud.placeholder;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.rudy.cities.manager.CityManager;
import fr.rudy.cities.manager.ClaimManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TerritoryPlaceholder extends PlaceholderExpansion {

    private final CityManager cityManager;
    private final ClaimManager claimManager;

    public TerritoryPlaceholder(CityManager cityManager, ClaimManager claimManager) {
        this.cityManager = cityManager;
        this.claimManager = claimManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "hud";
    }

    @Override
    public @NotNull String getAuthor() {
        return "rudy";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";

        if (identifier.equalsIgnoreCase("territory")) {
            return getTerritoryLabel(player);
        }

        return null;
    }

    private String getTerritoryLabel(Player player) {
        Location loc = player.getLocation();
        String worldName = loc.getWorld().getName().toLowerCase();

        switch (worldName) {
            case "world_newhorizon" -> {
                if (claimManager == null || cityManager == null) {
                    return "Nature";
                }

                Integer cityId = claimManager.getChunkOwnerId(loc);
                if (cityId == null) return "Nature";

                return cityManager.getCityNameById(cityId);
            }
            case "world_resource" -> {
                return "Ressource";
            }
            case "world_resource_nether" -> {
                return "Nether";
            }
            case "world_resource_the_end" -> {
                return "The End";
            }
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(loc.getWorld()));
        if (manager != null) {
            ApplicableRegionSet set = manager.getApplicableRegions(BukkitAdapter.asBlockVector(loc));
            for (ProtectedRegion region : set) {
                String id = region.getId().toLowerCase();
                return switch (id) {
                    case "musee" -> "Musée";
                    case "ile_de_newhorizon" -> "Île de NewHorizon";
                    default -> id;
                };
            }
        }

        return "Aucune région";
    }
}
