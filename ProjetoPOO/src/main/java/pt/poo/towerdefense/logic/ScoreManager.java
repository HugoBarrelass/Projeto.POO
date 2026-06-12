package pt.poo.towerdefense.logic;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Gere a leitura e escrita de highscores num ficheiro de texto (scores.txt).
 * Formato atual: desafio;nome;wavessobrevividas;pontuacao;tempo;data.
 * Continua a conseguir ler o formato antigo para o ignorar como ranking legado.
 *
 * Conceito POO: Classe utilitária — métodos estáticos para gestão de ficheiros.
 * Conceito POO: Classe interna (Inner Class) — ScoreEntry é definida dentro de ScoreManager
 *               (encapsula a estrutura de um registo de score).
 * Conceito POO: Encapsulamento — o formato do ficheiro é interno,
 *               o acesso é feito via métodos públicos (guardarScore, lerScores).
 */
public class ScoreManager {
    private static final String FICHEIRO_SCORES = "scores.txt";       // Nome do ficheiro onde os scores são guardados
    private static final int MAX_SCORES = 10;                         // Máximo de scores na tabela (Top 10)
    private static final String DESAFIO_ATUAL = "NIVEL-6-ENDLESS";    // Identificador do desafio ativo
    private static final long SEED_DESAFIO_ATUAL = 20260527L;         // Seed fixa para reproduzir o mesmo mapa/waves
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"); // Formato da data

    public static String getDesafioAtual() { return DESAFIO_ATUAL; }
    public static long getSeedDesafioAtual() { return SEED_DESAFIO_ATUAL; }

    /**
     * Representa um registo de score.
     */
    public static class ScoreEntry {
        private final String nome;
        private final int wavesSobrevividas;
        private final int pontuacao;
        private final String tempoJogo; // formato mm:ss
        private final String data;
        private final String desafio;

        public ScoreEntry(String nome, int wavesSobrevividas, int pontuacao, String tempoJogo, String data) {
            this(nome, wavesSobrevividas, pontuacao, tempoJogo, data, DESAFIO_ATUAL);
        }

        public ScoreEntry(String nome, int wavesSobrevividas, int pontuacao, String tempoJogo,
                          String data, String desafio) {
            this.nome = nome;
            this.wavesSobrevividas = wavesSobrevividas;
            this.pontuacao = pontuacao;
            this.tempoJogo = tempoJogo;
            this.data = data;
            this.desafio = desafio;
        }

        public String getNome() { return nome; }
        public int getWavesSobrevividas() { return wavesSobrevividas; }
        public int getPontuacao() { return pontuacao; }
        public String getTempoJogo() { return tempoJogo; }
        public String getData() { return data; }
        public String getDesafio() { return desafio; }

        /**
         * Converte para linha de ficheiro.
         */
        public String toFileLine() {
            return desafio + ";" + nome + ";" + wavesSobrevividas + ";" + pontuacao + ";" + tempoJogo + ";" + data;
        }

        /**
         * Cria um ScoreEntry a partir de uma linha do ficheiro.
         */
        public static ScoreEntry fromFileLine(String line) {
            String[] parts = line.split(";");
            if (parts.length >= 6) {
                return new ScoreEntry(
                        parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]),
                        parts[4],
                        parts[5],
                        parts[0]
                );
            }
            if (parts.length >= 5) {
                return new ScoreEntry(
                        parts[0],
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        parts[3],
                        parts[4],
                        "LEGACY"
                );
            }
            return null;
        }
    }

    /**
     * Guarda um novo score no ficheiro.
     * O score é inserido na posição correta (ordenado por waves e pontuação).
     */
    public static void guardarScore(String nome, int wavesSobrevividas, int pontuacao, long tempoSegundos) {
        String tempoFormatado = formatarTempo(tempoSegundos);
        String data = LocalDateTime.now().format(FORMATO_DATA);

        ScoreEntry novoScore = new ScoreEntry(nome, wavesSobrevividas, pontuacao, tempoFormatado, data);

        List<ScoreEntry> scores = lerScores();
        scores.add(novoScore);

        // Ordenar por waves (desc), depois pontuação (desc)
        scores.sort(Comparator
                .comparingInt(ScoreEntry::getWavesSobrevividas).reversed()
                .thenComparing(Comparator.comparingInt(ScoreEntry::getPontuacao).reversed()));

        // Manter apenas top 10
        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES);
        }

        // Escrever no ficheiro
        escreverScores(scores);
    }

    /**
     * Lê todos os scores do ficheiro.
     * @return lista de scores ordenada
     */
    public static List<ScoreEntry> lerScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        File ficheiro = new File(FICHEIRO_SCORES);

        if (!ficheiro.exists()) return scores;

        try (BufferedReader reader = new BufferedReader(new FileReader(ficheiro))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    ScoreEntry entry = ScoreEntry.fromFileLine(line);
                    if (entry != null && DESAFIO_ATUAL.equals(entry.getDesafio())) {
                        scores.add(entry);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler scores: " + e.getMessage());
        }

        return scores;
    }

    /**
     * Escreve a lista de scores no ficheiro.
     */
    private static void escreverScores(List<ScoreEntry> scores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FICHEIRO_SCORES))) {
            for (ScoreEntry score : scores) {
                writer.write(score.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar scores: " + e.getMessage());
        }
    }

    /**
     * Formata tempo em segundos para mm:ss.
     */
    private static String formatarTempo(long segundos) {
        long minutos = segundos / 60;
        long segs = segundos % 60;
        return String.format("%02d:%02d", minutos, segs);
    }

    /**
     * Verifica se um score entra no top 10.
     */
    public static boolean isHighscore(int wavesSobrevividas, int pontuacao) {
        List<ScoreEntry> scores = lerScores();
        if (scores.size() < MAX_SCORES) return true;
        ScoreEntry ultimo = scores.get(scores.size() - 1);
        return wavesSobrevividas > ultimo.getWavesSobrevividas() ||
               (wavesSobrevividas == ultimo.getWavesSobrevividas() && pontuacao > ultimo.getPontuacao());
    }
}
