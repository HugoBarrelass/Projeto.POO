package pt.poo.towerdefense.model.enums;

/**
 * Enumeração que representa os níveis de dificuldade do Modo Praticar.
 * Cada constante encapsula todos os parâmetros de configuração de uma dificuldade:
 * tamanho do mapa, número de caminhos, waves, ouro inicial e HP do castelo.
 *
 * Conceito POO: Enumeração com atributos — cada constante tem os seus próprios
 * valores, acedidos via getters (encapsulamento). O construtor é privado
 * (por defeito em enums), garantindo que só existem as instâncias definidas.
 */
public enum Dificuldade {
    /** Fácil: mapa 8×8, 1 caminho, 5 waves — ideal para aprender a mecânica. */
    FACIL("Fácil", 8, 1, 5, 180, 20),

    /** Médio: mapa 10×10, 2 caminhos, 10 waves — exige mais estratégia. */
    MEDIO("Médio", 10, 2, 10, 190, 20),

    /** Difícil: mapa 12×12, 2 caminhos, 15 waves — desafio máximo com menos recursos. */
    DIFICIL("Difícil", 12, 2, 15, 210, 15);

    // Atributos privados — encapsulamento (acesso só via getters)
    private final String nome;           // Nome legível da dificuldade
    private final int tamanhoMapa;       // Dimensão da grelha (NxN)
    private final int numeroCaminhos;    // Quantos caminhos os inimigos usam
    private final int numeroWaves;       // Quantas waves o jogador tem de sobreviver
    private final int ouroInicial;       // Ouro com que o jogador começa
    private final int hpCastelo;         // Pontos de vida iniciais do castelo

    /**
     * Construtor privado da enumeração — define os parâmetros de cada dificuldade.
     * @param nome nome legível (ex: "Fácil")
     * @param tamanhoMapa dimensão do mapa (NxN)
     * @param numeroCaminhos número de caminhos dos inimigos
     * @param numeroWaves total de waves a sobreviver
     * @param ouroInicial ouro inicial do jogador
     * @param hpCastelo HP inicial do castelo
     */
    Dificuldade(String nome, int tamanhoMapa, int numeroCaminhos, int numeroWaves, int ouroInicial, int hpCastelo) {
        this.nome = nome;
        this.tamanhoMapa = tamanhoMapa;
        this.numeroCaminhos = numeroCaminhos;
        this.numeroWaves = numeroWaves;
        this.ouroInicial = ouroInicial;
        this.hpCastelo = hpCastelo;
    }

    // === Getters — acesso controlado aos atributos (encapsulamento) ===

    public String getNome() { return nome; }
    public int getTamanhoMapa() { return tamanhoMapa; }
    public int getNumeroCaminhos() { return numeroCaminhos; }
    public int getNumeroWaves() { return numeroWaves; }
    public int getOuroInicial() { return ouroInicial; }
    public int getHpCastelo() { return hpCastelo; }
}
