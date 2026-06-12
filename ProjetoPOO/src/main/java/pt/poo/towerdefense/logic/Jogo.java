package pt.poo.towerdefense.logic;

import pt.poo.towerdefense.model.*;
import pt.poo.towerdefense.model.inimigo.*;
import pt.poo.towerdefense.model.torre.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Controlador principal do jogo (padrão MVC — Controller).
 * Gere o fluxo do jogo: inicialização, waves, movimento de inimigos,
 * ataques das torres, condições de vitória/derrota e pontuação.
 *
 * Conceito POO: Associação — o Jogo usa Jogador, Mapa, ModoJogo e Waves.
 * Conceito POO: Polimorfismo — chama torre.atacar() e inimigo.habilidadeEspecial()
 *               que se comportam de forma diferente em cada subclasse.
 * Conceito POO: Composição — contém a lista de Waves e inimigos ativos.
 *
 * O método atualizar() é chamado ~60x por segundo pelo AnimationTimer (EcraJogo).
 */
public class Jogo {
    // === Estado do jogo ===
    private Jogador jogador;                    // O jogador da partida atual
    private ModoJogo modo;                      // Modo de jogo ativo (História ou Praticar) — polimorfismo
    private Mapa mapa;                          // Mapa com a grelha e caminhos
    private List<Wave> waves;                   // Lista de todas as waves desta partida
    private Wave waveAtual;                     // Wave atualmente em curso
    private int indiceWaveAtual;                // Índice da wave atual (0-based)
    private List<Inimigo> inimigosAtivos;       // Inimigos vivos na wave atual
    private boolean emAndamento;                // true enquanto o jogo está a decorrer
    private boolean waveEmCurso;                // true durante uma wave ativa
    private boolean vitoria;                    // true quando o jogador ganha
    private boolean derrota;                    // true quando o castelo é destruído
    private long tempoInicio;                   // Timestamp de início da partida
    private long tempoJogo;                     // Duração total em segundos
    private boolean isEndless;                  // true no Nível 6 (waves infinitas)
    private int waveEndlessAtual;               // Contador de waves no modo endless
    private final Set<Inimigo> inimigosRecompensados = new HashSet<>(); // Evita recompensar o mesmo inimigo 2x

    /**
     * Regista um ataque visual (torre → inimigo) para a UI desenhar projéteis.
     */
    public record AtaqueVisual(double torreX, double torreY, double inimigoX, double inimigoY, String tipoTorre) {}

    /** Ataques que aconteceram neste frame — a UI consome-os para gerar projéteis. */
    private final List<AtaqueVisual> ataquesVisuais = new ArrayList<>();

    // === Callbacks para a UI (padrão Observer simplificado) ===
    // Quando algo acontece no jogo, estas funções notificam a UI para atualizar
    private Runnable onInimigoEliminado;    // Chamado quando um inimigo morre
    private Runnable onTorreDestruida;      // Chamado quando uma torre é destruída
    private Runnable onDanoCastelo;         // Chamado quando o castelo recebe dano
    private Runnable onWaveCompleta;        // Chamado quando uma wave termina
    private Runnable onVitoria;             // Chamado quando o jogador ganha
    private Runnable onDerrota;             // Chamado quando o jogador perde

    private final Random random;

    /** Construtor padrão com Random aleatório (mapas/waves diferentes a cada partida). */
    public Jogo() {
        this(new Random());
    }

    /** Construtor com seed — permite reproduzir o mesmo mapa/waves (para desafios e testes). */
    public Jogo(long seed) {
        this(new Random(seed));
    }

    /** Construtor privado — inicializa o estado base do jogo. */
    private Jogo(Random random) {
        this.random = random;
        this.inimigosAtivos = new ArrayList<>();
        this.emAndamento = false;
        this.waveEmCurso = false;
    }

    // Getters
    public Jogador getJogador() { return jogador; }
    public ModoJogo getModo() { return modo; }
    public Mapa getMapa() { return mapa; }
    public Wave getWaveAtual() { return waveAtual; }
    public int getIndiceWaveAtual() { return indiceWaveAtual; }
    public int getTotalWaves() { return waves != null ? waves.size() : 0; }
    public List<Inimigo> getInimigosAtivos() { return inimigosAtivos; }
    public boolean isEmAndamento() { return emAndamento; }
    public boolean isWaveEmCurso() { return waveEmCurso; }
    public boolean isVitoria() { return vitoria; }
    public boolean isDerrota() { return derrota; }
    public boolean isEndless() { return isEndless; }
    public long getTempoJogo() { return tempoJogo; }

    /**
     * Retorna e limpa a lista de ataques visuais do último frame.
     * Chamado pela UI para criar projéteis animados.
     */
    public List<AtaqueVisual> consumirAtaquesVisuais() {
        List<AtaqueVisual> copia = new ArrayList<>(ataquesVisuais);
        ataquesVisuais.clear();
        return copia;
    }

    // Callbacks
    public void setOnInimigoEliminado(Runnable r) { this.onInimigoEliminado = r; }
    public void setOnTorreDestruida(Runnable r) { this.onTorreDestruida = r; }
    public void setOnDanoCastelo(Runnable r) { this.onDanoCastelo = r; }
    public void setOnWaveCompleta(Runnable r) { this.onWaveCompleta = r; }
    public void setOnVitoria(Runnable r) { this.onVitoria = r; }
    public void setOnDerrota(Runnable r) { this.onDerrota = r; }

    /**
     * Inicializa uma nova partida com o modo de jogo escolhido.
     * Gera o mapa, cria o jogador, gera as waves e prepara o estado inicial.
     *
     * @param nomeJogador nome do jogador
     * @param modo modo de jogo escolhido (ModoHistoria ou ModoPraticar — polimorfismo)
     */
    public void iniciar(String nomeJogador, ModoJogo modo) {
        this.modo = modo;
        // Gera o mapa aleatório usando GeradorMapa (delegação)
        this.mapa = GeradorMapa.gerarMapa(modo.getTamanhoMapa(), modo.getNumeroCaminhos(), random);
        // Cria o jogador com os parâmetros do modo (polimorfismo — modo retorna valores diferentes)
        this.jogador = new Jogador(nomeJogador, modo.getOuroInicial(), modo.getHpCastelo());
        // Gera as waves usando GeradorWaves
        this.waves = GeradorWaves.gerarWaves(modo.getNumeroWaves(), modo.getInimigosDisponiveis(),
                modo.isEndless(), random);
        this.indiceWaveAtual = 0;
        this.inimigosAtivos = new ArrayList<>();
        this.emAndamento = true;
        this.waveEmCurso = false;
        this.vitoria = false;
        this.derrota = false;
        this.tempoInicio = System.currentTimeMillis();
        this.isEndless = modo.isEndless();
        this.waveEndlessAtual = modo.getNumeroWaves();
        this.inimigosRecompensados.clear();
    }

    /**
     * Inicia a próxima wave.
     */
    public void iniciarWave() {
        if (!emAndamento || waveEmCurso) return;

        if (indiceWaveAtual < waves.size()) {
            waveAtual = waves.get(indiceWaveAtual);
        } else if (isEndless) {
            // Gerar wave endless
            waveEndlessAtual++;
            waveAtual = GeradorWaves.gerarWaveEndless(waveEndlessAtual, modo.getInimigosDisponiveis(), random);
            waves.add(waveAtual);
        } else {
            // Todas as waves completas — vitória!
            vitoria = true;
            emAndamento = false;
            tempoJogo = (System.currentTimeMillis() - tempoInicio) / 1000;
            if (onVitoria != null) onVitoria.run();
            return;
        }

        waveAtual.iniciar();
        waveEmCurso = true;

        // Distribuir inimigos pelos caminhos com spawn escalonado
        List<List<Posicao>> caminhos = mapa.getCaminhos();
        List<Inimigo> inimigosWave = waveAtual.getInimigos();
        inimigosAtivos.clear();

        for (int i = 0; i < inimigosWave.size(); i++) {
            Inimigo inimigo = inimigosWave.get(i);
            int indiceCaminho = caminhos.size() > 1 ? i % caminhos.size() : 0;
            inimigo.setCaminho(new ArrayList<>(caminhos.get(indiceCaminho)));
            // Spawn escalonado: cada inimigo espera 0.4s * índice antes de sair
            inimigo.setAtrasoSpawn(i * 0.35);
            inimigosAtivos.add(inimigo);
        }
    }

    /**
     * GAME LOOP — Atualiza o estado do jogo (chamado a cada frame, ~60x/segundo).
     * Executa 7 fases por ordem:
     * 1. Mover inimigos ao longo do caminho
     * 2. Torres atacam inimigos no alcance (POLIMORFISMO — cada torre ataca de forma diferente)
     * 3. Inimigos usam habilidades especiais (POLIMORFISMO — cada tipo é diferente)
     * 4. Verificar torres destruídas
     * 5. Inimigos que chegaram ao castelo causam dano
     * 6. Remover inimigos mortos
     * 7. Verificar fim da wave
     *
     * @param deltaTime tempo desde o último frame em segundos (ex: 0.016 para 60fps)
     */
    public void atualizar(double deltaTime) {
        if (!emAndamento || !waveEmCurso) return;

        // Limpar ataques do frame anterior
        ataquesVisuais.clear();

        // 1. Mover inimigos
        for (Inimigo inimigo : inimigosAtivos) {
            if (inimigo.isVivo() && !inimigo.isChegouAoCastelo()) {
                inimigo.mover(deltaTime);
                inimigo.atualizarEfeitos(deltaTime);
            }
        }
        recompensarInimigosEliminados();

        // 2. Torres atacam
        List<Torre> torres = mapa.getTodasTorres();
        for (Torre torre : torres) {
            if (torre.isViva()) {
                torre.atualizarCooldown(deltaTime);
                if (torre.podeAtacar()) {
                    Posicao posTorre = torre.getPosicao();

                    List<Inimigo> atingidos = torre.atacar(inimigosAtivos);

                    // Registar ataques visuais para projéteis
                    if (posTorre != null && !atingidos.isEmpty()) {
                        for (Inimigo alvo : atingidos) {
                            double[] posVis = alvo.getPosicaoVisual();
                            ataquesVisuais.add(new AtaqueVisual(
                                    posTorre.getX(), posTorre.getY(),
                                    posVis[0], posVis[1],
                                    torre.getNome()
                            ));
                        }
                    }

                }
            }
        }
        recompensarInimigosEliminados();

        // 3. Inimigos que atacam torres (habilidade especial)
        for (Inimigo inimigo : inimigosAtivos) {
            if (inimigo.isVivo() && inimigo.isSpawned() && !inimigo.isChegouAoCastelo()) {
                inimigo.habilidadeEspecial(torres);
            }
        }

        // 4. Verificar torres destruídas
        Iterator<Torre> itTorres = torres.iterator();
        while (itTorres.hasNext()) {
            Torre torre = itTorres.next();
            if (!torre.isViva()) {
                jogador.ganharOuro(torre.getValorReembolso());
                Celula celula = mapa.getCelula(torre.getPosicao());
                if (celula != null) celula.removerTorre();
                if (onTorreDestruida != null) onTorreDestruida.run();
            }
        }

        // 5. Inimigos que chegaram ao castelo
        for (Inimigo inimigo : inimigosAtivos) {
            if (inimigo.isVivo() && inimigo.isChegouAoCastelo()) {
                jogador.receberDano(inimigo.getDanoCastelo());
                inimigo.receberDano(inimigo.getVida());
                if (onDanoCastelo != null) onDanoCastelo.run();

                if (!jogador.isVivo()) {
                    derrota = true;
                    emAndamento = false;
                    waveEmCurso = false;
                    tempoJogo = (System.currentTimeMillis() - tempoInicio) / 1000;
                    if (onDerrota != null) onDerrota.run();
                    return;
                }
            }
        }

        // 6. Remover inimigos mortos da lista ativa
        inimigosAtivos.removeIf(i -> !i.isVivo() || i.isChegouAoCastelo());

        // 7. Verificar fim da wave
        if (inimigosAtivos.isEmpty()) {
            waveAtual.verificarConclusao();
            if (waveAtual.isConcluida()) {
                waveEmCurso = false;
                indiceWaveAtual++;
                jogador.registarWaveCompletada();
                jogador.ganharOuro(calcularBonusWave(waveAtual.getNumero()));

                if (jogador.getHpCastelo() == jogador.getMaxHpCastelo()) {
                    jogador.adicionarPontuacao(200);
                }

                if (onWaveCompleta != null) onWaveCompleta.run();

                if (!isEndless && indiceWaveAtual >= waves.size()) {
                    vitoria = true;
                    emAndamento = false;
                    tempoJogo = (System.currentTimeMillis() - tempoInicio) / 1000;
                    if (onVitoria != null) onVitoria.run();
                }
            }
        }
    }

    /**
     * Verifica todos os inimigos ativos e recompensa os que morreram.
     * Usa um Set para garantir que cada inimigo só é recompensado uma vez.
     */
    private void recompensarInimigosEliminados() {
        for (Inimigo inimigo : inimigosAtivos) {
            if (!inimigo.isVivo() && inimigosRecompensados.add(inimigo)) {
                jogador.ganharOuro(inimigo.getRecompensa());
                jogador.adicionarPontuacao(inimigo.getRecompensa());
                jogador.registarInimigoEliminado();
                if (onInimigoEliminado != null) onInimigoEliminado.run();
            }
        }
    }

    private int calcularBonusWave(int numeroWave) {
        return 8 + numeroWave * 2;
    }

    /**
     * Coloca uma torre numa célula do mapa.
     * Valida: (1) célula livre, (2) ouro suficiente, (3) regras especiais (Campeão).
     * Usa instanceof para verificar se é TorreCampeao (polimorfismo).
     *
     * @param x coluna da célula
     * @param y linha da célula
     * @param tipoTorre tipo de torre a colocar
     * @return true se a torre foi colocada com sucesso
     */
    public boolean colocarTorre(int x, int y, String tipoTorre) {
        Celula celula = mapa.getCelula(x, y);
        if (celula == null || !celula.isLivre()) return false;

        Torre torre = criarTorre(tipoTorre);
        if (torre == null) return false;

        if (!jogador.gastarOuro(torre.getCusto())) return false;

        if (torre instanceof TorreCampeao) {
            int count = mapa.contarTorresDoTipo(TorreCampeao.class);
            if (count >= TorreCampeao.MAXIMO_NO_MAPA) {
                jogador.ganharOuro(torre.getCusto());
                return false;
            }
            boolean bossPresente = inimigosAtivos.stream().anyMatch(i -> i instanceof InimigoBoss);
            boolean bossNaWave = waveAtual != null && waveAtual.getInimigos().stream().anyMatch(i -> i instanceof InimigoBoss);
            if (!bossPresente && !bossNaWave) {
                jogador.ganharOuro(torre.getCusto());
                return false;
            }
        }

        return celula.colocarTorre(torre);
    }

    /**
     * Vende uma torre numa posição do mapa.
     */
    public boolean venderTorre(int x, int y) {
        Celula celula = mapa.getCelula(x, y);
        if (celula == null || !celula.temTorre()) return false;

        Torre torre = celula.removerTorre();
        jogador.ganharOuro(torre.getValorVenda());
        return true;
    }

    /**
     * Factory method — cria a torre adequada com base no nome.
     * Usa switch expression para mapear strings a classes concretas.
     * Demonstra polimorfismo: retorna sempre o tipo Torre (superclasse),
     * mas a instância real é uma subclasse específica.
     */
    private Torre criarTorre(String tipo) {
        return switch (tipo) {
            case "Arqueiro" -> new TorreArqueiro();
            case "Mago" -> new TorreMago();
            case "TorreGelo" -> new TorreGelo();
            case "Canhao" -> new TorreCanhao();
            case "TorreVeneno" -> new TorreVeneno();
            case "Campeao" -> new TorreCampeao();
            default -> null;
        };
    }

    public static Torre getInfoTorre(String tipo) {
        return switch (tipo) {
            case "Arqueiro" -> new TorreArqueiro();
            case "Mago" -> new TorreMago();
            case "TorreGelo" -> new TorreGelo();
            case "Canhao" -> new TorreCanhao();
            case "TorreVeneno" -> new TorreVeneno();
            case "Campeao" -> new TorreCampeao();
            default -> null;
        };
    }

    public boolean isTorreDisponivel(String tipo) {
        if (!modo.getHeroisDisponiveis().contains(tipo)) return false;
        Torre torre = criarTorre(tipo);
        if (torre == null) return false;

        if (jogador.getOuro() < torre.getCusto()) return false;

        if (tipo.equals("Campeao")) {
            int count = mapa.contarTorresDoTipo(TorreCampeao.class);
            if (count >= TorreCampeao.MAXIMO_NO_MAPA) return false;
            boolean bossPresente = inimigosAtivos.stream().anyMatch(i -> i instanceof InimigoBoss);
            boolean bossNaWave = waveAtual != null && waveAtual.getInimigos().stream().anyMatch(i -> i instanceof InimigoBoss);
            if (!bossPresente && !bossNaWave) return false;
        }

        return true;
    }

    public int getNumeroWaveAtual() {
        return indiceWaveAtual + 1;
    }

    public static String getNomeBonitoTorre(String tipo) {
        return switch (tipo) {
            case "Arqueiro" -> "Arqueiro";
            case "Mago" -> "Mago";
            case "TorreGelo" -> "Torre de Gelo";
            case "Canhao" -> "Canhão";
            case "TorreVeneno" -> "Torre Veneno";
            case "Campeao" -> "Campeão";
            default -> tipo;
        };
    }
}
