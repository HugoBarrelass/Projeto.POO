package pt.poo.towerdefense.model.inimigo;

import pt.poo.towerdefense.model.torre.Torre;

import java.util.List;

/**
 * Inimigo Rápido — Velocidade alta (2x a do Normal), difícil de acertar.
 * Vida: 80 (menos que o Normal), Velocidade: 2.0 cél/s, Recompensa: 7 ouro.
 *
 * Conceito POO: Herança — herda de Inimigo.
 * A velocidade alta é configurada no construtor via super(). Não precisa de override adicional.
 */
public class InimigoRapido extends Inimigo {

    public InimigoRapido() {
        super("Rápido", 80, 2.0, 7, "💨");
    }

    /** Sem habilidade ativa — a velocidade alta já é a sua vantagem (definida no construtor). */
    @Override
    public void habilidadeEspecial(List<Torre> torres) {
        // Método vazio: a velocidade 2x é a sua habilidade passiva
    }
}
