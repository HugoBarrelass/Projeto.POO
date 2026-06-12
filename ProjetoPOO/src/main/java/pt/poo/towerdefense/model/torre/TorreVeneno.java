package pt.poo.towerdefense.model.torre;

import pt.poo.towerdefense.model.inimigo.Inimigo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Torre Veneno — Aplica veneno (dano contínuo por segundo) aos inimigos.
 * Dano: 20, Alcance: 3, Custo: 120, HP: 100, Velocidade: Rápida (1.2 ataques/s).
 *
 * Conceito POO: Herança — herda de Torre.
 * Conceito POO: Polimorfismo — implementa atacar() com aplicação de veneno
 *               (chama aplicarVeneno() nos inimigos para dano ao longo do tempo).
 */
public class TorreVeneno extends Torre {

    private static final int DANO_VENENO_POR_TICK = 10;     // Dano de veneno por segundo
    private static final double DURACAO_VENENO = 3.0;       // Duração do efeito em segundos

    public TorreVeneno() {
        super("Torre Veneno", 20, 3, 120, 100, 1.2, "☠️");
    }

    @Override
    public List<Inimigo> atacar(List<Inimigo> inimigos) {
        List<Inimigo> atingidos = new ArrayList<>();
        if (!podeAtacar()) return atingidos;

        List<Inimigo> noAlcance = inimigosNoAlcance(inimigos);
        if (!noAlcance.isEmpty()) {
            // Espalha o veneno antes de renovar um efeito ja ativo.
            Inimigo alvo = noAlcance.stream()
                    .sorted(Comparator.comparing(Inimigo::isEnvenenado)
                            .thenComparing(Comparator.comparingDouble(Inimigo::getProgressoTotal).reversed()))
                    .findFirst()
                    .orElse(null);
            if (alvo != null) {
                alvo.receberDano(getDano());
                alvo.aplicarVeneno(DANO_VENENO_POR_TICK, DURACAO_VENENO);
                atingidos.add(alvo);
                resetCooldown();
            }
        }
        return atingidos;
    }
}
