package pt.poo.towerdefense.model.torre;

import pt.poo.towerdefense.model.Posicao;
import pt.poo.towerdefense.model.inimigo.Inimigo;

import java.util.List;

/**
 * Classe abstrata que define os atributos e comportamentos comuns a todos os heróis (torres).
 * Cada herói tem dano, alcance, velocidade de ataque, custo e HP.
 *
 * Conceito POO: Abstração — define o contrato comum (atacar() é abstrato).
 * Conceito POO: Herança — 6 subclasses herdam desta classe (TorreArqueiro, TorreMago, etc.).
 * Conceito POO: Polimorfismo — o método atacar() comporta-se de forma diferente
 *               em cada subclasse (ataque único, área, abrandamento, veneno, etc.).
 * Conceito POO: Encapsulamento — atributos privados com getters.
 */
public abstract class Torre {
    private final String nome;                  // Nome legível do herói (ex: "Arqueiro")
    private final int dano;                     // Dano base por ataque
    private final int alcance;                  // Alcance em células
    private final int custo;                    // Custo em ouro para comprar
    private int hp;                             // Pontos de vida atuais
    private final int maxHp;                    // HP máximo (para barra de vida)
    private final double velocidadeAtaque;      // Ataques por segundo
    private Posicao posicao;                    // Posição no mapa (definida ao colocar)
    private double cooldownAtual;               // Tempo restante até poder atacar novamente
    private boolean abrandada;                  // true se afetada pelo Feiticeiro
    private double tempoAbrandamento;           // Tempo restante do efeito de abrandamento
    private double fatorVelocidadeAbrandada;    // Fator de redução da velocidade de ataque
    private final String emoji;                 // Emoji representativo (para UI de texto)

    /**
     * Construtor protegido — só pode ser chamado pelas subclasses (herança).
     * Cada subclasse chama super() com os seus valores específicos.
     */
    protected Torre(String nome, int dano, int alcance, int custo, int hp, double velocidadeAtaque, String emoji) {
        this.nome = nome;
        this.dano = dano;
        this.alcance = alcance;
        this.custo = custo;
        this.hp = hp;
        this.maxHp = hp;
        this.velocidadeAtaque = velocidadeAtaque;
        this.cooldownAtual = 0;
        this.abrandada = false;
        this.tempoAbrandamento = 0;
        this.fatorVelocidadeAbrandada = 1.0;
        this.emoji = emoji;
    }

    // Getters
    public String getNome() { return nome; }
    public int getDano() { return dano; }
    public int getAlcance() { return alcance; }
    public int getCusto() { return custo; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public double getVelocidadeAtaque() { return velocidadeAtaque; }
    public Posicao getPosicao() { return posicao; }
    public String getEmoji() { return emoji; }
    public boolean isAbrandada() { return abrandada; }

    public void setPosicao(Posicao posicao) { this.posicao = posicao; }

    /**
     * Método abstrato — cada torre ataca de forma diferente (POLIMORFISMO).
     * As subclasses implementam a sua própria lógica de ataque:
     * - TorreArqueiro: ataque a alvo único (o mais próximo)
     * - TorreMago: dano em área (splash) a até 3 inimigos
     * - TorreGelo: abranda até 3 inimigos (reduz velocidade)
     * - TorreCanhao: dano muito alto, prioriza blindados
     * - TorreVeneno: aplica veneno (dano contínuo)
     * - TorreCampeao: dano dobrado contra o Boss
     *
     * @param inimigos lista de inimigos na wave
     * @return lista de inimigos que foram atingidos (para animação de projéteis)
     */
    public abstract List<Inimigo> atacar(List<Inimigo> inimigos);

    /**
     * Verifica se a torre pode atacar (cooldown pronto).
     */
    public boolean podeAtacar() {
        return cooldownAtual <= 0;
    }

    /**
     * Reseta o cooldown após um ataque.
     * Se a torre está abrandada pelo Feiticeiro, o cooldown é mais longo
     * (velocidade de ataque reduzida).
     */
    public void resetCooldown() {
        double velocidade = abrandada ? velocidadeAtaque * fatorVelocidadeAbrandada : velocidadeAtaque;
        cooldownAtual = 1.0 / velocidade;  // Tempo = 1/velocidade (ex: 2 ataques/s = 0.5s)
    }

    /**
     * Atualiza o cooldown e efeitos de estado (chamado a cada frame do jogo).
     * Reduz o tempo de cooldown e verifica se o abrandamento expirou.
     */
    public void atualizarCooldown(double deltaTime) {
        if (cooldownAtual > 0) {
            cooldownAtual -= deltaTime;     // Reduz o cooldown pelo tempo decorrido
        }
        // Verificar se o efeito de abrandamento (do Feiticeiro) expirou
        if (abrandada) {
            tempoAbrandamento -= deltaTime;
            if (tempoAbrandamento <= 0) {
                abrandada = false;                  // Remove o efeito
                fatorVelocidadeAbrandada = 1.0;     // Restaura velocidade normal
            }
        }
    }

    /**
     * A torre recebe dano (de inimigos que atacam heróis).
     */
    public void receberDano(int dano) {
        this.hp = Math.max(0, this.hp - dano);
    }

    /**
     * Verifica se a torre ainda está viva.
     */
    public boolean isViva() {
        return hp > 0;
    }

    /**
     * Valor devolvido ao jogador quando vende uma torre voluntariamente (50%).
     */
    public int getValorVenda() {
        return (int) (custo * 0.5);
    }

    /**
     * Valor devolvido quando a torre é destruída (30%).
     */
    public int getValorReembolso() {
        return (int) (custo * 0.3);
    }

    /**
     * Aplica efeito de abrandamento do feiticeiro.
     */
    public void aplicarAbrandamento(double fatorVelocidade, double duracao) {
        this.abrandada = true;
        this.fatorVelocidadeAbrandada = fatorVelocidade;
        this.tempoAbrandamento = duracao;
    }

    /**
     * Filtra inimigos que estão dentro do alcance desta torre.
     * Usa posição interpolada para melhor precisão visual.
     * Tolerância de +0.5 para compensar que a posição é centro-a-centro.
     */
    protected List<Inimigo> inimigosNoAlcance(List<Inimigo> inimigos) {
        double alcanceEfetivo = alcance + 0.5; // tolerância para posição fracionária
        return inimigos.stream()
                .filter(Inimigo::isVivo)
                .filter(i -> !i.isChegouAoCastelo()) // ignorar fantasmas no castelo
                .filter(Inimigo::isSpawned) // ignorar inimigos que ainda não apareceram
                .filter(i -> {
                    double[] posVis = i.getPosicaoVisual();
                    if (posVis == null) return false;
                    double dx = posicao.getX() - posVis[0];
                    double dy = posicao.getY() - posVis[1];
                    double distSq = dx * dx + dy * dy;
                    return distSq <= alcanceEfetivo * alcanceEfetivo;
                })
                .toList();
    }

    @Override
    public String toString() {
        return emoji + " " + nome + " (HP:" + hp + "/" + maxHp + ")";
    }
}
