package pt.poo.towerdefense.model;

import pt.poo.towerdefense.model.enums.TipoCelula;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa o mapa do jogo composto por uma grelha de células NxN.
 * Contém os caminhos predefinidos por onde os inimigos avançam,
 * as posições de spawn e a posição do castelo.
 *
 * Conceito POO: Composição — o Mapa é composto por múltiplas Celulas (relação 1:*).
 * Se o Mapa for destruído, as Celulas deixam de fazer sentido.
 *
 * Conceito POO: Encapsulamento — a grelha interna é privada.
 * O acesso às células é feito via getCelula(x,y), que valida os limites.
 */
public class Mapa {
    private final int largura;                          // Número de colunas da grelha
    private final int altura;                           // Número de linhas da grelha
    private final Celula[][] grelha;                    // Matriz 2D de células (composição)
    private final List<List<Posicao>> caminhos;         // Lista de caminhos (cada caminho é uma lista de posições)
    private Posicao posicaoCastelo;                     // Posição de referência do castelo

    /**
     * Cria um mapa com a largura e altura especificadas.
     * Todas as células são inicializadas como LIVRE.
     *
     * @param largura número de colunas
     * @param altura número de linhas
     */
    public Mapa(int largura, int altura) {
        this.largura = largura;
        this.altura = altura;
        this.grelha = new Celula[altura][largura];
        this.caminhos = new ArrayList<>();

        // Inicializar todas as células como LIVRE (composição — cria os objetos internos)
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                grelha[y][x] = new Celula(new Posicao(x, y), TipoCelula.LIVRE);
            }
        }
    }

    // === Getters ===

    /** Retorna a largura (número de colunas) do mapa. */
    public int getLargura() { return largura; }

    /** Retorna a altura (número de linhas) do mapa. */
    public int getAltura() { return altura; }

    /** Retorna a lista de caminhos definidos no mapa. */
    public List<List<Posicao>> getCaminhos() { return caminhos; }

    /** Retorna a posição de referência do castelo. */
    public Posicao getPosicaoCastelo() { return posicaoCastelo; }

    /**
     * Obtém a célula nas coordenadas (x,y).
     * Valida os limites para evitar ArrayIndexOutOfBoundsException.
     *
     * @param x coluna (0 a largura-1)
     * @param y linha (0 a altura-1)
     * @return a célula, ou null se as coordenadas forem inválidas
     */
    public Celula getCelula(int x, int y) {
        if (x < 0 || x >= largura || y < 0 || y >= altura) return null;
        return grelha[y][x];    // Nota: grelha[linha][coluna] = grelha[y][x]
    }

    /**
     * Obtém a célula numa posição (sobrecarga do método getCelula).
     *
     * @param pos posição com coordenadas x,y
     * @return a célula nessa posição
     */
    public Celula getCelula(Posicao pos) {
        return getCelula(pos.getX(), pos.getY());
    }

    /**
     * Adiciona um caminho ao mapa e marca as células correspondentes como CAMINHO.
     * Cada caminho é uma lista ordenada de posições, do spawn ao castelo.
     *
     * @param caminho lista de posições que formam o caminho
     */
    public void adicionarCaminho(List<Posicao> caminho) {
        int indice = caminhos.size();   // Índice deste caminho (0 para o primeiro, 1 para o segundo)
        caminhos.add(caminho);
        // Marcar cada célula do caminho com o tipo CAMINHO e o índice
        for (Posicao pos : caminho) {
            Celula celula = getCelula(pos);
            if (celula != null) {
                celula.setTipo(TipoCelula.CAMINHO);
                celula.setIndiceCaminho(indice);    // Permite distinguir visualmente os caminhos
            }
        }
    }

    /**
     * Define a posição de referência do castelo (off-screen à direita).
     * O castelo não ocupa uma célula no mapa — é apenas um ponto de referência.
     *
     * @param posicao posição do castelo
     */
    public void setPosicaoCastelo(Posicao posicao) {
        this.posicaoCastelo = posicao;
    }

    /**
     * Define uma posição de spawn no mapa (ponto de entrada dos inimigos).
     * Marca a célula como SPAWN para visualização diferenciada.
     *
     * @param posicao posição do spawn
     */
    public void setSpawn(Posicao posicao) {
        Celula celula = getCelula(posicao);
        if (celula != null) {
            celula.setTipo(TipoCelula.SPAWN);
        }
    }

    /**
     * Retorna todas as torres (heróis) colocadas no mapa.
     * Percorre toda a grelha e recolhe as torres existentes.
     *
     * @return lista de todas as torres ativas no mapa
     */
    public List<pt.poo.towerdefense.model.torre.Torre> getTodasTorres() {
        List<pt.poo.towerdefense.model.torre.Torre> torres = new ArrayList<>();
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                if (grelha[y][x].temTorre()) {
                    torres.add(grelha[y][x].getTorre());
                }
            }
        }
        return torres;
    }

    /**
     * Conta quantas torres de um tipo específico existem no mapa.
     * Usado para limitar o Campeão a máximo 2 no mapa (regra de negócio).
     * Usa isInstance() para verificar o tipo em runtime (polimorfismo).
     *
     * @param tipoTorre a classe da torre a contar (ex: TorreCampeao.class)
     * @return número de torres desse tipo no mapa
     */
    public int contarTorresDoTipo(Class<?> tipoTorre) {
        int count = 0;
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                // isInstance() verifica se o objeto é da classe ou subclasse (polimorfismo)
                if (grelha[y][x].temTorre() && tipoTorre.isInstance(grelha[y][x].getTorre())) {
                    count++;
                }
            }
        }
        return count;
    }
}
