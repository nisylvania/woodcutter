package net.nisylvania.woodcutter

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (label.equals("wood", ignoreCase = true)) {
            if (sender !is Player) return false
            if (PlayerListeners.disable.contains(sender)) {
                PlayerListeners.disable.remove(sender)
                sender.sendMessage(
                    (ChatColor.WHITE.toString() + "木こりプラグインを"
                            + ChatColor.BLUE + "有効化" + ChatColor.WHITE + "しました。")
                )
            } else {
                PlayerListeners.disable.add(sender)
                sender.sendMessage(
                    (ChatColor.WHITE.toString() + "木こりプラグインを"
                            + ChatColor.RED + "無効化" + ChatColor.WHITE + "しました。")
                )
            }
            return true
        }

        return false
    }
}