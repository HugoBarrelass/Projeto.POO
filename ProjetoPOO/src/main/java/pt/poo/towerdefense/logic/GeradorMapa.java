package pt.poo.towerdefense.logic;

import pt.poo.towerdefense.model.Mapa;
import pt.poo.towerdefense.model.Posicao;

import java.util.*;

/**
 * Gera mapas com caminhos ALEATÓRIOS da esquerda (spawn) para a direita (castelo).
 * Cada vez que se joga, o mapa é diferente (usa Random para aleatoriedade).
 * Quando há 2 caminhos: cruzam-se em exatamente 1 célula,
 * mantêm espaçamento mínimo entre si (nunca lado a lado).
 *
 * Conceito POO: Classe utilitária — métodos estáticos (não precisa de instanciação).
 * Conceito POO: Delegação — o Jogo delega a geração do mapa a esta classe.
 * Conceito POO: Sobrecarga (Overload) — gerarMapa() tem 2 versões
 *               (com e sem Random para testes).
 */
public class GeradorMapa {
    private static final Random DEFAULT_RANDOM = new Random();
    private static final int MAX_TENTATIVAS = 200;

    /**
     * Gera um mapa com o tamanho e número de caminhos especificados.
     */
    public static Mapa gerarMapa(int tamanho, int numeroCaminhos) {
        return gerarMapa(tamanho, numeroCaminhos, DEFAULT_RANDOM);
    }

    public static Mapa gerarMapa(int tamanho, int numeroCaminhos, Random random) {
        Mapa mapa = new Mapa(tamanho, tamanho);

        if (numeroCaminhos <= 1) {
            gerarCaminhoUnico(mapa, tamanho, random);
        } else {
            gerarDoisCaminhos(mapa, tamanho, random);
        }

        return mapa;
    }

    // ==================== CAMINHO ÚNICO (serpentina aleatória) ====================

    private static void gerarCaminhoUnico(Mapa mapa, int tamanho, Random random) {
        int lastCol = tamanho - 1;
        int spawnY = 1 + random.nextInt(tamanho - 2);
        int endY = 1 + random.nextInt(tamanho - 2);
        while (Math.abs(endY - spawnY) < 2) endY = 1 + random.nextInt(tamanho - 2);

        // Gerar 1 waypoint intermédio para criar curva em S
        int midX = 2 + random.nextInt(Math.max(1, tamanho / 2 - 1));
        int midY = 1 + random.nextInt(tamanho - 2);
        while (Math.abs(midY - spawnY) < 2) midY = 1 + random.nextInt(tamanho - 2);

        int turnX = midX + 2 + random.nextInt(Math.max(1, lastCol - midX - 3));
        turnX = Math.min(turnX, lastCol - 1);

        List<Posicao> caminho = new ArrayList<>();
        adicionarH(caminho, 0, midX, spawnY);
        adicionarV(caminho, midX, spawnY, midY);
        adicionarH(caminho, midX, turnX, midY);
        adicionarV(caminho, turnX, midY, endY);
        adicionarH(caminho, turnX, lastCol, endY);

        caminho = removerDuplicados(caminho);
        mapa.adicionarCaminho(caminho);
        mapa.setSpawn(caminho.get(0));
    }

    // ==================== DOIS CAMINHOS COM CRUZAMENTO ====================

    private static void gerarDoisCaminhos(Mapa mapa, int tamanho, Random random) {
        int lastCol = tamanho - 1;

        for (int t = 0; t < MAX_TENTATIVAS; t++) {
            // Cruzamento na zona central
            int cx = tamanho / 3 + random.nextInt(Math.max(1, tamanho / 3));
            int cy = tamanho / 3 + random.nextInt(Math.max(1, tamanho / 3));

            // Path 1: spawna em cima, acaba em baixo (cruza horizontalmente)
            int s1 = 1 + random.nextInt(Math.max(1, cy - 2));
            int e1 = Math.min(lastCol - 1, cy + 2 + random.nextInt(Math.max(1, tamanho - cy - 4)));
            // Path 2: spawna em baixo, acaba em cima (cruza verticalmente)
            int s2 = Math.min(lastCol - 1, cy + 2 + random.nextInt(Math.max(1, tamanho - cy - 4)));
            int e2 = 1 + random.nextInt(Math.max(1, cy - 2));

            if (Math.abs(s1 - s2) < 3 || Math.abs(e1 - e2) < 3) continue;
            if (s1 >= cy - 1 || e1 <= cy + 1 || s2 <= cy + 1 || e2 >= cy - 1) continue;

            // Caminho 1: horizontal pelo cruzamento
            int t1 = 1 + random.nextInt(Math.max(1, cx - 2));
            int t2 = Math.min(lastCol - 1, cx + 2 + random.nextInt(Math.max(1, lastCol - cx - 3)));
            List<Posicao> c1 = new ArrayList<>();
            adicionarH(c1, 0, t1, s1);
            adicionarV(c1, t1, s1, cy);
            adicionarH(c1, t1, t2, cy); // passa pelo cruzamento (cx,cy)
            adicionarV(c1, t2, cy, e1);
            adicionarH(c1, t2, lastCol, e1);

            // Verificar que o cruzamento está realmente no caminho 1
            if (t1 > cx || t2 < cx) continue;

            // Caminho 2: vertical pelo cruzamento
            int yAbove = Math.max(1, cy - 2 - random.nextInt(Math.max(1, cy - e2 - 2)));
            yAbove = Math.max(yAbove, e2);
            int yBelow = Math.min(lastCol - 1, cy + 2 + random.nextInt(Math.max(1, s2 - cy - 2)));
            yBelow = Math.min(yBelow, s2);

            int arrX = 1 + random.nextInt(Math.max(1, cx - 1));
            int exitX = Math.min(lastCol - 1, cx + 2 + random.nextInt(Math.max(1, lastCol - cx - 3)));

            List<Posicao> c2 = new ArrayList<>();
            adicionarH(c2, 0, arrX, s2);
            adicionarV(c2, arrX, s2, yBelow);
            adicionarH(c2, arrX, cx, yBelow);
            adicionarV(c2, cx, yBelow, yAbove); // passa pelo cruzamento (cx,cy) verticalmente
            adicionarH(c2, cx, exitX, yAbove);
            adicionarV(c2, exitX, yAbove, e2);
            adicionarH(c2, exitX, lastCol, e2);

            // Verificar que cruzamento está no caminho 2 vertical
            if (yBelow < cy || yAbove > cy) continue;

            c1 = removerDuplicados(c1);
            c2 = removerDuplicados(c2);

            // Validar espaçamento
            if (validarEspacamento(c1, c2, new Posicao(cx, cy))
                    && validarDentroDosLimites(c1, tamanho)
                    && validarDentroDosLimites(c2, tamanho)) {
                mapa.adicionarCaminho(c1);
                mapa.setSpawn(c1.get(0));
                mapa.adicionarCaminho(c2);
                mapa.setSpawn(c2.get(0));
                return;
            }
        }

        // Fallback: layout estático garantido
        gerarFallback(mapa, tamanho);
    }

    // ==================== FALLBACK (layouts estáticos testados) ====================

    private static void gerarFallback(Mapa mapa, int tamanho) {
        int lastCol = tamanho - 1;
        int cy = tamanho / 2;

        // Caminho 1: horizontal pelo cruzamento
        List<Posicao> c1 = new ArrayList<>();
        adicionarH(c1, 0, 3, 1);
        adicionarV(c1, 3, 1, cy);
        adicionarH(c1, 3, lastCol - 2, cy);
        adicionarV(c1, lastCol - 2, cy, lastCol - 2);
        adicionarH(c1, lastCol - 2, lastCol, lastCol - 2);
        c1 = removerDuplicados(c1);
        mapa.adicionarCaminho(c1);
        mapa.setSpawn(c1.get(0));

        int cx = tamanho / 2;
        // Caminho 2: vertical pelo cruzamento
        List<Posicao> c2 = new ArrayList<>();
        adicionarH(c2, 0, 1, lastCol - 2);
        adicionarV(c2, 1, lastCol - 2, cy + 2);
        adicionarH(c2, 1, cx, cy + 2);
        adicionarV(c2, cx, cy + 2, cy - 2);
        adicionarH(c2, cx, lastCol - 1, cy - 2);
        adicionarV(c2, lastCol - 1, cy - 2, 2);
        adicionarH(c2, lastCol - 1, lastCol, 2);
        c2 = removerDuplicados(c2);
        mapa.adicionarCaminho(c2);
        mapa.setSpawn(c2.get(0));
    }

    // ==================== VALIDAÇÃO ====================

    /**
     * Verifica que nenhuma célula de c1 é adjacente (4-connected) a uma célula de c2,
     * exceto na posição de cruzamento.
     */
    private static boolean validarEspacamento(List<Posicao> c1, List<Posicao> c2, Posicao crossing) {
        Set<Long> setC2 = new HashSet<>();
        for (Posicao p : c2) setC2.add(chave(p.getX(), p.getY()));

        for (Posicao p : c1) {
            if (p.equals(crossing)) continue;
            int x = p.getX(), y = p.getY();
            // Verificar 4 vizinhos
            for (int[] d : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                long vizinho = chave(x + d[0], y + d[1]);
                if (setC2.contains(vizinho)) {
                    Posicao vizPos = new Posicao(x + d[0], y + d[1]);
                    if (!vizPos.equals(crossing)) return false;
                }
            }
        }

        // Verificar sobreposição (só podem partilhar o cruzamento)
        Set<Long> setC1 = new HashSet<>();
        for (Posicao p : c1) setC1.add(chave(p.getX(), p.getY()));
        for (Posicao p : c2) {
            if (p.equals(crossing)) continue;
            if (setC1.contains(chave(p.getX(), p.getY()))) return false;
        }

        return true;
    }

    private static boolean validarDentroDosLimites(List<Posicao> caminho, int tamanho) {
        for (Posicao p : caminho) {
            if (p.getX() < 0 || p.getX() >= tamanho || p.getY() < 0 || p.getY() >= tamanho) {
                return false;
            }
        }
        return true;
    }

    private static long chave(int x, int y) {
        return (long) x * 10000 + y;
    }

    // ==================== SEGMENTOS ====================

    /** Adiciona segmento horizontal de x1 até x2 na linha y. */
    private static void adicionarH(List<Posicao> caminho, int x1, int x2, int y) {
        if (x1 <= x2) {
            for (int x = x1; x <= x2; x++) caminho.add(new Posicao(x, y));
        } else {
            for (int x = x1; x >= x2; x--) caminho.add(new Posicao(x, y));
        }
    }

    /** Adiciona segmento vertical de y1 até y2 na coluna x (exclui y1 para não duplicar). */
    private static void adicionarV(List<Posicao> caminho, int x, int y1, int y2) {
        if (y1 < y2) {
            for (int y = y1 + 1; y <= y2; y++) caminho.add(new Posicao(x, y));
        } else {
            for (int y = y1 - 1; y >= y2; y--) caminho.add(new Posicao(x, y));
        }
    }

    // ==================== UTILIDADES ====================

    private static List<Posicao> removerDuplicados(List<Posicao> caminho) {
        List<Posicao> limpo = new ArrayList<>();
        for (Posicao pos : caminho) {
            if (limpo.isEmpty() || !limpo.get(limpo.size() - 1).equals(pos)) {
                limpo.add(pos);
            }
        }
        return limpo;
    }
}
