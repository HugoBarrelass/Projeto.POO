package pt.poo.towerdefense.model.enums;

/**
 * Enumeração que representa os tipos de célula no mapa do jogo.
 * Cada célula da grelha do mapa tem exatamente um destes tipos,
 * que determina se pode receber uma torre, se é percorrida por inimigos, etc.
 *
 * Conceito POO: Enumeração — define um conjunto fixo de constantes tipadas,
 * garantindo segurança de tipos e evitando valores inválidos.
 */
public enum TipoCelula {
    /** Célula livre onde o jogador pode colocar heróis (torres). */
    LIVRE,

    /** Célula que faz parte de um caminho por onde os inimigos avançam. */
    CAMINHO,

    /** Célula que representa o castelo do jogador (objetivo a defender). */
    CASTELO,

    /** Célula de spawn — ponto de entrada dos inimigos no mapa. */
    SPAWN
}
