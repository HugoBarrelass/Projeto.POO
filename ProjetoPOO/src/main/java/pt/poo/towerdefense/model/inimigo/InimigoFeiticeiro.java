package pt.poo.towerdefense.model.inimigo;

import pt.poo.towerdefense.model.Posicao;
import pt.poo.towerdefense.model.torre.Torre;

import java.util.Comparator;
import java.util.List;

/**
 * Feiticeiro — Ataca os heróis e abranda a velocidade de ataque deles em 35% durante 3 segundos.
 * Vida: 90, Velocidade: 0.7 cél/s, Recompensa: 14 ouro.
 *
 * Conceito POO: Herança — herda de Inimigo.
 * Conceito POO: Polimorfismo — implementa habilidadeEspecial() para abrandar heróis.
 * Conceito POO: Interação entre objetos — chama torre.aplicarAbrandamento() para
 *               afetar a velocidade de ataque do herói mais próximo.
 */
public class InimigoFeiticeiro extends Inimigo {

    private static final int DANO_ATAQUE = 6;                  // Dano por ataque ao herói
    private static final int ALCANCE_ATAQUE = 3;                // Alcance em células
    private static final double DURACAO_ABRANDAMENTO = 3.0;     // Duração do efeito em segundos
    private double cooldownHabilidade;                          // Tempo até poder usar a habilidade novamente

    public InimigoFeiticeiro() {
        super("Feiticeiro", 90, 0.7, 14, "🧿");
        this.cooldownHabilidade = 0;
    }

    /**
     * Habilidade especial: causa dano E abranda a torre mais próxima.
     * O abrandamento reduz a velocidade de ataque da torre em 35% durante 3s.
     * Cooldown de 5 segundos entre utilizações.
     */
    @Override
    public void habilidadeEspecial(List<Torre> torres) {
        if (cooldownHabilidade > 0 || !isVivo()) return;

        Posicao posAtual = getPosicaoAtual();
        if (posAtual == null) return;

        // Abranda a torre mais próxima no alcance
        Torre alvo = torres.stream()
                .filter(Torre::isViva)
                .filter(t -> t.getPosicao() != null && posAtual.distancia(t.getPosicao()) <= ALCANCE_ATAQUE)
                .min(Comparator.comparingDouble(t -> posAtual.distancia(t.getPosicao())))
                .orElse(null);

        if (alvo != null) {
            alvo.receberDano(DANO_ATAQUE);                          // Causa dano ao herói
            alvo.aplicarAbrandamento(0.65, DURACAO_ABRANDAMENTO);   // Abranda o herói (interação entre objetos)
            cooldownHabilidade = 5.0;                               // 5 segundos de cooldown
        }
    }

    @Override
    public void atualizarEfeitos(double deltaTime) {
        super.atualizarEfeitos(deltaTime);
        if (cooldownHabilidade > 0) {
            cooldownHabilidade -= deltaTime;
        }
    }
}
