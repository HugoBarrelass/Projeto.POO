package pt.poo.towerdefense.model;

import pt.poo.towerdefense.model.enums.Dificuldade;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um nível do modo história com configuração de mapa, waves e personagens disponíveis.
 */
public class Nivel {
    private final int numero;                       // Número do nível (1 a 6)
    private final int tamanhoMapa;                   // Dimensão NxN da grelha
    private final int numeroCaminhos;                // Quantos caminhos os inimigos percorrem
    private final int numeroWaves;                   // Total de waves a sobreviver
    private final int ouroInicial;                   // Ouro com que o jogador começa
    private final int hpCastelo;                     // HP inicial do castelo
    private final List<String> heroisDisponiveis;    // Tipos de heróis que o jogador pode usar
    private final List<String> inimigosDisponiveis;  // Tipos de inimigos que aparecem
    private boolean desbloqueado;                    // true se o jogador pode jogar este nível
    private boolean completado;                      // true se o jogador já completou este nível
    private final boolean isEndless;                 // Nível 6 = modo endless (waves infinitas após as 15 base)

    /**
     * Construtor — cria um nível com toda a sua configuração.
     * O nível 1 começa desbloqueado; os restantes são desbloqueados ao completar o anterior.
     */
    public Nivel(int numero, int tamanhoMapa, int numeroCaminhos, int numeroWaves,
                 int ouroInicial, int hpCastelo, List<String> herois, List<String> inimigos,
                 boolean isEndless) {
        this.numero = numero;
        this.tamanhoMapa = tamanhoMapa;
        this.numeroCaminhos = numeroCaminhos;
        this.numeroWaves = numeroWaves;
        this.ouroInicial = ouroInicial;
        this.hpCastelo = hpCastelo;
        this.heroisDisponiveis = new ArrayList<>(herois);       // Cópia defensiva da lista
        this.inimigosDisponiveis = new ArrayList<>(inimigos);   // Cópia defensiva da lista
        this.desbloqueado = (numero == 1);  // Só o nível 1 começa desbloqueado
        this.completado = false;
        this.isEndless = isEndless;
    }

    // Getters
    public int getNumero() { return numero; }
    public int getTamanhoMapa() { return tamanhoMapa; }
    public int getNumeroCaminhos() { return numeroCaminhos; }
    public int getNumeroWaves() { return numeroWaves; }
    public int getOuroInicial() { return ouroInicial; }
    public int getHpCastelo() { return hpCastelo; }
    public List<String> getHeroisDisponiveis() { return heroisDisponiveis; }
    public List<String> getInimigosDisponiveis() { return inimigosDisponiveis; }
    public boolean isDesbloqueado() { return desbloqueado; }
    public boolean isCompletado() { return completado; }
    public boolean isEndless() { return isEndless; }

    public void desbloquear() { this.desbloqueado = true; }
    public void completar() { this.completado = true; }

    /**
     * Factory method estático — cria e retorna os 6 níveis do Modo História.
     * Cada nível introduz progressivamente novos heróis, inimigos e mapas maiores.
     * Os parâmetros seguem a especificação da Fase 1 do projeto.
     *
     * @return lista com os 6 níveis configurados
     */
    public static List<Nivel> criarNiveisHistoria() {
        List<Nivel> niveis = new ArrayList<>();

        // Nivel 1 - introducao acessivel a mecanica base.
        niveis.add(new Nivel(1, 8, 1, 5, 180, 20,
                List.of("Arqueiro", "Mago", "TorreGelo"),
                List.of("Normal", "Rapido", "ArqueiroInimigo"),
                false));

        // Nivel 2 - introduz inimigos blindados.
        niveis.add(new Nivel(2, 8, 1, 5, 185, 20,
                List.of("Arqueiro", "Mago", "TorreGelo"),
                List.of("Normal", "Rapido", "ArqueiroInimigo", "Tanque"),
                false));

        // Nivel 3 - dois caminhos e acesso ao canhao.
        niveis.add(new Nivel(3, 10, 2, 8, 210, 20,
                List.of("Arqueiro", "Mago", "TorreGelo", "Canhao"),
                List.of("Normal", "Rapido", "ArqueiroInimigo", "Tanque"),
                false));

        // Nivel 4 - introduz inimigos de controlo.
        niveis.add(new Nivel(4, 10, 2, 8, 220, 18,
                List.of("Arqueiro", "Mago", "TorreGelo", "Canhao"),
                List.of("Normal", "Rapido", "ArqueiroInimigo", "Tanque", "Feiticeiro"),
                false));

        // Nivel 5 - introduz boss e veneno.
        niveis.add(new Nivel(5, 12, 2, 10, 230, 15,
                List.of("Arqueiro", "Mago", "TorreGelo", "Canhao", "TorreVeneno"),
                List.of("Normal", "Rapido", "ArqueiroInimigo", "Tanque", "Feiticeiro", "Boss"),
                false));

        // Nivel 6 - desafio classificado infinito apos as waves base.
        niveis.add(new Nivel(6, 12, 2, 15, 250, 15,
                List.of("Arqueiro", "Mago", "TorreGelo", "Canhao", "TorreVeneno", "Campeao"),
                List.of("Normal", "Rapido", "ArqueiroInimigo", "Tanque", "Feiticeiro", "Boss"),
                true));

        return niveis;
    }
}
