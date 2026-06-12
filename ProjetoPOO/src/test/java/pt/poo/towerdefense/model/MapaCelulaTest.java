package pt.poo.towerdefense.model;

import org.junit.jupiter.api.Test;
import pt.poo.towerdefense.model.enums.TipoCelula;
import pt.poo.towerdefense.model.torre.TorreArqueiro;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapaCelulaTest {

    @Test
    void getCelulaRetornaNullParaCoordenadasForaDoMapa() {
        Mapa mapa = new Mapa(4, 4);

        assertNull(mapa.getCelula(-1, 0));
        assertNull(mapa.getCelula(4, 0));
        assertNull(mapa.getCelula(0, 4));
        assertNotNull(mapa.getCelula(0, 0));
    }

    @Test
    void adicionarCaminhoMarcaCelulasComIndiceDoCaminho() {
        Mapa mapa = new Mapa(5, 5);
        List<Posicao> caminho = List.of(new Posicao(0, 1), new Posicao(1, 1), new Posicao(2, 1));

        mapa.adicionarCaminho(caminho);

        assertEquals(TipoCelula.CAMINHO, mapa.getCelula(1, 1).getTipo());
        assertEquals(0, mapa.getCelula(1, 1).getIndiceCaminho());
        assertEquals(1, mapa.getCaminhos().size());
    }

    @Test
    void celulaLivreAceitaTorreECelulaDeCaminhoRecusa() {
        Mapa mapa = new Mapa(5, 5);
        TorreArqueiro arqueiro = new TorreArqueiro();

        assertTrue(mapa.getCelula(2, 2).colocarTorre(arqueiro));
        assertEquals(new Posicao(2, 2), arqueiro.getPosicao());
        assertEquals(1, mapa.getTodasTorres().size());

        mapa.adicionarCaminho(List.of(new Posicao(0, 1), new Posicao(1, 1)));
        assertFalse(mapa.getCelula(1, 1).colocarTorre(new TorreArqueiro()));
    }
}
