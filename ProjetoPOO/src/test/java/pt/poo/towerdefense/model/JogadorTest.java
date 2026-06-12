package pt.poo.towerdefense.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JogadorTest {

    @Test
    void gastarOuroSoDeduzQuandoHaSaldoSuficiente() {
        Jogador jogador = new Jogador("Teste", 100, 10);

        assertTrue(jogador.gastarOuro(65));
        assertEquals(35, jogador.getOuro());

        assertFalse(jogador.gastarOuro(50));
        assertEquals(35, jogador.getOuro());
    }

    @Test
    void receberDanoNuncaDeixaHpNegativo() {
        Jogador jogador = new Jogador("Teste", 100, 10);

        jogador.receberDano(4);
        assertEquals(6, jogador.getHpCastelo());
        assertTrue(jogador.isVivo());

        jogador.receberDano(50);
        assertEquals(0, jogador.getHpCastelo());
        assertFalse(jogador.isVivo());
    }

    @Test
    void calculaPontuacaoFinalComPontuacaoHpEOuroRestante() {
        Jogador jogador = new Jogador("Teste", 120, 8);

        jogador.adicionarPontuacao(300);
        jogador.gastarOuro(20);

        assertEquals(800, jogador.calcularPontuacaoFinal());
    }
}
