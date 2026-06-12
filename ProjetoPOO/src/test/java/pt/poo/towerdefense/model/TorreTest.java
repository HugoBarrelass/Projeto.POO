package pt.poo.towerdefense.model;

import org.junit.jupiter.api.Test;
import pt.poo.towerdefense.model.inimigo.Inimigo;
import pt.poo.towerdefense.model.inimigo.InimigoNormal;
import pt.poo.towerdefense.model.torre.TorreArqueiro;
import pt.poo.towerdefense.model.torre.TorreMago;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TorreTest {

    @Test
    void arqueiroAtacaApenasUmInimigoNoAlcanceERespeitaCooldown() {
        TorreArqueiro arqueiro = new TorreArqueiro();
        arqueiro.setPosicao(new Posicao(0, 0));
        InimigoNormal inimigo = inimigoEm(new Posicao(1, 0));

        List<Inimigo> atingidos = arqueiro.atacar(List.of(inimigo));

        assertEquals(1, atingidos.size());
        assertEquals(110, inimigo.getVida());
        assertTrue(arqueiro.atacar(List.of(inimigo)).isEmpty());
    }

    @Test
    void magoAtingeNoMaximoTresInimigosNoAlcance() {
        TorreMago mago = new TorreMago();
        mago.setPosicao(new Posicao(0, 0));
        List<Inimigo> inimigos = List.of(
                inimigoEm(new Posicao(1, 0)),
                inimigoEm(new Posicao(2, 0)),
                inimigoEm(new Posicao(3, 0)),
                inimigoEm(new Posicao(4, 0))
        );

        List<Inimigo> atingidos = mago.atacar(inimigos);

        assertEquals(3, atingidos.size());
        assertEquals(3, inimigos.stream().filter(i -> i.getVida() == 90).count());
        assertEquals(1, inimigos.stream().filter(i -> i.getVida() == 130).count());
    }

    private InimigoNormal inimigoEm(Posicao posicao) {
        InimigoNormal inimigo = new InimigoNormal();
        inimigo.setCaminho(List.of(posicao, new Posicao(posicao.getX() + 1, posicao.getY())));
        return inimigo;
    }
}
