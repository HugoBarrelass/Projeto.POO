package pt.poo.towerdefense.model;

import org.junit.jupiter.api.Test;
import pt.poo.towerdefense.model.inimigo.InimigoNormal;

import static org.junit.jupiter.api.Assertions.*;

class WaveTest {

    @Test
    void waveSoFicaConcluidaDepoisDeIniciadaESemInimigosAtivos() {
        Wave wave = new Wave(1);
        InimigoNormal inimigo = new InimigoNormal();
        wave.adicionarInimigo(inimigo);

        wave.verificarConclusao();
        assertFalse(wave.isConcluida());

        wave.iniciar();
        assertEquals(1, wave.getInimigosAtivos().size());

        inimigo.receberDano(999);
        wave.verificarConclusao();

        assertTrue(wave.isConcluida());
        assertEquals(1, wave.getInimigosEliminados());
    }
}
