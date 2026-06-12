package pt.poo.towerdefense.logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreManagerTest {

    private static final Path SCORES = Path.of("scores.txt");

    @BeforeEach
    @AfterEach
    void limparScores() throws IOException {
        Files.deleteIfExists(SCORES);
    }

    @Test
    void guardarScoreMantemTop10OrdenadoPorWavesEDepoisPontuacao() {
        for (int i = 0; i < 12; i++) {
            ScoreManager.guardarScore("Jogador" + i, i, i * 100, 60);
        }
        ScoreManager.guardarScore("EmpateMelhor", 11, 9999, 60);

        List<ScoreManager.ScoreEntry> scores = ScoreManager.lerScores();

        assertEquals(10, scores.size());
        assertEquals("EmpateMelhor", scores.get(0).getNome());
        assertEquals(11, scores.get(0).getWavesSobrevividas());
        assertTrue(scores.get(0).getPontuacao() > scores.get(1).getPontuacao());
    }

    @Test
    void ficheiroDeScoresUsaIdentificadorAtual() throws IOException {
        ScoreManager.guardarScore("Teste", 5, 500, 90);

        String conteudo = Files.readString(SCORES);

        assertTrue(conteudo.startsWith("NIVEL-6-ENDLESS;"));
    }
}
