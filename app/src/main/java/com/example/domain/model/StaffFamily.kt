package com.example.domain.model

enum class StaffFamily(
    val displayName: String,
    val morphName: String,
    val morphDescription: String,
    val apiItemSuffix: String,
    val apiArtifactSuffix: String?,
    val isArtifactWeapon: Boolean = true
) {
    LIGHTCALLER(
        displayName = "Chamador da Luz",
        morphName = "Ave da Alvorada",
        morphDescription = "Transforma-se em uma radiante Ave da Alvorada, causando alto dano mágico contínuo em área.",
        apiItemSuffix = "2H_SHAPESHIFTER_UNDEAD",
        apiArtifactSuffix = "ARTEFACT_2H_SHAPESHIFTER_UNDEAD",
        isArtifactWeapon = true
    ),
    BLOODMOON(
        displayName = "Lua de Sangue",
        morphName = "Lobisomem",
        morphDescription = "Transforma-se em um feroz Lobisomem com roubo de vida violento e dano corpo a corpo contínuo.",
        apiItemSuffix = "2H_SHAPESHIFTER_MORGANA",
        apiArtifactSuffix = "ARTEFACT_2H_SHAPESHIFTER_MORGANA",
        isArtifactWeapon = true
    ),
    HELLSPAWN(
        displayName = "Cria Infernal",
        morphName = "Diabrete Infernal",
        morphDescription = "Transforma-se em um Diabrete flamejante que invoca clones explosivos e incinera inimigos.",
        apiItemSuffix = "2H_SHAPESHIFTER_HELL",
        apiArtifactSuffix = "ARTEFACT_2H_SHAPESHIFTER_HELL",
        isArtifactWeapon = true
    ),
    EARTHRUNE(
        displayName = "Runa da Terra",
        morphName = "Golem de Pedra",
        morphDescription = "Transforma-se em um colossal Golem de Rocha com enorme armadura e controle de grupo pesado.",
        apiItemSuffix = "2H_SHAPESHIFTER_KEEPER",
        apiArtifactSuffix = "ARTEFACT_2H_SHAPESHIFTER_KEEPER",
        isArtifactWeapon = true
    ),
    PRIMAL(
        displayName = "Cajado Primitivo",
        morphName = "Beemote Ancestral",
        morphDescription = "Transforma-se em um lendário Beemote Avaloniânico com interrupções e dano devastadores.",
        apiItemSuffix = "2H_SHAPESHIFTER_AVALON",
        apiArtifactSuffix = "ARTEFACT_2H_SHAPESHIFTER_AVALON",
        isArtifactWeapon = true
    ),
    ROOTBOUND(
        displayName = "Raiz-Presa",
        morphName = "Ente Silvestre",
        morphDescription = "Transforma-se em um antigo guardião arbóreo concedendo escudos maciços e suporte para o grupo.",
        apiItemSuffix = "2H_SHAPESHIFTER_SET1",
        apiArtifactSuffix = "ARTEFACT_2H_SHAPESHIFTER_SET1",
        isArtifactWeapon = true
    ),
    PROWLING(
        displayName = "Espreitador",
        morphName = "Pantera das Sombras",
        morphDescription = "Transforma-se em uma furtiva Pantera das Sombras especializada em emboscadas e assassinatos rápidos.",
        apiItemSuffix = "MAIN_SHAPESHIFTER",
        apiArtifactSuffix = null,
        isArtifactWeapon = false
    );

    companion object {
        fun fromId(name: String): StaffFamily? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}
