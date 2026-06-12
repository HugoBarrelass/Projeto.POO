package pt.poo.towerdefense.model;

import pt.poo.towerdefense.model.enums.TipoCelula;
import pt.poo.towerdefense.model.torre.Torre;

/**
 * Representa uma célula individual na grelha do mapa.
 * Cada célula tem uma posição, um tipo (LIVRE, CAMINHO, CASTELO ou SPAWN)
 * e pode opcionalmente conter uma torre (herói).
 *
 * Conceito POO: Composição — o Mapa é composto por múltiplas Celulas (relação 1:*).
 * Conceito POO: Agregação — cada Célula pode ter no máximo uma Torre (relação 0..1).
 * Conceito POO: Encapsulamento — atributos privados com acesso controlado via métodos.
 */
public class Celula {
    private final Posicao posicao;            // Coordenadas (x,y) desta célula na grelha
    private TipoCelula tipo;                  // Tipo atual da célula (pode mudar ao gerar caminhos)
    private Torre torre;                      // Torre colocada nesta célula (null se vazia)
    private int indiceCaminho = -1;           // Índice do caminho a que pertence (-1 = sem caminho)

    /**
     * Cria uma nova célula na posição indicada com o tipo especificado.
     * Inicialmente sem torre colocada.
     *
     * @param posicao coordenadas (x,y) na grelha
     * @param tipo tipo inicial da célula
     */
    public Celula(Posicao posicao, TipoCelula tipo) {
        this.posicao = posicao;
        this.tipo = tipo;
        this.torre = null; // Sem torre inicialmente
    }

    // === Getters e Setters (Encapsulamento) ===

    /** Retorna a posição desta célula na grelha. */
    public Posicao getPosicao() { return posicao; }

    /** Retorna o tipo atual da célula. */
    public TipoCelula getTipo() { return tipo; }

    /** Define o tipo da célula (usado pelo GeradorMapa ao criar caminhos). */
    public void setTipo(TipoCelula tipo) { this.tipo = tipo; }

    /** Retorna a torre colocada nesta célula, ou null se não houver. */
    public Torre getTorre() { return torre; }

    /** Retorna o índice do caminho a que esta célula pertence (-1 se não é caminho). */
    public int getIndiceCaminho() { return indiceCaminho; }

    /** Define o índice do caminho (usado pelo Mapa ao adicionar caminhos). */
    public void setIndiceCaminho(int indiceCaminho) { this.indiceCaminho = indiceCaminho; }

    /**
     * Verifica se a célula está livre para colocar um herói.
     * Uma célula está livre se: (1) é do tipo LIVRE e (2) não tem torre colocada.
     *
     * @return true se o jogador pode colocar uma torre aqui
     */
    public boolean isLivre() {
        return tipo == TipoCelula.LIVRE && torre == null;
    }

    /**
     * Coloca uma torre (herói) nesta célula.
     * Só permite colocar se a célula estiver livre.
     * Ao colocar, atualiza a posição da torre para as coordenadas desta célula.
     *
     * @param torre a torre a colocar
     * @return true se a torre foi colocada com sucesso, false se a célula não está livre
     */
    public boolean colocarTorre(Torre torre) {
        if (!isLivre()) return false;       // Não pode colocar se não está livre
        this.torre = torre;                 // Guarda referência para a torre (agregação)
        torre.setPosicao(posicao);          // Informa a torre da sua posição no mapa
        return true;
    }

    /**
     * Remove a torre desta célula (quando vendida ou destruída).
     * Retorna a torre removida para permitir calcular o reembolso.
     *
     * @return a torre removida, ou null se não havia torre
     */
    public Torre removerTorre() {
        Torre removida = this.torre;
        this.torre = null;                  // Liberta a célula
        return removida;
    }

    /**
     * Verifica se esta célula tem uma torre colocada.
     *
     * @return true se há uma torre nesta célula
     */
    public boolean temTorre() {
        return torre != null;
    }
}
