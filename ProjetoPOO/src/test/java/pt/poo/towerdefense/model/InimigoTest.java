package pt.poo.towerdefense.model;

import org.junit.jupiter.api.Test;
import pt.poo.towerdefense.model.inimigo.InimigoNormal;
import pt.poo.towerdefense.model.inimigo.InimigoTanque;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InimigoTest {

    @Test
    void inimigoMoveAoLongoDoCaminhoEAtingeCastelo() {
        InimigoNormal inimigo = new InimigoNormal();
        inimigo.setCaminho(List.of(new Posicao(0, 0), new Posicao(1, 0), new Posicao(2, 0)));

        inimigo.mover(0.5);
        assertEquals(0, inimigo.getIndiceCaminho());
        assertEquals(0.5, inimigo.getProgressoEntreCelulas(), 0.0001);
        assertFalse(inimigo.isChegouAoCastelo());

        inimigo.mover(3.0);
        assertTrue(inimigo.isChegouAoCastelo());
    }

    @Test
    void danoPodeEliminarInimigoMasNuncaDeixaVidaNegativa() {
        InimigoNormal inimigo = new InimigoNormal();

        inimigo.receberDano(200);

        assertEquals(0, inimigo.getVida());
        assertFalse(inimigo.isVivo());
    }

    @Test
    void tanqueReduzDanoComArmadura() {
        InimigoTanque tanque = new InimigoTanque();

        tanque.receberDano(100);

        assertEquals(320, tanque.getVida());
    }

    @Test
    void venenoAplicaDanoAoLongoDoTempo() {
        InimigoNormal inimigo = new InimigoNormal();

        inimigo.aplicarVeneno(10, 2.0);
        inimigo.atualizarEfeitos(1.0);

        assertEquals(120, inimigo.getVida());
        assertTrue(inimigo.isEnvenenado());
    }
}
