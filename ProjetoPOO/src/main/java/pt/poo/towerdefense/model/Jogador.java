package pt.poo.towerdefense.model;

/**
 * Representa o estado do jogador durante uma partida do Tower Defense.
 * Guarda o ouro disponível, os pontos de vida (HP) do castelo, a pontuação
 * e as estatísticas (inimigos eliminados, waves completadas).
 *
 * Conceito POO: Encapsulamento — todos os atributos são privados.
 * O ouro só pode ser alterado via gastarOuro() e ganharOuro(),
 * garantindo que nunca fique negativo (validação interna).
 *
 * Conceito POO: Composição — o Jogo contém exatamente um Jogador (relação 1:1).
 */
public class Jogador {
    private String nome;                // Nome do jogador (inserido no ecrã inicial)
    private int ouro;                   // Ouro disponível para comprar heróis
    private int hpCastelo;              // HP atual do castelo
    private int maxHpCastelo;           // HP máximo do castelo (para calcular percentagem)
    private int pontuacao;              // Pontuação acumulada durante a partida
    private int inimigosEliminados;     // Contador de inimigos derrotados
    private int wavesCompletadas;       // Contador de waves sobrevividas

    /**
     * Cria um jogador com o nome, ouro inicial e HP do castelo.
     * Estes valores dependem do modo/nível/dificuldade escolhidos.
     *
     * @param nome nome do jogador
     * @param ouroInicial quantidade de ouro com que começa
     * @param hpCastelo pontos de vida iniciais do castelo
     */
    public Jogador(String nome, int ouroInicial, int hpCastelo) {
        this.nome = nome;
        this.ouro = ouroInicial;
        this.hpCastelo = hpCastelo;
        this.maxHpCastelo = hpCastelo;      // O máximo é definido pelo valor inicial
        this.pontuacao = 0;
        this.inimigosEliminados = 0;
        this.wavesCompletadas = 0;
    }

    // === Getters — acesso controlado aos atributos (encapsulamento) ===

    public String getNome() { return nome; }
    public int getOuro() { return ouro; }
    public int getHpCastelo() { return hpCastelo; }
    public int getMaxHpCastelo() { return maxHpCastelo; }
    public int getPontuacao() { return pontuacao; }
    public int getInimigosEliminados() { return inimigosEliminados; }
    public int getWavesCompletadas() { return wavesCompletadas; }

    // === Setters ===

    public void setNome(String nome) { this.nome = nome; }

    /**
     * Gasta ouro para comprar um herói (torre).
     * Valida se o jogador tem ouro suficiente antes de descontar.
     * Este método demonstra encapsulamento — o ouro nunca pode ficar negativo.
     *
     * @param quantidade custo do herói
     * @return true se o jogador tinha ouro suficiente e a compra foi efetuada
     */
    public boolean gastarOuro(int quantidade) {
        if (ouro >= quantidade) {
            ouro -= quantidade;             // Desconta o custo
            return true;
        }
        return false;                       // Ouro insuficiente
    }

    /**
     * Ganha ouro (recompensa por eliminar inimigos, venda de torres, bónus de wave).
     *
     * @param quantidade ouro a adicionar
     */
    public void ganharOuro(int quantidade) {
        ouro += quantidade;
    }

    /**
     * O castelo recebe dano quando um inimigo chega ao fim do caminho.
     * O HP nunca desce abaixo de 0 (usa Math.max para garantir).
     *
     * @param dano quantidade de dano (1 para inimigos normais, 3 para o Boss)
     */
    public void receberDano(int dano) {
        hpCastelo = Math.max(0, hpCastelo - dano);
    }

    /**
     * Verifica se o castelo ainda está de pé (condição de derrota: HP == 0).
     *
     * @return true se o castelo tem HP > 0
     */
    public boolean isVivo() {
        return hpCastelo > 0;
    }

    /**
     * Adiciona pontos à pontuação do jogador.
     * Os pontos vêm de recompensas de inimigos e bónus.
     *
     * @param pontos pontos a somar
     */
    public void adicionarPontuacao(int pontos) {
        pontuacao += pontos;
    }

    /**
     * Regista um inimigo eliminado (para estatísticas finais).
     */
    public void registarInimigoEliminado() {
        inimigosEliminados++;
    }

    /**
     * Regista uma wave completada com sucesso (para estatísticas finais).
     */
    public void registarWaveCompletada() {
        wavesCompletadas++;
    }

    /**
     * Calcula a pontuação final da partida.
     * Fórmula: pontuação acumulada + (HP restante × 50) + ouro restante.
     * O HP é muito valorizado para premiar jogadores que não deixam inimigos passar.
     *
     * @return pontuação final
     */
    public int calcularPontuacaoFinal() {
        return pontuacao + (hpCastelo * 50) + ouro;
    }
}
