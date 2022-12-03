package moe.oko.alcazar;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import moe.oko.alcazar.command.InventoryCommand;
import moe.oko.alcazar.handler.InventoryHandler;
import moe.oko.alcazar.handler.DeathMessageHandler;
import moe.oko.alcazar.command.WarpCommand;
import moe.oko.alcazar.command.WarpsCommand;
import moe.oko.alcazar.handler.WarpHandler;
import moe.oko.alcazar.listener.PlayerDeathListener;
import moe.oko.alcazar.listener.PlayerJoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Alcazar extends JavaPlugin {
    private AlcazarConfig config;
    private AlcazarDB db;
    private InventoryHandler inventoryHandler;
    private WarpHandler warpHandler;

    private DeathMessageHandler deathMessageHandler;
    private HolographicDisplaysAPI holographicDisplaysAPI;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.reloadConfig();

        config = new AlcazarConfig(this.getConfig());
        db = config.createDB(this);

        inventoryHandler = new InventoryHandler(this, db);
        warpHandler = new WarpHandler(this, db);
        deathMessageHandler = new DeathMessageHandler();
        holographicDisplaysAPI = getServer().getPluginManager().isPluginEnabled("HolographicDisplays")
                ? HolographicDisplaysAPI.get(this)
                : null;

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this, deathMessageHandler, holographicDisplaysAPI), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(config.getWelcomeMessage()), this);

        // Register commands
        this.getCommand("inv").setExecutor(new InventoryCommand(inventoryHandler));
        this.getCommand("warp").setExecutor(new WarpCommand(warpHandler));
        this.getCommand("warps").setExecutor(new WarpsCommand(warpHandler));

        info("Alcazar loaded.");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("Shutting down.");
    }

    // Logging methods
    public void info(String str) { getLogger().info(str); }
    public void warning(String str) { getLogger().warning(str); }
    public void severe(String str) { getLogger().severe(str); }
}
