package pt.poo.towerdefense.model.inimigo;

import pt.poo.towerdefense.model.Posicao;
import pt.poo.towerdefense.model.torre.Torre;

import java.util.Comparator;
import java.util.List;

/**
 * Boss — O inimigo mais forte do jogo.
 * Vida: 1200, Velocidade: 0.3 cél/s, Recompensa: 70 ouro.
 * Armadura: 30% (herdada do InimigoTanque), ataca heróis, tira 3 HP ao castelo.
 *
 * Conceito POO: HERANÇA MULTI-NÍVEL — cadeia: Inimigo ← InimigoTanque ← InimigoBoss.
 *   - Do InimigoTanque herda: armadura (calcularDanoEfetivo()) e construtor protegido.
 *   - Do Inimigo herda: vida, velocidade, movimento, efeitos de estado, etc.
 * Conceito POO: Override — faz override de getDanoCastelo() (3 em vez de 1)
 *               e de habilidadeEspecial() (ataca heróis com dano elevado).
 * Conceito POO: Sobrecarga (Overload) — tem 2 construtores (padrão e com vida extra).
 */
public class InimigoBoss extends InimigoTanque {

    private static final int DANO_ATAQUE = 20;      // Dano aos heróis (muito superior aos outros inimigos)
    private static final int ALCANCE_ATAQUE = 2;     // Alcance curto (precisa estar perto)
    private double cooldownAtaque;                   // Cooldown entre ataques

    /** Construtor padrão — Boss com 1200 HP e 30% de armadura (herdada do Tanque). */
    public InimigoBoss() {
        super("Boss", 1200, 0.3, 70, 0.3, "👿");  // Chama construtor do InimigoTanque (herança multi-nível)
        this.cooldownAtaque = 0;
    }

    /**
     * Construtor com vida extra — sobrecarga (overload) do construtor.
     * Usado nas waves avançadas do modo endless para criar Bosses mais fortes.
     */
    public InimigoBoss(int vidaExtra) {
        super("Boss", 1200 + vidaExtra, 0.3, 70, 0.3, "👿");  // Vida aumentada
        this.cooldownAtaque = 0;
    }

    /**
     * Override de getDanoCastelo() — o Boss causa 3 HP de dano ao castelo (em vez de 1).
     * Demonstra polimorfismo: o método base retorna 1, mas o Boss retorna 3.
     */
    @Override
    public int getDanoCastelo() {
        return 3;  // 3x mais dano ao castelo que inimigos normais
    }

    /**
     * Habilidade especial: ataca a torre mais próxima com dano muito elevado (20).
     * O Boss é uma ameaça tanto ao castelo como aos heróis.
     */
    @Override
    public void habilidadeEspecial(List<Torre> torres) {
        if (cooldownAtaque > 0 || !isVivo()) return;

        Posicao posAtual = getPosicaoAtual();
        if (posAtual == null) return;

        Torre alvo = torres.stream()
                .filter(Torre::isViva)
                .filter(t -> t.getPosicao() != null && posAtual.distancia(t.getPosicao()) <= ALCANCE_ATAQUE)
                .min(Comparator.comparingDouble(t -> posAtual.distancia(t.getPosicao())))
                .orElse(null);

        if (alvo != null) {
            alvo.receberDano(DANO_ATAQUE);
            cooldownAtaque = 2.5; // 2.5s entre ataques
        }
    }

    @Override
    public void atualizarEfeitos(double deltaTime) {
        super.atualizarEfeitos(deltaTime);
        if (cooldownAtaque > 0) {
            cooldownAtaque -= deltaTime;
        }
    }
}
