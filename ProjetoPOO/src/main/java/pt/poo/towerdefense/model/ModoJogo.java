package pt.poo.towerdefense.model;

import java.util.List;

/**
 * Classe abstrata que define a interface comum dos modos de jogo.
 * ModoHistoria e ModoPraticar herdam desta classe (herança).
 *
 * Conceito POO: Abstração — define o "contrato" que todos os modos devem cumprir,
 * sem fornecer implementação. Cada método abstrato obriga as subclasses a implementar
 * a sua versão específica (polimorfismo).
 *
 * Conceito POO: Herança — ModoHistoria e ModoPraticar herdam de ModoJogo,
 * reutilizando o atributo 'nome' e o seu getter.
 *
 * Relação: O Jogo tem um ModoJogo ativo (associação). Em runtime, pode ser
 * ModoHistoria ou ModoPraticar (polimorfismo de subtipos).
 */
public abstract class ModoJogo {
    private final String nome;  // Nome legível do modo ("Modo História" ou "Modo Praticar")

    /**
     * Construtor protegido — só pode ser chamado pelas subclasses.
     * @param nome nome legível do modo de jogo
     */
    protected ModoJogo(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }

    /**
     * Retorna a descrição do modo de jogo.
     */
    public abstract String getDescricao();

    /**
     * Retorna o tamanho do mapa para a configuração atual.
     */
    public abstract int getTamanhoMapa();

    /**
     * Retorna o número de caminhos.
     */
    public abstract int getNumeroCaminhos();

    /**
     * Retorna o número de waves.
     */
    public abstract int getNumeroWaves();

    /**
     * Retorna o ouro inicial.
     */
    public abstract int getOuroInicial();

    /**
     * Retorna o HP do castelo.
     */
    public abstract int getHpCastelo();

    /**
     * Retorna os heróis disponíveis.
     */
    public abstract List<String> getHeroisDisponiveis();

    /**
     * Retorna os inimigos disponíveis.
     */
    public abstract List<String> getInimigosDisponiveis();

    /**
     * Verifica se o modo é endless (waves infinitas).
     */
    public abstract boolean isEndless();
}
