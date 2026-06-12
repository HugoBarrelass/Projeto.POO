package pt.poo.towerdefense.model.inimigo;

import pt.poo.towerdefense.model.Posicao;
import pt.poo.towerdefense.model.torre.Torre;

import java.util.Comparator;
import java.util.List;

/**
 * Arqueiro Inimigo — Ataca os heróis enquanto anda pelo caminho (dano direto).
 * Vida: 100, Velocidade: 0.8 cél/s, Recompensa: 10 ouro.
 *
 * Conceito POO: Herança — herda de Inimigo.
 * Conceito POO: Polimorfismo — implementa habilidadeEspecial() para atacar
 *               a torre mais próxima dentro do alcance.
 * Conceito POO: Override — faz override de atualizarEfeitos() para gerir o cooldown
 *               do ataque (chamando super.atualizarEfeitos() para manter os efeitos base).
 */
public class InimigoArqueiro extends Inimigo {

    private static final int DANO_ATAQUE = 6;       // Dano infligido aos heróis por ataque
    private static final int ALCANCE_ATAQUE = 3;     // Alcance em células
    private double cooldownAtaque;                   // Tempo restante até poder atacar novamente

    public InimigoArqueiro() {
        super("Arqueiro Inimigo", 100, 0.8, 10, "🏴");
        this.cooldownAtaque = 0;
    }

    /**
     * Habilidade especial: ataca a torre (herói) mais próxima no alcance.
     * Usa Streams com Comparator para encontrar a torre mais perto.
     */
    @Override
    public void habilidadeEspecial(List<Torre> torres) {
        if (cooldownAtaque > 0 || !isVivo()) return;

        Posicao posAtual = getPosicaoAtual();
        if (posAtual == null) return;

        // Ataca a torre mais próxima no alcance
        Torre alvo = torres.stream()
                .filter(Torre::isViva)
                .filter(t -> t.getPosicao() != null && posAtual.distancia(t.getPosicao()) <= ALCANCE_ATAQUE)
                .min(Comparator.comparingDouble(t -> posAtual.distancia(t.getPosicao())))
                .orElse(null);

        if (alvo != null) {
            alvo.receberDano(DANO_ATAQUE);
            cooldownAtaque = 2.0; // 2s entre ataques
        }
    }

    /**
     * Override de atualizarEfeitos() para também gerir o cooldown do ataque.
     * Chama super.atualizarEfeitos() para manter o comportamento base (veneno, abrandamento).
     * Demonstra o uso correto de super em override.
     */
    @Override
    public void atualizarEfeitos(double deltaTime) {
        super.atualizarEfeitos(deltaTime);  // Chama o método da superclasse (herança)
        if (cooldownAtaque > 0) {
            cooldownAtaque -= deltaTime;    // Reduz cooldown do ataque
        }
    }
}
