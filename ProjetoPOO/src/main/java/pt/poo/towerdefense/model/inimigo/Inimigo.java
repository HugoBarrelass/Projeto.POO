package pt.poo.towerdefense.model.inimigo;

import pt.poo.towerdefense.model.Posicao;
import pt.poo.towerdefense.model.torre.Torre;

import java.util.List;

/**
 * Classe abstrata que define atributos e comportamentos comuns a todos os inimigos.
 * Cada inimigo tem vida, velocidade, recompensa em ouro e segue um caminho predefinido.
 *
 * Conceito POO: Abstração — define o contrato (habilidadeEspecial() é abstrato).
 * Conceito POO: Herança — 6 subclasses herdam desta classe.
 *   - Herança simples: InimigoNormal, InimigoRapido, InimigoArqueiro, InimigoFeiticeiro
 *   - Herança MULTI-NÍVEL: Inimigo ← InimigoTanque ← InimigoBoss
 * Conceito POO: Polimorfismo — habilidadeEspecial() comporta-se diferente em cada subclasse.
 * Conceito POO: Encapsulamento — atributos privados, acesso via getters.
 */
public abstract class Inimigo {
    // === Atributos base do inimigo (encapsulamento) ===
    private final String nome;                  // Nome legível (ex: "Normal", "Boss")
    private int vida;                           // Pontos de vida atuais
    private int maxVida;                        // Vida máxima (para barra de HP)
    private final double velocidadeBase;        // Velocidade original (células por segundo)
    private double velocidadeAtual;             // Velocidade atual (pode ser reduzida)
    private final int recompensa;               // Ouro ganho ao eliminar este inimigo
    private List<Posicao> caminho;              // Lista de posições que o inimigo segue
    private int indiceCaminho;                  // Índice atual no caminho (0 = spawn)
    private double progressoEntreCelulas;       // Progresso de 0.0 a 1.0 entre duas células (para animação suave)
    private boolean vivo;                       // true enquanto tiver HP > 0
    private boolean chegouAoCastelo;            // true quando atinge o fim do caminho
    private final String emoji;                 // Emoji representativo (para UI)

    // === Efeitos de estado (aplicados por torres) ===
    private boolean abrandado;                  // true se afetado pela Torre de Gelo
    private double fatorAbrandamento;           // Fator de redução de velocidade (ex: 0.65 = -35%)
    private double tempoAbrandamento;           // Tempo restante do abrandamento
    private boolean envenenado;                 // true se afetado pela Torre Veneno
    private int danoVenenoPorTick;              // Dano de veneno por segundo
    private double tempoVeneno;                 // Tempo restante do veneno
    private double danoVenenoAcumulado;         // Acumulador de dano fracionário do veneno

    // === Spawn escalonado ===
    private double atrasoSpawn;                 // Segundos de espera antes de começar a mover
    private boolean spawned;                    // true quando o atraso termina e o inimigo aparece

    /**
     * Construtor protegido — só pode ser chamado pelas subclasses (herança).
     * Cada subclasse define os seus próprios valores via super().
     */
    protected Inimigo(String nome, int vida, double velocidade, int recompensa, String emoji) {
        this.nome = nome;
        this.vida = vida;
        this.maxVida = vida;
        this.velocidadeBase = velocidade;
        this.velocidadeAtual = velocidade;
        this.recompensa = recompensa;
        this.indiceCaminho = 0;
        this.progressoEntreCelulas = 0.0;
        this.vivo = true;
        this.chegouAoCastelo = false;
        this.emoji = emoji;
        this.abrandado = false;
        this.envenenado = false;
        this.danoVenenoAcumulado = 0.0;
        this.atrasoSpawn = 0.0;
        this.spawned = true; // por defeito, sem atraso
    }

    // Getters
    public String getNome() { return nome; }
    public int getVida() { return vida; }
    public int getMaxVida() { return maxVida; }
    public double getVelocidadeBase() { return velocidadeBase; }
    public double getVelocidadeAtual() { return velocidadeAtual; }
    public int getRecompensa() { return recompensa; }
    public int getIndiceCaminho() { return indiceCaminho; }
    public double getProgressoEntreCelulas() { return progressoEntreCelulas; }
    public boolean isVivo() { return vivo; }
    public boolean isChegouAoCastelo() { return chegouAoCastelo; }
    public String getEmoji() { return emoji; }
    public boolean isAbrandado() { return abrandado; }
    public boolean isEnvenenado() { return envenenado; }
    public List<Posicao> getCaminho() { return caminho; }
    public boolean isSpawned() { return spawned; }

    /**
     * Escala a resistencia de inimigos no desafio infinito.
     */
    public void reforcar(double multiplicador) {
        this.maxVida = (int) Math.round(this.maxVida * multiplicador);
        this.vida = this.maxVida;
    }

    /**
     * Define o atraso antes do inimigo começar a mover-se.
     */
    public void setAtrasoSpawn(double atraso) {
        this.atrasoSpawn = atraso;
        this.spawned = (atraso <= 0);
    }

    /**
     * Define o caminho que este inimigo vai seguir.
     */
    public void setCaminho(List<Posicao> caminho) {
        this.caminho = caminho;
        this.indiceCaminho = 0;
        this.progressoEntreCelulas = 0.0;
    }

    /**
     * Move o inimigo ao longo do caminho.
     * @param deltaTime tempo decorrido desde o último update
     */
    public void mover(double deltaTime) {
        if (!vivo || chegouAoCastelo || caminho == null || caminho.isEmpty()) return;

        // Atraso de spawn: esperar antes de começar a andar
        if (!spawned) {
            atrasoSpawn -= deltaTime;
            if (atrasoSpawn <= 0) {
                spawned = true;
            } else {
                return; // ainda não spawnou, não se move
            }
        }

        double velocidade = abrandado ? velocidadeBase * fatorAbrandamento : velocidadeBase;
        progressoEntreCelulas += velocidade * deltaTime;

        while (progressoEntreCelulas >= 1.0 && indiceCaminho < caminho.size() - 1) {
            progressoEntreCelulas -= 1.0;
            indiceCaminho++;
        }

        // Chegou ao fim do caminho (castelo)
        if (indiceCaminho >= caminho.size() - 1 && progressoEntreCelulas >= 1.0) {
            chegouAoCastelo = true;
            progressoEntreCelulas = 1.0;
        }
    }

    /**
     * Retorna a posição atual interpolada do inimigo no mapa.
     */
    public Posicao getPosicaoAtual() {
        if (caminho == null || caminho.isEmpty()) return null;
        if (indiceCaminho >= caminho.size()) return caminho.get(caminho.size() - 1);
        return caminho.get(indiceCaminho);
    }

    /**
     * Retorna a posição visual interpolada (para animações suaves).
     */
    public double[] getPosicaoVisual() {
        if (caminho == null || caminho.isEmpty()) return new double[]{0, 0};
        
        Posicao atual = caminho.get(Math.min(indiceCaminho, caminho.size() - 1));
        if (indiceCaminho < caminho.size() - 1) {
            Posicao proximo = caminho.get(indiceCaminho + 1);
            double x = atual.getX() + (proximo.getX() - atual.getX()) * progressoEntreCelulas;
            double y = atual.getY() + (proximo.getY() - atual.getY()) * progressoEntreCelulas;
            return new double[]{x, y};
        }
        return new double[]{atual.getX(), atual.getY()};
    }

    /**
     * O inimigo recebe dano. Pode ser reduzido por armadura (override em subclasses).
     */
    public void receberDano(int dano) {
        int danoEfetivo = calcularDanoEfetivo(dano);
        this.vida = Math.max(0, this.vida - danoEfetivo);
        if (this.vida <= 0) {
            this.vivo = false;
        }
    }

    /**
     * Calcula o dano efetivo após armadura.
     * Na classe base, o dano não é reduzido.
     * Override no InimigoTanque para aplicar redução de armadura (POLIMORFISMO).
     */
    protected int calcularDanoEfetivo(int dano) {
        return dano;
    }

    /**
     * Retorna o dano que este inimigo causa ao castelo quando chega.
     * Override no Boss para 3 HP.
     */
    public int getDanoCastelo() {
        return 1;
    }

    /**
     * Habilidade especial do inimigo — MÉTODO ABSTRATO (polimorfismo).
     * Chamada a cada frame do jogo. Cada subclasse implementa a sua:
     * - InimigoNormal/InimigoRapido: sem habilidade
     * - InimigoArqueiro: ataca heróis próximos (dano direto)
     * - InimigoFeiticeiro: abranda a velocidade de ataque dos heróis
     * - InimigoTanque: sem habilidade (armadura é passiva)
     * - InimigoBoss: ataca heróis com dano elevado
     *
     * @param torres lista de torres no mapa (para os inimigos que atacam)
     */
    public abstract void habilidadeEspecial(List<Torre> torres);

    /**
     * Aplica efeito de abrandamento (da Torre de Gelo).
     */
    public void aplicarAbrandamento(double fator, double duracao) {
        this.abrandado = true;
        this.fatorAbrandamento = fator;
        this.tempoAbrandamento = duracao;
    }

    /**
     * Aplica efeito de veneno (da Torre Veneno).
     */
    public void aplicarVeneno(int danoPorTick, double duracao) {
        if (!this.envenenado) {
            this.danoVenenoAcumulado = 0.0;
        }
        this.envenenado = true;
        this.danoVenenoPorTick = danoPorTick;
        this.tempoVeneno = duracao;
    }

    /**
     * Atualiza efeitos de estado (veneno, abrandamento).
     */
    public void atualizarEfeitos(double deltaTime) {
        if (abrandado) {
            tempoAbrandamento -= deltaTime;
            if (tempoAbrandamento <= 0) {
                abrandado = false;
                velocidadeAtual = velocidadeBase;
            }
        }
        if (envenenado) {
            tempoVeneno -= deltaTime;
            danoVenenoAcumulado += danoVenenoPorTick * deltaTime;
            int danoAplicar = (int) danoVenenoAcumulado;
            if (danoAplicar > 0) {
                // Veneno ignora armadura; e aplicado como dano interno persistente.
                this.vida = Math.max(0, this.vida - danoAplicar);
                danoVenenoAcumulado -= danoAplicar;
            }
            if (this.vida <= 0) this.vivo = false;
            if (tempoVeneno <= 0) {
                envenenado = false;
                danoVenenoAcumulado = 0.0;
            }
        }
    }

    /**
     * Retorna a percentagem de vida restante (para barra de HP).
     */
    public double getPercentagemVida() {
        return (double) vida / maxVida;
    }

    /**
     * Retorna o progresso total ao longo do caminho (0.0 a 1.0).
     */
    public double getProgressoTotal() {
        if (caminho == null || caminho.size() <= 1) return 0;
        return (indiceCaminho + progressoEntreCelulas) / (caminho.size() - 1);
    }

    @Override
    public String toString() {
        return emoji + " " + nome + " (HP:" + vida + "/" + maxVida + ")";
    }
}
