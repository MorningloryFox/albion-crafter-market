package com.example.domain.model

enum class Tier(
    val level: Int,
    val label: String,
    val roman: String,
    val colorHex: Long
) {
    T4(4, "T4", "IV", 0xFF3B82F6), // Azul
    T5(5, "T5", "V", 0xFFEF4444),  // Vermelho
    T6(6, "T6", "VI", 0xFFF97316), // Laranja
    T7(7, "T7", "VII", 0xFFEAB308), // Amarelo/Ouro
    T8(8, "T8", "VIII", 0xFF06B6D4); // Ciano/Diamante

    companion object {
        fun fromLevel(lvl: Int): Tier = entries.find { it.level == lvl } ?: T4
    }
}

enum class Enchantment(
    val level: Int,
    val label: String,
    val dotSuffix: String,
    val colorHex: Long
) {
    NONE(0, ".0", "", 0xFF9CA3AF),         // Cinza
    UNCOMMON(1, ".1", "@1", 0xFF22C55E),   // Verde
    RARE(2, ".2", "@2", 0xFF3B82F6),       // Azul
    EXCEPTIONAL(3, ".3", "@3", 0xFFA855F7); // Roxo

    companion object {
        fun fromLevel(lvl: Int): Enchantment = entries.find { it.level == lvl } ?: NONE
    }
}

enum class MarketType(val displayName: String, val shortName: String) {
    CAERLEON("Mercado de Caerleon", "Caerleon"),
    BLACK_MARKET("Mercado Negro", "Mercado Negro")
}

enum class ActionRecommendation(
    val label: String,
    val description: String,
    val isCraft: Boolean
) {
    CRAFTAR(
        label = "CRAFTAR",
        description = "Craftar gera retornos superiores em comparação com a venda dos componentes brutos.",
        isCraft = true
    ),
    VENDER_CRU(
        label = "VENDER CRU",
        description = "Lucro inferior ao custo de oportunidade dos materiais/artefato. Venda os itens brutos diretamente.",
        isCraft = false
    )
}
