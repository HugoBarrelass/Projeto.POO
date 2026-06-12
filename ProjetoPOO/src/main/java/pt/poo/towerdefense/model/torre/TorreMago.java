package pt.poo.towerdefense.model.torre;

import pt.poo.towerdefense.model.inimigo.Inimigo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Mago — Ataque lento com dano em área (splash) limitado a 3 inimigos.
 * Dano: 40, Alcance: 4, Custo: 115, HP: 80, Velocidade: Lenta (0.55 ataques/s).
 *
 * Conceito POO: Herança — herda de Torre (classe abstrata).
 * Conceito POO: Polimorfismo — implementa atacar() com lógica de dano em área
 *               (ataca múltiplos inimigos de uma vez, ao contrário do Arqueiro).
 */
public class TorreMago extends Torre {

    private static final int MAX_ALVOS_AOE = 3;  // Número máximo de inimigos atingidos por ataque

    public TorreMago() {
        super("Mago", 40, 4, 115, 80, 0.55, "🧙");
    }

    /**
     * Dano em área (splash) — ataca até 3 inimigos de uma vez (polimorfismo de atacar()).
     * Ordena os inimigos por distância e ataca os mais próximos.
     */
    @Override
    public List<Inimigo> atacar(List<Inimigo> inimigos) {
        List<Inimigo> atingidos = new ArrayList<>();
        if (!podeAtacar()) return atingidos;

        List<Inimigo> noAlcance = inimigosNoAlcance(inimigos);
        if (!noAlcance.isEmpty()) {
            // Dano em área — ataca até MAX_ALVOS_AOE inimigos (os mais próximos)
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
                atingidos.add(inimigo);
            }
            resetCooldown();
        }
        return atingidos;
    }
}
