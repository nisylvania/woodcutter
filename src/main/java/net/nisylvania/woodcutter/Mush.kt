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

class Mush(b: Block) {
    private val maxAmount = 1000

    /** 木であるかどうかを返す
     *
     * @return 木：Ture 木でない:False
     */
    val isMush: Boolean
    private val mushLog: MutableList<Block> = ArrayList()
    private var canCut = true

    /** 木を選択するコンストラクタ
     *
     * @param b 最初に破壊したブロック  キノコでなければならない
     */
    init {
        isMush = isMush(b)
    }

    /** 選択したものが木であるかを判別し、返す
     *
     * @param b 最初に壊したブロック
     * @return 木：Ture 木でない:False
     */
    private fun isMush(b: Block): Boolean {
        //原木と、隣接している葉をフィールドに入れていく
        mushLogic(b)

        //葉が原木に一つでも隣接していればTrueを返す
        return mushLog.size > 0
    }

    /** 周囲のブロックを検査し、原木があればさらにその周りを検査する
     *
     * @param firstBlock 最初に壊したブロック  キノコでなければならない
     */
    private fun mushLogic(firstBlock: Block) {
        val l = firstBlock.location.clone()
        canCut = searchAround(l)
    }

    /** ある原木の周りを検査し、原木と葉をフィールドに追加する
     *
     * ある原木のxy周囲8マス、y座標1増加した9増マス分を検査し
     * 同種原木なら再帰、葉なら追加してreturnする
     *
     *
     * @param center 検査する原木の座標
     * @return 検査の最大値を超えたかどうか (超えたらfalse)
     */
    private fun searchAround(center: Location): Boolean {
        if (mushLog.size > maxAmount) return false
        val l = center.clone()
        var b: Block

        //y=-1, 0, 1の順番に検査
        for (y in -1..1) {
            l.y = center.y + y
            for (x in -1..1) {
                l.x = center.x + x
                for (z in -1..1) {
                    l.z = center.z + z
                    b = l.block
                    if (isMushLog(b.type)) {
                        if (!mushLog.contains(b)) {
                            mushLog.add(b)
                            searchAround(l)
                        }
                    }
                }
            }
        }
        return mushLog.size <= maxAmount
    }

    /** 木を切る
     *
     * @param p 木を切ったPlayer
     * @return 実行したかどうか(trueは実行)
     */
    fun cut(p: Player): Boolean {
        if (!canCut) return false
        val tool = p.inventory.itemInMainHand
        object : BukkitRunnable() {
            override fun run() {
                for (b in mushLog) b.breakNaturally(tool)
            }
        }.run()

        consumption(p, mushLog.size)
        return true
    }

    /** プレイヤーの手に持っているツールの耐久値を減らす
     *
     * @param p Player
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

    companion object {
        @JvmStatic
        fun isMushLog(m: Material): Boolean {
            return m == Material.MUSHROOM_STEM || m == Material.BROWN_MUSHROOM_BLOCK || m == Material.RED_MUSHROOM_BLOCK
        }
    }
}