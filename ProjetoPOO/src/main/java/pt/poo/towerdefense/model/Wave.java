package pt.poo.towerdefense.model;

import pt.poo.towerdefense.model.inimigo.Inimigo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma onda (wave) de inimigos no jogo.
 * Cada wave contém uma lista de inimigos que são lançados em sequência.
 * O jogador deve eliminar todos os inimigos da wave para avançar.
 *
 * Conceito POO: Composição — cada Wave contém múltiplos Inimigos (relação 1:*).
 * Os inimigos são criados especificamente para esta wave pelo GeradorWaves.
 */
public class Wave {
    private final int numero;               // Número da wave (1, 2, 3, ...)
    private final List<Inimigo> inimigos;   // Lista de inimigos desta wave (composição)
    private boolean iniciada;               // true depois de iniciar()
    private boolean concluida;              // true quando todos os inimigos morreram ou chegaram ao castelo

    /**
     * Cria uma nova wave com o número especificado.
     * Inicialmente sem inimigos — são adicionados pelo GeradorWaves.
     *
     * @param numero número sequencial da wave
     */
    public Wave(int numero) {
        this.numero = numero;
        this.inimigos = new ArrayList<>();
        this.iniciada = false;
        this.concluida = false;
    }

    // === Getters ===

    /** Retorna o número da wave. */
    public int getNumero() { return numero; }

    /** Retorna a lista completa de inimigos desta wave. */
    public List<Inimigo> getInimigos() { return inimigos; }

    /** Verifica se a wave já foi iniciada. */
    public boolean isIniciada() { return iniciada; }

    /** Verifica se a wave já foi concluída (todos inimigos mortos ou no castelo). */
    public boolean isConcluida() { return concluida; }

    /**
     * Adiciona um inimigo a esta wave (chamado pelo GeradorWaves durante a criação).
     *
     * @param inimigo o inimigo a adicionar
     */
    public void adicionarInimigo(Inimigo inimigo) {
        inimigos.add(inimigo);
    }

    /**
     * Marca a wave como iniciada.
     * Depois disto, os inimigos começam a ser spawned no mapa.
     */
    public void iniciar() {
        this.iniciada = true;
    }

    /**
     * Retorna os inimigos que ainda estão vivos e não chegaram ao castelo.
     * Usa Streams com method references (Inimigo::isVivo) e lambdas.
     *
     * @return lista de inimigos ainda ativos
     */
    public List<Inimigo> getInimigosAtivos() {
        return inimigos.stream()
                .filter(Inimigo::isVivo)                    // Method reference — filtra vivos
                .filter(i -> !i.isChegouAoCastelo())        // Lambda — exclui os que chegaram ao castelo
                .toList();
    }

    /**
     * Verifica se a wave foi concluída (todos os inimigos foram eliminados
     * ou chegaram ao castelo). Chamado pelo Jogo a cada frame.
     */
    public void verificarConclusao() {
        if (iniciada && getInimigosAtivos().isEmpty()) {
            concluida = true;       // Não há mais inimigos ativos — wave completa
        }
    }

    /**
     * Retorna o número total de inimigos nesta wave.
     *
     * @return total de inimigos (incluindo já eliminados)
     */
    public int getTotalInimigos() {
        return inimigos.size();
    }

    /**
     * Retorna quantos inimigos já foram eliminados nesta wave.
     * Usa Stream com filter para contar os que já não estão vivos.
     *
     * @return número de inimigos eliminados
     */
    public int getInimigosEliminados() {
        return (int) inimigos.stream().filter(i -> !i.isVivo()).count();
    }
}
