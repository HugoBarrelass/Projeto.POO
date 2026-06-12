package pt.poo.towerdefense.logic;

import pt.poo.towerdefense.model.Wave;
import pt.poo.towerdefense.model.inimigo.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Gera waves com crescimento gradual e composição limitada por tipo.
 * Os overloads que recebem Random permitem desafios e testes reproduzíveis.
 *
 * Conceito POO: Classe utilitária — métodos estáticos.
 * Conceito POO: Sobrecarga (Overload) — gerarWaves() e gerarWaveEndless()
 *               têm versões com e sem Random.
 * Conceito POO: Polimorfismo — usa Supplier<Inimigo> para criar tipos diferentes
 *               de inimigos (InimigoNormal, InimigoRapido, etc.) com a mesma interface.
 */
public class GeradorWaves {
    private static final Random DEFAULT_RANDOM = new Random();

    public static List<Wave> gerarWaves(int numeroWaves, List<String> disponiveis, boolean isEndless) {
        return gerarWaves(numeroWaves, disponiveis, isEndless, DEFAULT_RANDOM);
    }

    public static List<Wave> gerarWaves(int numeroWaves, List<String> disponiveis,
                                        boolean isEndless, Random random) {
        List<Wave> waves = new ArrayList<>();
        for (int numero = 1; numero <= numeroWaves; numero++) {
            waves.add(criarWave(numero, numeroWaves, disponiveis, random));
        }
        return waves;
    }

    public static Wave gerarWaveEndless(int numeroWave, List<String> disponiveis) {
        return gerarWaveEndless(numeroWave, disponiveis, DEFAULT_RANDOM);
    }

    /**
     * A fase endless continua a curva da wave 15, acrescentando bosses lentamente.
     */
    public static Wave gerarWaveEndless(int numeroWave, List<String> disponiveis, Random random) {
        int quantidade = Math.min(80 + Math.max(0, numeroWave - 15) * 3, 110);
        int bosses = Math.min(1 + Math.max(0, numeroWave - 16) / 5, 5);
        int tanques = disponiveis.contains("Tanque") ? (int) Math.round((quantidade - bosses) * 0.24) : 0;
        int feiticeiros = disponiveis.contains("Feiticeiro") ? (int) Math.round((quantidade - bosses) * 0.12) : 0;
        int arqueiros = disponiveis.contains("ArqueiroInimigo") ? (int) Math.round((quantidade - bosses) * 0.12) : 0;

        Wave wave = new Wave(numeroWave);
        preencherWave(wave, quantidade, bosses, tanques, feiticeiros, arqueiros, 0.38, random);
        double multiplicadorVida = 1.0 + Math.max(0, numeroWave - 15) * 0.30;
        for (Inimigo inimigo : wave.getInimigos()) {
            inimigo.reforcar(multiplicadorVida);
        }
        return wave;
    }

    private static Wave criarWave(int numeroWave, int totalWaves, List<String> disponiveis, Random random) {
        double progresso = (double) numeroWave / totalWaves;
        int quantidade = calcularQuantidade(numeroWave, totalWaves);

        int bosses = disponiveis.contains("Boss") && progresso >= 0.80
                ? (progresso >= 1.0 ? 2 : 1)
                : 0;
        int semBoss = quantidade - bosses;

        int tanques = 0;
        if (disponiveis.contains("Tanque") && progresso >= 0.35) {
            double escala = (progresso - 0.35) / 0.65;
            tanques = (int) Math.round(semBoss * (0.06 + 0.14 * escala));
        }

        int feiticeiros = 0;
        if (disponiveis.contains("Feiticeiro") && progresso >= 0.45) {
            double escala = (progresso - 0.45) / 0.55;
            feiticeiros = (int) Math.round(semBoss * (0.04 + 0.06 * escala));
        }

        int arqueiros = 0;
        if (disponiveis.contains("ArqueiroInimigo") && numeroWave >= 2) {
            arqueiros = (int) Math.round(semBoss * (0.04 + 0.06 * progresso));
        }

        Wave wave = new Wave(numeroWave);
        double percentagemNormais = 0.58 - progresso * 0.16;
        preencherWave(wave, quantidade, bosses, tanques, feiticeiros, arqueiros,
                percentagemNormais, random);
        return wave;
    }

    private static int calcularQuantidade(int numeroWave, int totalWaves) {
        double escala = 1.0 + Math.max(0, totalWaves - 5) * 0.06;
        return (int) Math.round((4 + numeroWave * 3) * escala);
    }

    private static void preencherWave(Wave wave, int quantidade, int bosses, int tanques,
                                      int feiticeiros, int arqueiros, double probNormal,
                                      Random random) {
        List<Inimigo> inimigos = new ArrayList<>();
        adicionar(inimigos, bosses, InimigoBoss::new);
        adicionar(inimigos, tanques, InimigoTanque::new);
        adicionar(inimigos, feiticeiros, InimigoFeiticeiro::new);
        adicionar(inimigos, arqueiros, InimigoArqueiro::new);

        while (inimigos.size() < quantidade) {
            inimigos.add(random.nextDouble() < probNormal ? new InimigoNormal() : new InimigoRapido());
        }

        Collections.shuffle(inimigos, random);
        for (Inimigo inimigo : inimigos) {
            wave.adicionarInimigo(inimigo);
        }
    }

    private static void adicionar(List<Inimigo> inimigos, int quantidade,
                                  java.util.function.Supplier<Inimigo> fornecedor) {
        for (int i = 0; i < quantidade; i++) {
            inimigos.add(fornecedor.get());
        }
    }
}
