package pt.poo.towerdefense.model.torre;

import pt.poo.towerdefense.model.inimigo.Inimigo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Torre de Gelo — Abranda até 3 inimigos no alcance e causa dano reduzido.
 * Dano: 8, Alcance: 3, Custo: 100, HP: 90, Velocidade: Média (0.8 ataques/s).
 *
 * Conceito POO: Herança — herda de Torre (classe abstrata).
 * Conceito POO: Polimorfismo — implementa atacar() com efeito de abrandamento
 *               (chama aplicarAbrandamento() nos inimigos, reduzindo a velocidade deles).
 */
public class TorreGelo extends Torre {

    private static final double FATOR_ABRANDAMENTO = 0.65; // Reduz velocidade dos inimigos em 35%
    private static final int MAX_ALVOS_AOE = 3;             // Número máximo de inimigos afetados

    public TorreGelo() {
        super("Torre de Gelo", 8, 3, 100, 90, 0.8, "❄️");
    }

    /**
     * Abranda até 3 inimigos — causa dano e aplica efeito de abrandamento.
     * Os inimigos abrandados movem-se 35% mais devagar durante 2 segundos.
     */
    @Override
    public List<Inimigo> atacar(List<Inimigo> inimigos) {
        List<Inimigo> atingidos = new ArrayList<>();
        if (!podeAtacar()) return atingidos;

        List<Inimigo> noAlcance = inimigosNoAlcance(inimigos);
        if (!noAlcance.isEmpty()) {
            // Abranda até MAX_ALVOS_AOE inimigos (os mais próximos)
            List<Inimigo> alvos = noAlcance.stream()
                    .sorted(Comparator.comparingDouble(i -> {
                        double[] pos = i.getPosicaoVisual();
                        double dx = getPosicao().getX() - pos[0];
                        double dy = getPosicao().getY() - pos[1];
                        return dx * dx + dy * dy;
                    }))
                    .limit(MAX_ALVOS_AOE)
                    .toList();

            for (Inimigo inimigo : alvos) {
                inimigo.receberDano(getDano());
                inimigo.aplicarAbrandamento(FATOR_ABRANDAMENTO, 2.0);
                atingidos.add(inimigo);
            }
            resetCooldown();
        }
        return atingidos;
    }
}
