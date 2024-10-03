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

class Tree(b: Block) {
    private val MAX_LOG_AMOUNT = 1000
    private val MAX_LEAVES_AMOUNT = 2000

    /** 木であるかどうかを返す
     *
     * @return 木：Ture 木でない:False
     */
    val isTree: Boolean
    private val logType = b.type
    private val leaveType: Material
    private val saplingType: Material
    private val index = WoodUtil.getIndex(b.type)
    private val treeLog = ArrayList<Block>()
    private val treeLeaves: MutableList<Block> = ArrayList()
    private val firstLayerLoc: MutableList<Location> = ArrayList()

    /** 木を選択するコンストラクタ
     *
     * @param b 最初に破壊したブロック  原木でなければならない
     */
    init {
        leaveType = WoodUtil.getLeavesMaterial(index)
        saplingType = WoodUtil.getSaplingMaterial(index)
        isTree = isTree(b)
    }

    /** 選択したものが木であるかを判別し、返す
     *
     * @param b 最初に壊したブロック
     * @return 木：Ture 木でない:False
     */
    private fun isTree(b: Block): Boolean {
        val under = b.location.clone()
        under.y = under.y - 1
        val ub = under.block.type

        //原木の下が土系のブロックか
        if (!(ub == Material.DIRT
                    || ub == Material.PODZOL
                    || ub == Material.COARSE_DIRT
                    || ub == Material.GRASS_BLOCK
                    || ub == Material.MYCELIUM
                    || ub == Material.CRIMSON_NYLIUM
                    || ub == Material.WARPED_NYLIUM
                    || ub == Material.NETHERRACK
                    || ub == Material.MOSS_BLOCK
                    || ub == Material.MUD
                    || ub == Material.MANGROVE_ROOTS
                    || ub == Material.MUDDY_MANGROVE_ROOTS
                    )
        ) {
            return false
        } else if (!(WoodUtil.isWood(b.type) || WoodUtil.isMangroveLog(b.type))) {
            return false
        }

        //原木と、隣接している葉をフィールドに入れていく
        val logIndex = WoodUtil.getIndex(b.type)
        if (logIndex == 0) {
            orkLogic(b, 4)
        } else if (WoodUtil.isMangroveLog(logIndex)) {
            mangroveLogic(b)
        } else {
            orkLogic(b, 3)
        }

        //葉が原木に一つでも隣接していればTrueを返す
        return treeLeaves.size > 0
    }

    /** 周囲のブロックを検査し、原木があればさらにその周りを検査する
     *
     * @param firstBlock 最初に壊したブロック  原木でなければならない
     */
    private fun orkLogic(firstBlock: Block, radius: Int) {
        val l = firstBlock.location.clone()
        var firstLayer = true

        while (l.block.type == logType) {
            if (tooBig()) {
                break
            } else {
                searchAround(l, firstLayer, radius)
            }
            if (firstLayer) firstLayer = false
            l.y = l.y + 1
        }
    }

    /** 周囲のブロックを検査し、原木があればさらにその周りを検査する
     *
     * @param firstBlock 最初に壊したブロック  原木でなければならない
     */
    private fun mangroveLogic(firstBlock: Block) {
        val l = firstBlock.location.clone()
        var firstLayer = true

        while (l.block.type == logType) {
            if (tooBig()) {
                break
            } else {
                searchAroundMangrove(l, firstLayer, 4)
            }
            if (firstLayer) firstLayer = false
            l.y = l.y + 1
        }
    }

    /** ある原木の周りを検査し、原木と葉をフィールドに追加する
     *
     * ある原木のxy周囲8マス、y座標1増加した9増マス分を検査し
     * 同種原木なら再帰、葉なら追加してreturnする
     *
     *
     * @param center 検査する原木の座標
     * @param firstLayer 地面に隣接している場所かどうか
     * @param radius 探索する範囲の半径(通常3)
     */
    private fun searchAround(center: Location, firstLayer: Boolean, radius: Int): Boolean {
        var firstLayer = firstLayer
        if (tooBig()) {
            return false
        }
        val l = center.clone()
        var b = l.block

        if (b.type == logType) {
            if (!treeLog.contains(b)) treeLog.add(b)
            if (firstLayer) firstLayerLoc.add(l.clone())
        } else if (b.type == leaveType) {
            if (!treeLeaves.contains(b)) treeLeaves.add(b)
            return true
        } else {
            return true
        }

        val sr = -radius + 2
        val er = radius - 1


        for (h in 0..1) {
            for (i in sr until er) {
                for (j in sr until er) {
                    l.x = center.x + i
                    l.z = center.z + j
                    b = l.block

                    if (b.type == logType && !treeLog.contains(b)) {
                        treeLog.add(b)
                        searchAround(l, firstLayer, radius)
                    } else if (b.type == leaveType && !treeLeaves.contains(b)) {
                        treeLeaves.add(b)
                    }
                    if (tooBig()) return false
                }
            }
            firstLayer = false
            l.y = center.y + 1
        }
        return true
    }

    /** ある原木の周りを検査し、原木と葉をフィールドに追加する
     *
     * ある原木のxy周囲8マス、y座標1増加した9増マス分を検査し
     * 同種原木なら再帰、葉なら追加してreturnする
     *
     *
     * @param center 検査する原木の座標
     * @param firstBlock 地面に隣接している場所かどうか
     * @param diameter 探索する範囲の直径(通常3)
     */
    private fun searchAroundMangrove(center: Location, firstBlock: Boolean, diameter: Int): Boolean {
        if (tooBig()) {
            return false
        }
        val l = center.clone()
        var b = l.block

        if (WoodUtil.isMangroveWood(b.type)) {
            if (!treeLog.contains(b)) treeLog.add(b)
            if (firstBlock) firstLayerLoc.add(l.clone())
        } else if (b.type == leaveType) {
            if (!treeLeaves.contains(b)) treeLeaves.add(b)
            return true
        } else {
            return true
        }

        //reamke
        val width = diameter / 2
        for (yShift in -1..1) {
            l.y = center.y + yShift
            for (xShift in -width..width) {
                for (zShift in -width..width) {
                    l.x = center.x + xShift
                    l.z = center.z + zShift
                    b = l.block
                    if (WoodUtil.isMangroveWood(b.type) && !treeLog.contains(b)) {
                        treeLog.add(b)
                        searchAroundMangrove(l, false, diameter)
                    } else if (b.type == leaveType && !treeLeaves.contains(b)) {
                        treeLeaves.add(b)
                    }
                    if (tooBig()) return false
                }
            }
        }

        return true
    }

    /** 木を切る
     *
     * @param p 木を切ったPlayer
     * @return 実行できたかどうか(出来ればtrue)
     */
    fun cut(p: Player): Boolean {
        if (tooBig()) return false
        val tool = p.inventory.itemInMainHand
        object : BukkitRunnable() {
            override fun run() {
                for (b in treeLog) b.breakNaturally(tool)
                for (b in treeLeaves) b.breakNaturally()
            }
        }.run()

        consumption(p, treeLog.size)
        return true
    }

    /** 木の大きさが限度を超えているかどうか
     *
     * @return 木の大きさが限度を超えているか(超えていればtrue)
     */
    private fun tooBig(): Boolean {
        return treeLog.size > MAX_LOG_AMOUNT || treeLeaves.size > MAX_LEAVES_AMOUNT
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
}