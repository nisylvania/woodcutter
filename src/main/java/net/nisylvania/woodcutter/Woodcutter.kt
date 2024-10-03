package net.nisylvania.woodcutter

import org.bukkit.plugin.java.JavaPlugin

class Woodcutter : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic

        // Plugin startup logic
        logger.info("木こりプラグインが起動しました。")
        getCommand("wood")?.setExecutor(Commands())
        PlayerListeners(this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}