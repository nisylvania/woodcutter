package net.nisylvania.woodcutter

import net.nisylvania.woodcutter.Mush.Companion.isMushLog
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class PlayerListeners(main: Woodcutter) : Listener {
    init {
        main.server.pluginManager.registerEvents(this, main)
    }

    @EventHandler
    fun brockBreakEvent(e: BlockBreakEvent) {
        //サバイバルとアドベンチャー以外除外

        if (e.player.gameMode != GameMode.SURVIVAL && e.player.gameMode != GameMode.ADVENTURE) return

        //壊したブロックが原木orキノコであるか
        //survival or adventure でのみ使用可
        if ((WoodUtil.isWood(e.block.type) || isMushLog(e.block.type)) && !disable.contains(e.player)) {
            //Can only be used if it contains the "AXE" name

            val its = e.player.inventory.itemInMainHand
            if (!its.type.name.matches((".*" + "AXE" + ".*").toRegex())) return

            val execute: Boolean

            //select tree block
            if (isMushLog(e.block.type)) {
                val mush = Mush(e.block)
                if (!mush.isMush) return
                execute = mush.cut(e.player)
                if (!execute) e.player.sendMessage(ChatColor.RED.toString() + "この木は制限を超えています。")
                return
            } else {
                val tree = Tree(e.block)
                if (!tree.isTree) return
                execute = tree.cut(e.player)
                if (!execute) e.player.sendMessage(ChatColor.RED.toString() + "この木は制限を超えています。")
            }

            if (execute) e.isCancelled = true

            //シャベルでマングローブの泥の根を破壊した時
        } else if (e.block.type == Material.MUDDY_MANGROVE_ROOTS && !disable.contains(e.player)) {
            //Can only be used if it contains the "SHOVE" name

            val its = e.player.inventory.itemInMainHand
            if (!its.type.name.matches((".*" + "SHOVEL" + ".*").toRegex())) return


            if (e.block.type == Material.MUDDY_MANGROVE_ROOTS) {
                val mmr = MuddyMangroveRoots(e.block)
                if (!mmr.dig(e.player)) {
                    e.player.sendMessage(ChatColor.RED.toString() + "この木は制限を超えています。")
                }
            }
        }
    }

    companion object {
        var disable: MutableList<Player> = ArrayList()
    }
}