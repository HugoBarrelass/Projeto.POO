package pt.poo.towerdefense.model.inimigo;

import pt.poo.towerdefense.model.torre.Torre;

import java.util.List;

/**
 * Inimigo Normal — Sem habilidade especial, apenas anda em direção ao castelo.
 * Vida: 130, Velocidade: 1.0 cél/s, Recompensa: 5 ouro.
 *
 * Conceito POO: Herança — herda de Inimigo (classe abstrata).
 * É o tipo mais simples. A habilidadeEspecial() está vazia (polimorfismo).
 */
public class InimigoNormal extends Inimigo {

    /** Construtor — chama super() com os atributos do inimigo normal. */
    public InimigoNormal() {
        super("Normal", 130, 1.0, 5, "👹");  // nome, vida, velocidade, recompensa, emoji
    }

    /** Sem habilidade especial — implementação vazia do método abstrato. */
    @Override
    public void habilidadeEspecial(List<Torre> torres) {
        // Método vazio: o inimigo normal não tem habilidade ativa
    }
}
