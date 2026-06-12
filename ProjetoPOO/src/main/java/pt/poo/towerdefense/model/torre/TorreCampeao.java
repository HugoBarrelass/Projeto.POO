package pt.poo.towerdefense.model.torre;

import pt.poo.towerdefense.model.inimigo.Inimigo;
import pt.poo.towerdefense.model.inimigo.InimigoBoss;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Campeão — O herói mais forte. Dano dobrado contra o Boss.
 * Dano: 120, Alcance: 3, Custo: 250, HP: 200, Velocidade: Média (1.0 ataques/s).
 * Máximo 2 no mapa. Só pode ser comprado quando o Boss aparece.
 *
 * Conceito POO: Herança — herda de Torre.
 * Conceito POO: Polimorfismo — implementa atacar() com prioridade ao Boss
 *               e dano dobrado contra ele (usa instanceof InimigoBoss).
 */
public class TorreCampeao extends Torre {

    public static final int MAXIMO_NO_MAPA = 2;  // Regra de negócio: máximo 2 Campeões em simultâneo

    public TorreCampeao() {
        super("Campeão", 120, 3, 250, 200, 1.0, "⚔️");
    }

    /**
     * Prioriza o Boss; se não houver Boss, ataca o mais próximo.
     * Contra o Boss, o dano é duplicado (2×) — usa instanceof InimigoBoss.
     */
    @Override
    public List<Inimigo> atacar(List<Inimigo> inimigos) {
        List<Inimigo> atingidos = new ArrayList<>();
        if (!podeAtacar()) return atingidos;

        List<Inimigo> noAlcance = inimigosNoAlcance(inimigos);
        if (!noAlcance.isEmpty()) {
            // Prioriza o Boss; senão, o mais próximo
            Inimigo alvo = noAlcance.stream()
                    .filter(i -> i instanceof InimigoBoss)
                    .findFirst()
                    .orElse(noAlcance.stream()
                            .min(Comparator.comparingDouble(i -> getPosicao().distancia(i.getPosicaoAtual())))
                            .orElse(null));

            if (alvo != null) {
                // Dano dobrado contra o Boss (instanceof — verificação de tipo em runtime)
                int danoFinal = alvo instanceof InimigoBoss ? getDano() * 2 : getDano();
                alvo.receberDano(danoFinal);
                atingidos.add(alvo);
                resetCooldown();
            }
        }
        return atingidos;
    }
}
