package pt.poo.towerdefense.model;

import pt.poo.towerdefense.model.enums.Dificuldade;

import java.util.List;

/**
 * Modo Praticar — O jogador escolhe a dificuldade e treina livremente.
 * Todos os heróis e inimigos da dificuldade escolhida estão disponíveis desde o início.
 *
 * Conceito POO: Herança — herda de ModoJogo (classe abstrata) e implementa
 * todos os métodos abstratos com base na dificuldade escolhida.
 *
 * Conceito POO: Associação — usa uma Dificuldade (enum) para obter os parâmetros.
 * Ao contrário do ModoHistoria, não tem progressão — cada partida é independente.
 *
 * Conceito POO: Polimorfismo — o switch expression em getHeroisDisponiveis() e
 * getInimigosDisponiveis() retorna listas diferentes conforme a dificuldade.
 */
public class ModoPraticar extends ModoJogo {
    private Dificuldade dificuldade;  // Dificuldade selecionada (FACIL, MEDIO ou DIFICIL)

    /**
     * Construtor — cria o modo praticar com a dificuldade escolhida.
     * @param dificuldade nível de dificuldade selecionado pelo jogador
     */
    public ModoPraticar(Dificuldade dificuldade) {
        super("Modo Praticar");          // Chama construtor da superclasse (herança)
        this.dificuldade = dificuldade;  // Guarda a dificuldade escolhida
    }

    public Dificuldade getDificuldade() { return dificuldade; }
    public void setDificuldade(Dificuldade dificuldade) { this.dificuldade = dificuldade; }

    // === Implementação dos métodos abstratos de ModoJogo (polimorfismo) ===
    // Cada método delega para a enumeração Dificuldade.

    @Override
    public String getDescricao() {
        return "Treina livremente na dificuldade " + dificuldade.getNome() + "!";
    }

    @Override
    public int getTamanhoMapa() { return dificuldade.getTamanhoMapa(); }

    @Override
    public int getNumeroCaminhos() { return dificuldade.getNumeroCaminhos(); }

    @Override
    public int getNumeroWaves() { return dificuldade.getNumeroWaves(); }

    @Override
    public int getOuroInicial() { return dificuldade.getOuroInicial(); }

    @Override
    public int getHpCastelo() { return dificuldade.getHpCastelo(); }

    /**
     * Retorna os heróis disponíveis para a dificuldade escolhida.
     * Usa switch expression (Java 14+) — retorna listas diferentes por dificuldade.
     * Demonstra polimorfismo: o mesmo método retorna resultados diferentes.
     */
    @Override
    public List<String> getHeroisDisponiveis() {
        return switch (dificuldade) {
            case FACIL -> List.of("Arqueiro", "Mago", "TorreGelo");              // 3 heróis básicos
            case MEDIO -> List.of("Arqueiro", "Mago", "TorreGelo", "Canhao");   // + Canhão
            case DIFICIL -> List.of("Arqueiro", "Mago", "TorreGelo", "Canhao", "TorreVeneno", "Campeao"); // Todos
        };
    }

    /**
     * Retorna os inimigos disponíveis para a dificuldade escolhida.
     * Dificuldades mais altas introduzem tipos mais fortes.
     */
    @Override
    public List<String> getInimigosDisponiveis() {
        return switch (dificuldade) {
            case FACIL -> List.of("Normal", "Rapido", "ArqueiroInimigo");                        // 3 tipos básicos
            case MEDIO -> List.of("Normal", "Rapido", "ArqueiroInimigo", "Tanque");              // + Tanque
            case DIFICIL -> List.of("Normal", "Rapido", "ArqueiroInimigo", "Tanque", "Feiticeiro", "Boss"); // Todos
        };
    }

    /** O Modo Praticar não é endless — tem número fixo de waves. */
    @Override
    public boolean isEndless() { return false; }
}
