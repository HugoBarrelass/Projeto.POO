package pt.poo.towerdefense.model.torre;

import pt.poo.towerdefense.model.inimigo.Inimigo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Arqueiro — Ataque rápido a alvo único.
 * Dano: 20, Alcance: 3, Custo: 65, HP: 100, Velocidade: Rápida (1.6 ataques/s).
 *
 * Conceito POO: Herança — herda de Torre (classe abstrata).
 * Conceito POO: Polimorfismo — implementa atacar() com lógica de alvo único
 *               (dispara no inimigo mais próximo dentro do alcance).
 */
public class TorreArqueiro extends Torre {

    /** Construtor — chama super() com os atributos específicos do Arqueiro. */
    public TorreArqueiro() {
        super("Arqueiro", 20, 3, 65, 100, 1.6, "🏹");  // nome, dano, alcance, custo, hp, vel, emoji
    }

    /**
     * Ataque a alvo único — dispara no inimigo mais próximo (polimorfismo de atacar()).
     * Usa Streams com Comparator para encontrar o inimigo mais perto.
     */
    @Override
    public List<Inimigo> atacar(List<Inimigo> inimigos) {
        List<Inimigo> atingidos = new ArrayList<>();
        if (!podeAtacar()) return atingidos;

        List<Inimigo> noAlcance = inimigosNoAlcance(inimigos);  // Método herdado de Torre
        if (!noAlcance.isEmpty()) {
            // Encontra o inimigo mais próximo usando Stream + Comparator (programação funcional)
            Inimigo alvo = noAlcance.stream()
                    .min(Comparator.comparingDouble(i -> {
                        double[] pos = i.getPosicaoVisual();
                        double dx = getPosicao().getX() - pos[0];
                        double dy = getPosicao().getY() - pos[1];
                        return Math.sqrt(dx * dx + dy * dy);
                    }))
                    .orElse(null);
            if (alvo != null) {
                alvo.receberDano(getDano());
                atingidos.add(alvo);
                resetCooldown();
            }
        }
        return atingidos;
    }
}
