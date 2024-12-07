package net.nisylvania.woodcutter

import org.bukkit.Material

object WoodUtil {
    /** 木の種類
     * オーク -> 0
     * 松    -> 1
     * 白樺  -> 2
     * ジャングル  -> 3
     * アカシア    -> 4
     * ダークオーク -> 5
     * 深紅の木 -> 6
     * 歪んだ木 -> 7
     * マングローブの原木 -> 8
     * マングローブの根 -> 9
     * 桜 -> 10
     * ペールオーク -> 11
     */
    private val logs: List<Material> = listOf(
        Material.OAK_LOG,  //0
        Material.SPRUCE_LOG,
        Material.BIRCH_LOG,
        Material.JUNGLE_LOG,
        Material.ACACIA_LOG,  //4
        Material.DARK_OAK_LOG,
        Material.CRIMSON_STEM,
        Material.WARPED_STEM,
        Material.MANGROVE_LOG,
        Material.MANGROVE_ROOTS,  //9
        Material.CHERRY_LOG,
        Material.PALE_OAK_LOG
    )

    private val leaves: List<Material> = listOf(
        Material.OAK_LEAVES,  //0
        Material.SPRUCE_LEAVES,
        Material.BIRCH_LEAVES,
        Material.JUNGLE_LEAVES,
        Material.ACACIA_LEAVES,  //4
        Material.DARK_OAK_LEAVES,
        Material.NETHER_WART_BLOCK,
        Material.WARPED_WART_BLOCK,
        Material.MANGROVE_LEAVES,
        Material.MANGROVE_LEAVES,  //9
        Material.CHERRY_LEAVES,
        Material.PALE_OAK_LEAVES
    )

    private val saplings: List<Material> = listOf(
        Material.OAK_SAPLING,  //0
        Material.SPRUCE_SAPLING,
        Material.BIRCH_SAPLING,
        Material.JUNGLE_SAPLING,
        Material.ACACIA_SAPLING,  //4
        Material.DARK_OAK_SAPLING,
        Material.CRIMSON_FUNGUS,
        Material.WARPED_FUNGUS,
        Material.MANGROVE_PROPAGULE,
        Material.MANGROVE_PROPAGULE,  //9
        Material.CHERRY_SAPLING,
        Material.PALE_OAK_SAPLING
    )

    /** Materialが原木かどうかを返す
     *
     * @param type Material
     * @return 原木:true 原木でない:false
     */
    fun isWood(type: Material): Boolean {
        return logs.contains(type)
    }

    fun isNetherWood(type: Material): Boolean {
        return type == Material.CRIMSON_STEM || type == Material.WARPED_STEM
    }

    fun isMangroveWood(type: Material): Boolean {
        return type == Material.MANGROVE_LOG || type == Material.MANGROVE_ROOTS
    }

    /** Materialが葉かどうかを返す
     *
     * @param type Material
     * @return 葉:true 葉でない:false
     */
    fun isLeaves(type: Material): Boolean {
        return leaves.contains(type)
    }

    /** woodcutterでの木の種類を返す
     * 木の種類は上記
     *
     * @param wood 原木
     * @return woodcutterでの木の種類
     */
    fun getIndex(wood: Material): Int {
        for ((i, m) in logs.withIndex()) {
            if (wood == m) return i
        }
        return -1
    }

    /** woodcutterでの木の種類を原木に変換する
     *
     * @param index woodcutterでの木の種類
     * @return 原木のMaterial
     */
    fun getLogMaterial(index: Int): Material {
        return logs[index]
    }

    /** woodcutterでの木の種類を葉に変換する
     *
     * @param index woodcutterでの木の種類
     * @return 葉のMaterial
     */
    fun getLeavesMaterial(index: Int): Material {
        return leaves[index]
    }

    /** woodcutterでの木の種類を苗に変換する
     *
     * @param index woodcutterでの木の種類
     * @return 苗のMaterial
     */
    fun getSaplingMaterial(index: Int): Material {
        return saplings[index]
    }

    /**
     * ネザー関連の原木かどうかを返す
     *
     * @param index 調べるindex
     * @return ネザー関連ならtrue
     */
    fun isNetherLog(index: Int): Boolean {
        return index in 6..7
    }

    /**
     * ネザー関連の原木かどうかを返す
     *
     * @param material 調べるindex
     * @return ネザー関連ならtrue
     */
    fun isNetherLog(material: Material): Boolean {
        return isNetherLog(getIndex(material))
    }

    /**
     * マングローブ関連の原木かどうかを返す
     *
     * @param index 調べるindex
     * @return マングローブならtrue
     */
    fun isMangroveLog(index: Int): Boolean {
        return index in 8..9
    }

    /**
     * マングローブ関連の原木かどうかを返す
     *
     * @param material 調べるindex
     * @return マングローブならtrue
     */
    fun isMangroveLog(material: Material): Boolean {
        return isMangroveLog(getIndex(material))
    }
}