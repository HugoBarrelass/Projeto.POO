package pt.poo.towerdefense.model.torre;

import pt.poo.towerdefense.model.inimigo.Inimigo;
import pt.poo.towerdefense.model.inimigo.InimigoTanque;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Canhão — Explosão lenta que prioriza inimigos pesados e causa dano residual.
 * Dano: 160, Alcance: 2, Custo: 140, HP: 120, Velocidade: Muito lenta (0.45 ataques/s).
 *
 * Conceito POO: Herança — herda de Torre.
 * Conceito POO: Polimorfismo — implementa atacar() com dano aumentado contra blindados
 *               (usa instanceof para verificar se o alvo é InimigoTanque — +50% dano).
 */
public class TorreCanhao extends Torre {

    public TorreCanhao() {
        super("Canhão", 160, 2, 140, 120, 0.45, "💣");
    }

    /**
     * Ataca até 2 inimigos, priorizando os com mais vida.
     * O primeiro alvo recebe dano completo; o segundo recebe 40% (explosão secundária).
     * Contra inimigos blindados (InimigoTanque), o dano é aumentado em 50% (instanceof).
     */
    @Override
    public List<Inimigo> atacar(List<Inimigo> inimigos) {
        List<Inimigo> atingidos = new ArrayList<>();
        if (!podeAtacar()) return atingidos;

        List<Inimigo> noAlcance = inimigosNoAlcance(inimigos);
        if (!noAlcance.isEmpty()) {
            List<Inimigo> alvos = noAlcance.stream()
                    .sorted(Comparator.comparingInt(Inimigo::getVida).reversed())
                    .limit(2)
                    .toList();
            for (int i = 0; i < alvos.size(); i++) {
                Inimigo alvo = alvos.get(i);
                int danoBase = i == 0 ? getDano() : (int) Math.round(getDano() * 0.4); // 100% no 1º, 40% no 2º
                int dano = alvo instanceof InimigoTanque ? (int) (danoBase * 1.5) : danoBase; // +50% contra blindados
                alvo.receberDano(dano);
                atingidos.add(alvo);
            }
            resetCooldown();
        }
        return atingidos;
    }
}
