package pt.poo.towerdefense.model;

import java.util.List;

/**
 * Modo História — Gere a progressão dos 6 níveis e o desbloqueio de personagens.
 * O jogador completa um nível para desbloquear o seguinte.
 *
 * Conceito POO: Herança — herda de ModoJogo (classe abstrata) e implementa
 * todos os métodos abstratos. Cada método delega para o Nivel atual.
 *
 * Conceito POO: Composição — contém 6 Niveis (relação 1:*). Os Niveis
 * pertencem exclusivamente a este ModoHistoria.
 *
 * Conceito POO: Polimorfismo — os métodos como getTamanhoMapa() retornam
 * valores diferentes dependendo do nível selecionado.
 */
public class ModoHistoria extends ModoJogo {
    private final List<Nivel> niveis;   // Lista dos 6 níveis do modo história (composição)
    private Nivel nivelAtual;           // Referência para o nível atualmente selecionado

    /**
     * Construtor — inicializa os 6 níveis e seleciona o primeiro por defeito.
     * Chama super("Modo História") para definir o nome no ModoJogo (herança).
     */
    public ModoHistoria() {
        super("Modo História");                     // Chama construtor da superclasse
        this.niveis = Nivel.criarNiveisHistoria();  // Factory method que cria os 6 níveis
        this.nivelAtual = niveis.get(0);            // Começa no nível 1
    }

    public List<Nivel> getNiveis() { return niveis; }
    public Nivel getNivelAtual() { return nivelAtual; }

    /**
     * Seleciona um nível para jogar.
     * @return true se o nível está desbloqueado
     */
    public boolean selecionarNivel(int numero) {
        if (numero < 1 || numero > niveis.size()) return false;
        Nivel nivel = niveis.get(numero - 1);
        if (!nivel.isDesbloqueado()) return false;
        this.nivelAtual = nivel;
        return true;
    }

    /**
     * Completa o nível atual e desbloqueia o próximo.
     */
    public void completarNivelAtual() {
        nivelAtual.completar();
        int proximo = nivelAtual.getNumero();
        if (proximo < niveis.size()) {
            niveis.get(proximo).desbloquear();
        }
    }

    // === Implementação dos métodos abstratos de ModoJogo (polimorfismo) ===
    // Cada método delega para o nível atualmente selecionado.

    @Override
    public String getDescricao() {
        return "Progride por 6 níveis com dificuldade crescente. Desbloqueia novos heróis e inimigos!";
    }

    @Override
    public int getTamanhoMapa() { return nivelAtual.getTamanhoMapa(); }

    @Override
    public int getNumeroCaminhos() { return nivelAtual.getNumeroCaminhos(); }

    @Override
    public int getNumeroWaves() { return nivelAtual.getNumeroWaves(); }

    @Override
    public int getOuroInicial() { return nivelAtual.getOuroInicial(); }

    @Override
    public int getHpCastelo() { return nivelAtual.getHpCastelo(); }

    @Override
    public List<String> getHeroisDisponiveis() { return nivelAtual.getHeroisDisponiveis(); }

    @Override
    public List<String> getInimigosDisponiveis() { return nivelAtual.getInimigosDisponiveis(); }

    @Override
    public boolean isEndless() { return nivelAtual.isEndless(); }
}
