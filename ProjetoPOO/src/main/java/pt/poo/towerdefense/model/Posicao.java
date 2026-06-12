package pt.poo.towerdefense.model;

import java.util.Objects;

/**
 * Representa uma posição (x, y) na grelha do mapa do jogo.
 * Esta classe é imutável — uma vez criada, as coordenadas não mudam.
 *
 * Conceito POO: Encapsulamento — os atributos x e y são privados e final,
 * acessíveis apenas via getters. A imutabilidade garante que posições
 * podem ser partilhadas com segurança entre objetos (ex: Celula, Torre, Inimigo).
 *
 * Implementa equals() e hashCode() para permitir comparação por valor
 * (duas posições com as mesmas coordenadas são consideradas iguais).
 */
public class Posicao {
    private final int x; // Coordenada horizontal (coluna) na grelha
    private final int y; // Coordenada vertical (linha) na grelha

    /**
     * Cria uma nova posição com as coordenadas especificadas.
     * @param x coordenada horizontal (coluna)
     * @param y coordenada vertical (linha)
     */
    public Posicao(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Retorna a coordenada X (coluna). */
    public int getX() { return x; }

    /** Retorna a coordenada Y (linha). */
    public int getY() { return y; }

    /**
     * Calcula a distância euclidiana até outra posição.
     * Usada para verificar se um inimigo está dentro do alcance de uma torre.
     * Fórmula: √((x₁-x₂)² + (y₁-y₂)²)
     *
     * @param outra a posição de destino
     * @return distância em unidades de célula
     */
    public double distancia(Posicao outra) {
        int dx = this.x - outra.x;
        int dy = this.y - outra.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calcula a distância de Manhattan até outra posição.
     * Alternativa mais rápida à distância euclidiana (sem raiz quadrada).
     * Fórmula: |x₁-x₂| + |y₁-y₂|
     *
     * @param outra a posição de destino
     * @return distância de Manhattan em células
     */
    public int distanciaManhattan(Posicao outra) {
        return Math.abs(this.x - outra.x) + Math.abs(this.y - outra.y);
    }

    /**
     * Compara por valor — duas posições com as mesmas coordenadas são iguais.
     * Override necessário para que coleções como Set e Map funcionem corretamente.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posicao posicao = (Posicao) o;
        return x == posicao.x && y == posicao.y;
    }

    /**
     * Gera hash baseado nas coordenadas — consistente com equals().
     * Contrato: se a.equals(b), então a.hashCode() == b.hashCode().
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /** Representação textual da posição no formato "(x, y)". */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
