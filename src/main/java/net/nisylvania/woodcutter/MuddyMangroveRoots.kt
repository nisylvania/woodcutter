package net.nisylvania.woodcutter

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.scheduler.BukkitRunnable

class MuddyMangroveRoots(b: Block) {
    private val MAX_TRANSITION = 3 //四方への最大遷移数, (遷移: transition)
    private val MAX_AMOUNTS = 1000
    private val roots = ArrayList<Block>()

    init {
        mmrLogic(b, 1)
    }

    private fun mmrLogic(b: Block, transition: Int) {
        if (b.type != Material.MUDDY_MANGROVE_ROOTS || transition > MAX_TRANSITION || roots.size > MAX_AMOUNTS) return

        if (!roots.contains(b)) {
            roots.add(b)
        } else {
            return
        }

        //四方としたを追加
        mmrLogic((Location(b.world, (b.x + 1).toDouble(), b.y.toDouble(), b.z.toDouble())).block, transition + 1)
        mmrLogic((Location(b.world, (b.x - 1).toDouble(), b.y.toDouble(), b.z.toDouble())).block, transition + 1)
        mmrLogic((Location(b.world, b.x.toDouble(), b.y.toDouble(), (b.z + 1).toDouble())).block, transition + 1)
        mmrLogic((Location(b.world, b.x.toDouble(), b.y.toDouble(), (b.z - 1).toDouble())).block, transition + 1)
        mmrLogic((Location(b.world, b.x.toDouble(), (b.y - 1).toDouble(), b.z.toDouble())).block, transition)
        mmrLogic((Location(b.world, b.x.toDouble(), (b.y + 1).toDouble(), b.z.toDouble())).block, transition)
    }

    fun dig(p: Player): Boolean {
        if (roots.size > MAX_AMOUNTS) return false
        val tool = p.inventory.itemInMainHand
        object : BukkitRunnable() {
            override fun run() {
                for (b in roots) b.breakNaturally(tool)
            }
        }.run()
        consumption(p, roots.size)
        return true
    }

    /**
     * プレイヤーの手に持っているツールの耐久値を減らす
     *
     * @param p     Player
     * @param value 減らす値
     */
    private fun consumption(p: Player, value: Int) {
        val tool = p.inventory.itemInMainHand
        val level = tool.getEnchantmentLevel(Enchantment.UNBREAKING)
        //cf https://minecraft-ja.gamepedia.com/%E8%80%90%E4%B9%85%E5%8A%9B
        val decreaseProbability = (60.0 + (40.0 / (level + 1.0))) / 100.0

        var decrease = (durability(tool) + (value * decreaseProbability).toInt().toShort()).toShort()
        if (tool.type.maxDurability == durability(tool)) {
            p.playSound(p.location, Sound.ENTITY_ITEM_BREAK, 100f, 1f)
            p.spawnParticle(Particle.ITEM, p.location, 40, tool)
            p.inventory.setItemInMainHand(null)
            return
        } else if (tool.type.maxDurability < decrease) {
            decrease = tool.type.maxDurability
        }
        setDurability(tool, decrease)
    }

    private fun durability(item: ItemStack): Short {
        val meta = item.itemMeta
        return if (meta == null) 0 else (meta as Damageable).damage.toShort()
    }

    private fun setDurability(item: ItemStack, durability: Short) {
        val meta = item.itemMeta
        if (meta != null) {
            (meta as Damageable).damage = durability.toInt()
            item.setItemMeta(meta)
        }
    }
}