package fr.rudy.hud;

import fr.rudy.cities.api.CitiesAPI;
import fr.rudy.cities.manager.CityManager;
import fr.rudy.cities.manager.ClaimManager;
import fr.rudy.hud.manager.BossBarManager;
import fr.rudy.hud.manager.MenuItemManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    private BossBarManager bossBarManager;
    private MenuItemManager menuItemManager;

    private CityManager cityManager;
    private ClaimManager claimManager;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Téléphone/menu
        menuItemManager = new MenuItemManager(this);
        Bukkit.getPluginManager().registerEvents(menuItemManager, this);

        // Injection API Cities si dispo (après 1 tick pour s'assurer de son chargement)
        Bukkit.getScheduler().runTaskLater(this, this::setupCitiesIntegration, 1L);

        saveDefaultConfig();

        getLogger().info("✅ HUD plugin activé !");
    }

    private void setupCitiesIntegration() {
        Plugin citiesPlugin = Bukkit.getPluginManager().getPlugin("Cities");
        if (citiesPlugin != null && citiesPlugin.isEnabled()) {
            CitiesAPI api = Bukkit.getServicesManager().load(CitiesAPI.class);
            if (api != null) {
                cityManager = api.getCityManager();
                claimManager = api.getClaimManager();
            } else {
                getLogger().warning("⚠️ API Cities introuvable malgré la présence du plugin.");
            }
        } else {
            getLogger().info("ℹ️ Plugin Cities non trouvé.");
        }

        // Ne pas ré-enregistrer les events ici — cela est géré dans BossBarManager
        bossBarManager = new BossBarManager(this, cityManager, claimManager);
    }

    @Override
    public void onDisable() {
        getLogger().info("🛑 HUD plugin désactivé.");
    }

    public CityManager getCityManager() {
        return cityManager;
    }

    public ClaimManager getClaimManager() {
        return claimManager;
    }
}
