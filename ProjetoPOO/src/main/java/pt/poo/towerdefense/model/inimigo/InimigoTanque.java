package pt.poo.towerdefense.model.inimigo;

import pt.poo.towerdefense.model.torre.Torre;

import java.util.List;

/**
 * Inimigo Tanque — Muita vida com armadura que reduz o dano recebido em 20%.
 * Vida: 400, Velocidade: 0.5 cél/s, Recompensa: 20 ouro.
 *
 * Conceito POO: Herança — herda de Inimigo (classe abstrata).
 * Conceito POO: HERANÇA MULTI-NÍVEL — o InimigoBoss herda do InimigoTanque,
 *               formando a cadeia: Inimigo ← InimigoTanque ← InimigoBoss.
 * Conceito POO: Polimorfismo — faz override de calcularDanoEfetivo() para
 *               aplicar a redução de armadura (o Boss herda esta redução).
 */
public class InimigoTanque extends Inimigo {

    private final double armadura; // Percentagem de redução de dano (0.0 a 1.0, ex: 0.2 = 20%)

    /** Construtor público — cria um Tanque com valores padrão. */
    public InimigoTanque() {
        this("Tanque", 400, 0.5, 20, 0.2, "🛡️");  // 20% de armadura
    }

    /**
     * Construtor protegido — permite ao InimigoBoss personalizar os valores.
     * Demonstra herança multi-nível: o Boss chama este construtor via super().
     */
    protected InimigoTanque(String nome, int vida, double velocidade, int recompensa, double armadura, String emoji) {
        super(nome, vida, velocidade, recompensa, emoji);  // Chama construtor de Inimigo
        this.armadura = armadura;
    }

    public double getArmadura() { return armadura; }

    /**
     * Override de calcularDanoEfetivo() — aplica redução de armadura ao dano recebido.
     * O InimigoBoss HERDA esta redução automaticamente (herança multi-nível).
     * Exemplo: armadura 0.2 → dano efetivo = dano × (1 - 0.2) = dano × 0.8
     */
    @Override
    protected int calcularDanoEfetivo(int dano) {
        return (int) (dano * (1.0 - armadura));  // Dano reduzido pela percentagem de armadura
    }

    /** Sem habilidade ativa — a armadura é uma habilidade passiva (override de calcularDanoEfetivo). */
    @Override
    public void habilidadeEspecial(List<Torre> torres) {
        // Método vazio: a armadura já funciona passivamente via calcularDanoEfetivo()
    }
}
