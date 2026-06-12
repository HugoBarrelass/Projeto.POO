package pt.poo.towerdefense.ui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import pt.poo.towerdefense.App;
import pt.poo.towerdefense.logic.Jogo;
import pt.poo.towerdefense.logic.ScoreManager;
import pt.poo.towerdefense.model.*;
import pt.poo.towerdefense.model.enums.TipoCelula;
import pt.poo.towerdefense.model.inimigo.Inimigo;
import pt.poo.towerdefense.model.inimigo.InimigoBoss;
import pt.poo.towerdefense.model.inimigo.InimigoTanque;
import pt.poo.towerdefense.model.torre.Torre;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Ecrã principal de jogo com:
 * - Barra superior: ouro, HP, wave actual
 * - Grelha central: mapa com torres desenhadas como personagens, inimigos como sprites, projéteis animados
 * - Painel lateral: heróis disponíveis e botões de controlo
 */
public class EcraJogo extends BorderPane {
    private final App app;
    private final Jogo jogo;
    private final ModoJogo modo;

    // UI components
    private Label lblOuro, lblHp, lblWave, lblScore;
    private Canvas canvas;
    private VBox painelHerois;
    private Button btnIniciarWave, btnVender;
    private String torreSelecionada;
    private int celulaSelecionadaX = -1, celulaSelecionadaY = -1;

    // Animação
    private AnimationTimer gameLoop;
    private long lastUpdate = 0;
    private boolean gameRunning = false;

    // Tamanho de cada célula no canvas
    private double cellSize;

    // Sistema de projéteis (trails visuais de ataques)
    private final List<Projetil> projeteis = new ArrayList<>();
    private final List<Explosao> explosoes = new ArrayList<>();
    private final Random random = new Random();

    // Decoracao: posicoes pre-calculadas para erva, arbustos, flores, pedras, etc.
    private final List<int[]> decoracaoErva = new ArrayList<>();
    private final List<int[]> decoracaoPedras = new ArrayList<>();
    private boolean decoracaoGerada = false;

    /**
     * Representa um projétil visual entre torre e inimigo.
     */
    private static class Projetil {
        double startX, startY, endX, endY;
        double progresso; // 0.0 a 1.0
        double velocidade; // velocidade do projétil
        Color cor;
        double tamanho;
        String tipo; // "arrow", "magic", "ice", "cannon", "poison", "sword"

        Projetil(double sx, double sy, double ex, double ey, Color cor, String tipo) {
            this.startX = sx; this.startY = sy;
            this.endX = ex; this.endY = ey;
            this.progresso = 0;
            this.velocidade = 5.0;
            this.cor = cor;
            this.tamanho = 4;
            this.tipo = tipo;
        }

        boolean atualizar(double dt) {
            progresso += velocidade * dt;
            return progresso >= 1.0;
        }

        double getX() { return startX + (endX - startX) * progresso; }
        double getY() { return startY + (endY - startY) * progresso; }
    }

    /**
     * Representa uma explosão visual (impacto).
     */
    private static class Explosao {
        double x, y;
        double raio;
        double maxRaio;
        double vida;
        Color cor;

        Explosao(double x, double y, Color cor, double maxRaio) {
            this.x = x; this.y = y;
            this.cor = cor;
            this.maxRaio = maxRaio;
            this.raio = 2;
            this.vida = 1.0;
        }

        boolean atualizar(double dt) {
            vida -= dt * 3;
            raio = maxRaio * (1 - vida);
            return vida <= 0;
        }
    }

    public EcraJogo(App app, ModoJogo modo) {
        this.app = app;
        this.modo = modo;
        this.jogo = modo.isEndless()
                ? new Jogo(ScoreManager.getSeedDesafioAtual())
                : new Jogo();
        this.torreSelecionada = null;

        setStyle("-fx-background-color: #08111F;");

        // Iniciar o jogo
        jogo.iniciar(app.getNomeJogador(), modo);

        // Configurar callbacks
        configurarCallbacks();

        // Construir UI
        setTop(criarBarraSuperior());
        setCenter(criarAreaJogo());
        setRight(criarPainelLateral());

        // Configurar game loop
        configurarGameLoop();
    }

    /**
     * Cria a barra superior com informações do jogo.
     */
    private HBox criarBarraSuperior() {
        HBox barra = new HBox(25);
        barra.setAlignment(Pos.CENTER);
        barra.setPadding(new Insets(12, 20, 12, 20));
        barra.setStyle("-fx-background-color: #0C1528; " +
                "-fx-border-color: rgba(53,224,183,0.16); -fx-border-width: 0 0 1 0;");

        // Ouro
        HBox ouroBox = criarInfoBox("G", "gold-label");
        lblOuro = (Label) ouroBox.getChildren().get(1);
        lblOuro.setText(String.valueOf(jogo.getJogador().getOuro()));

        // HP do castelo
        HBox hpBox = criarInfoBox("HP", "hp-label");
        lblHp = (Label) hpBox.getChildren().get(1);
        lblHp.setText(jogo.getJogador().getHpCastelo() + "/" + jogo.getJogador().getMaxHpCastelo());

        // Wave
        HBox waveBox = criarInfoBox("W", "wave-label");
        lblWave = (Label) waveBox.getChildren().get(1);
        String waveText = modo.isEndless() ?
                "Wave " + jogo.getNumeroWaveAtual() + " (∞)" :
                "Wave " + jogo.getNumeroWaveAtual() + "/" + jogo.getTotalWaves();
        lblWave.setText(waveText);

        // Score
        HBox scoreBox = criarInfoBox("P", "info-label");
        lblScore = (Label) scoreBox.getChildren().get(1);
        lblScore.setText(String.valueOf(jogo.getJogador().getPontuacao()));
        lblScore.setTextFill(Color.web("#F5C451"));

        // Nome do modo
        String modoNome = modo instanceof ModoHistoria ?
                "Nível " + ((ModoHistoria) modo).getNivelAtual().getNumero() :
                ((ModoPraticar) modo).getDificuldade().getNome();
        Label lblModo = new Label(modoNome);
        lblModo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lblModo.setTextFill(Color.web("#FFFFFF"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        barra.getChildren().addAll(ouroBox, hpBox, waveBox, scoreBox, spacer, lblModo);
        return barra;
    }

    private HBox criarInfoBox(String icone, String styleClass) {
        HBox box = new HBox(6);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(6, 14, 6, 14));
        box.setStyle("-fx-background-color: rgba(23,36,58,0.72); -fx-background-radius: 10; " +
                "-fx-border-radius: 10; -fx-border-color: rgba(94,234,212,0.12); -fx-border-width: 1;");

        Label iconeLabel = new Label(icone);
        iconeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        iconeLabel.setTextFill(Color.web("#35E0B7"));

        Label valorLabel = new Label("0");
        valorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
        valorLabel.setTextFill(Color.web("#FFFFFF"));

        box.getChildren().addAll(iconeLabel, valorLabel);
        return box;
    }

    /**
     * Cria a área central com o canvas do mapa.
     */
    private StackPane criarAreaJogo() {
        StackPane container = new StackPane();
        container.setPadding(new Insets(10));

        int tamanhoMapa = modo.getTamanhoMapa();
        double canvasSize = Math.min(720, Math.max(520, 60 * tamanhoMapa));
        cellSize = canvasSize / tamanhoMapa;

        canvas = new Canvas(canvasSize, canvasSize);
        canvas.setOnMouseClicked(e -> {
            int x = (int) (e.getX() / cellSize);
            int y = (int) (e.getY() / cellSize);
            handleClickMapa(x, y);
        });

        canvas.setOnMouseMoved(e -> {
            int x = (int) (e.getX() / cellSize);
            int y = (int) (e.getY() / cellSize);
            celulaSelecionadaX = x;
            celulaSelecionadaY = y;
        });

        // Moldura decorada do canvas
        VBox canvasBox = new VBox(canvas);
        canvasBox.setAlignment(Pos.CENTER);
        canvasBox.setPadding(new Insets(8));
        canvasBox.setStyle("-fx-background-color: linear-gradient(to bottom right, #101A2D, #0B1323); " +
                "-fx-background-radius: 12; -fx-border-radius: 12; " +
                "-fx-border-color: rgba(53,224,183,0.22); " +
                "-fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 24, 0, 0, 8);");
        canvasBox.setMaxWidth(canvasSize + 16);
        canvasBox.setMaxHeight(canvasSize + 16);

        container.getChildren().add(canvasBox);
        container.widthProperty().addListener((obs, oldValue, newValue) ->
                ajustarTamanhoCanvas(container, tamanhoMapa, canvasBox));
        container.heightProperty().addListener((obs, oldValue, newValue) ->
                ajustarTamanhoCanvas(container, tamanhoMapa, canvasBox));
        Platform.runLater(() -> ajustarTamanhoCanvas(container, tamanhoMapa, canvasBox));

        renderizar();
        return container;
    }

    private void ajustarTamanhoCanvas(StackPane container, int tamanhoMapa, VBox canvasBox) {
        double larguraDisponivel = container.getWidth() - 36;
        double alturaDisponivel = container.getHeight() - 36;
        if (larguraDisponivel <= 0 || alturaDisponivel <= 0) {
            return;
        }

        double tamanhoDisponivel = Math.min(larguraDisponivel, alturaDisponivel);
        double tamanhoMaximo = Math.min(1040, tamanhoDisponivel);
        double tamanhoAlinhado = Math.floor(tamanhoMaximo / tamanhoMapa) * tamanhoMapa;
        double tamanhoMinimo = Math.min(420, tamanhoMapa * 55);
        double novoTamanho = Math.max(tamanhoMinimo, tamanhoAlinhado);

        if (Math.abs(canvas.getWidth() - novoTamanho) < 0.5) {
            return;
        }

        canvas.setWidth(novoTamanho);
        canvas.setHeight(novoTamanho);
        cellSize = novoTamanho / tamanhoMapa;
        canvasBox.setMaxWidth(novoTamanho + 16);
        canvasBox.setMaxHeight(novoTamanho + 16);
        renderizar();
    }

    /**
     * Cria o painel lateral com heróis e controlos.
     */
    private VBox criarPainelLateral() {
        VBox painel = new VBox(10);
        painel.setPadding(new Insets(15));
        painel.setPrefWidth(230);
        painel.setStyle("-fx-background-color: #0C1528; " +
                "-fx-border-color: rgba(53,224,183,0.14); -fx-border-width: 0 0 0 1;");

        // Título decorado
        HBox tituloBox = new HBox(8);
        tituloBox.setAlignment(Pos.CENTER);
        tituloBox.setPadding(new Insets(8));
        tituloBox.setStyle("-fx-background-color: rgba(53,224,183,0.08); -fx-background-radius: 10;");
        Text tituloHerois = new Text("HERÓIS");
        tituloHerois.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        tituloHerois.setFill(Color.web("#35E0B7"));
        tituloBox.getChildren().add(tituloHerois);

        painelHerois = new VBox(6);
        atualizarPainelHerois();

        // Separador decorado
        Region sep = new Region();
        sep.setPrefHeight(2);
        sep.setStyle("-fx-background-color: linear-gradient(to right, transparent, #35E0B7, transparent);");

        // Botão iniciar wave
        btnIniciarWave = new Button("INICIAR WAVE");
        btnIniciarWave.getStyleClass().add("button-gold");
        btnIniciarWave.setMinWidth(200);
        btnIniciarWave.setOnAction(e -> {
            jogo.iniciarWave();
            gameRunning = true;
            btnIniciarWave.setDisable(true);
            atualizarUI();
        });

        // Botão vender torre
        btnVender = new Button("Vender Torre");
        btnVender.getStyleClass().add("button-secondary");
        btnVender.setMinWidth(200);
        btnVender.setDisable(true);
        btnVender.setOnAction(e -> {
            if (celulaSelecionadaX >= 0 && celulaSelecionadaY >= 0) {
                Celula celula = jogo.getMapa().getCelula(celulaSelecionadaX, celulaSelecionadaY);
                if (celula != null && celula.temTorre()) {
                    jogo.venderTorre(celulaSelecionadaX, celulaSelecionadaY);
                    atualizarUI();
                    renderizar();
                }
            }
        });

        // Botão voltar ao menu
        Button btnMenu = new Button("Menu Principal");
        btnMenu.getStyleClass().add("button-secondary");
        btnMenu.setMinWidth(200);
        btnMenu.setStyle(btnMenu.getStyle() + " -fx-font-size: 12px;");
        btnMenu.setOnAction(e -> {
            if (gameLoop != null) gameLoop.stop();
            app.mostrarMenuPrincipal();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        painel.getChildren().addAll(tituloBox, painelHerois, sep, btnIniciarWave, btnVender, spacer, btnMenu);
        return painel;
    }

    /**
     * Atualiza o painel de heróis disponíveis.
     */
    private void atualizarPainelHerois() {
        painelHerois.getChildren().clear();
        List<String> herois = modo.getHeroisDisponiveis();

        for (String tipo : herois) {
            Torre info = Jogo.getInfoTorre(tipo);
            if (info == null) continue;

            Button btn = new Button(info.getNome() + " — " + info.getCusto() + "g");
            btn.getStyleClass().add("hero-button");
            btn.setMinWidth(200);

            boolean disponivel = jogo.isTorreDisponivel(tipo);
            btn.setDisable(!disponivel);
            if (!disponivel) {
                btn.getStyleClass().add("hero-button-disabled");
            }

            String tooltipText = info.getNome() + "\n" +
                    "Dano: " + info.getDano() + "\n" +
                    "Alcance: " + info.getAlcance() + "\n" +
                    "HP: " + info.getMaxHp() + "\n" +
                    "Custo: " + info.getCusto() + " ouro";
            Tooltip tooltip = new Tooltip(tooltipText);
            tooltip.setFont(Font.font(12));
            Tooltip.install(btn, tooltip);

            btn.setOnAction(e -> {
                torreSelecionada = tipo.equals(torreSelecionada) ? null : tipo;
                atualizarPainelHerois();
            });

            if (tipo.equals(torreSelecionada)) {
                btn.getStyleClass().add("hero-button-selected");
            }

            painelHerois.getChildren().add(btn);
        }
    }

    /**
     * Lida com o clique numa célula do mapa.
     */
    private void handleClickMapa(int x, int y) {
        Celula celula = jogo.getMapa().getCelula(x, y);
        if (celula == null) return;

        celulaSelecionadaX = x;
        celulaSelecionadaY = y;

        if (torreSelecionada != null && celula.isLivre()) {
            boolean sucesso = jogo.colocarTorre(x, y, torreSelecionada);
            if (sucesso) {
                torreSelecionada = null;
                atualizarUI();
                renderizar();
            }
        }

        btnVender.setDisable(!celula.temTorre());
        if (celula.temTorre()) {
            Torre torre = celula.getTorre();
            btnVender.setText("Vender " + torre.getNome() + " (" + torre.getValorVenda() + "g)");
        } else {
            btnVender.setText("Vender Torre");
        }

        renderizar();
    }

    /**
     * Configura os callbacks do jogo.
     */
    private void configurarCallbacks() {
        jogo.setOnInimigoEliminado(() -> Platform.runLater(this::atualizarUI));
        jogo.setOnTorreDestruida(() -> Platform.runLater(this::atualizarUI));
        jogo.setOnDanoCastelo(() -> Platform.runLater(() -> {
            atualizarUI();
            lblHp.setEffect(new Glow(1.0));
            Timeline flash = new Timeline(
                    new KeyFrame(Duration.millis(200), new KeyValue(((Glow) lblHp.getEffect()).levelProperty(), 0))
            );
            flash.play();
        }));
        jogo.setOnWaveCompleta(() -> Platform.runLater(() -> {
            atualizarUI();
            gameRunning = false;
            btnIniciarWave.setDisable(false);
            if (jogo.isEndless() || jogo.getIndiceWaveAtual() < jogo.getTotalWaves()) {
                btnIniciarWave.setText("INICIAR WAVE " + jogo.getNumeroWaveAtual());
            }
        }));
        jogo.setOnVitoria(() -> Platform.runLater(this::mostrarVitoria));
        jogo.setOnDerrota(() -> Platform.runLater(this::mostrarDerrota));
    }

    /**
     * Configura o game loop (AnimationTimer).
     */
    private void configurarGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;
                deltaTime = Math.min(deltaTime, 0.05);

                if (gameRunning && jogo.isEmAndamento()) {
                    jogo.atualizar(deltaTime);

                    // Gerar projéteis das torres que atacaram
                    gerarProjeteis();

                    // Atualizar projéteis
                    atualizarProjeteis(deltaTime);
                    atualizarExplosoes(deltaTime);

                    renderizar();
                    atualizarUI();
                }
            }
        };
        gameLoop.start();
    }

    /**
     * Gera projéteis a partir dos ataques registados pelo Jogo neste frame.
     */
    private void gerarProjeteis() {
        List<Jogo.AtaqueVisual> ataques = jogo.consumirAtaquesVisuais();

        for (Jogo.AtaqueVisual ataque : ataques) {
            double sx = (ataque.torreX() + 0.5) * cellSize;
            double sy = (ataque.torreY() + 0.5) * cellSize;
            double ex = (ataque.inimigoX() + 0.5) * cellSize;
            double ey = (ataque.inimigoY() + 0.5) * cellSize;

            Color cor = getCorProjetilPorNome(ataque.tipoTorre());
            String tipo = getTipoProjetilPorNome(ataque.tipoTorre());
            projeteis.add(new Projetil(sx, sy, ex, ey, cor, tipo));
        }

        // Limitar projéteis para performance
        while (projeteis.size() > 80) projeteis.remove(0);
    }

    private Color getCorProjetilPorNome(String nomeTorre) {
        return switch (nomeTorre) {
            case "Arqueiro" -> Color.web("#ffcc00");
            case "Mago" -> Color.web("#9b59b6");
            case "Torre de Gelo" -> Color.web("#74b9ff");
            case "Canhão" -> Color.web("#e67e22");
            case "Torre Veneno" -> Color.web("#00b894");
            case "Campeão" -> Color.web("#e74c3c");
            default -> Color.web("#ffffff");
        };
    }

    private String getTipoProjetilPorNome(String nomeTorre) {
        return switch (nomeTorre) {
            case "Arqueiro" -> "arrow";
            case "Mago" -> "magic";
            case "Torre de Gelo" -> "ice";
            case "Canhão" -> "cannon";
            case "Torre Veneno" -> "poison";
            case "Campeão" -> "sword";
            default -> "arrow";
        };
    }

    private void atualizarProjeteis(double dt) {
        Iterator<Projetil> it = projeteis.iterator();
        while (it.hasNext()) {
            Projetil p = it.next();
            if (p.atualizar(dt)) {
                // Criar explosão no impacto
                explosoes.add(new Explosao(p.endX, p.endY, p.cor, 8));
                it.remove();
            }
        }
    }

    private void atualizarExplosoes(double dt) {
        explosoes.removeIf(e -> e.atualizar(dt));
    }

    /**
     * Gera posições aleatórias de decoração (erva, pedras) uma vez.
     */
    private void gerarDecoracao() {
        if (decoracaoGerada) return;
        decoracaoGerada = true;
        Mapa mapa = jogo.getMapa();
        int tamanho = mapa.getLargura();
        for (int y = 0; y < tamanho; y++) {
            for (int x = 0; x < tamanho; x++) {
                Celula c = mapa.getCelula(x, y);
                if (c.getTipo() == TipoCelula.LIVRE) {
                    // O campo ganha vida sem tapar demasiado as zonas para colocar torres.
                    if (random.nextDouble() < 0.46) {
                        decoracaoErva.add(new int[]{x, y, random.nextInt(6)});
                    }
                    if (random.nextDouble() < 0.16) {
                        decoracaoPedras.add(new int[]{x, y, random.nextInt(4)});
                    }
                }
            }
        }
    }

    /**
     * Renderiza o mapa, torres, inimigos, projéteis e decoração no canvas.
     */
    private void renderizar() {
        gerarDecoracao();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Mapa mapa = jogo.getMapa();
        int tamanho = mapa.getLargura();

        // Limpar canvas
        gc.setFill(Color.web("#2E7D32"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // ========== CAMADA 1: CÉLULAS ==========
        for (int y = 0; y < tamanho; y++) {
            for (int x = 0; x < tamanho; x++) {
                Celula celula = mapa.getCelula(x, y);
                double px = x * cellSize;
                double py = y * cellSize;

                switch (celula.getTipo()) {
                    case LIVRE -> desenharCelulaLivre(gc, px, py);
                    case CAMINHO -> desenharCelulaCaminho(gc, px, py, celula.getIndiceCaminho());
                    case CASTELO -> desenharCelulaCastelo(gc, px, py);
                    case SPAWN -> desenharCelulaSpawn(gc, px, py);
                }

                // Grid muito subtil, só para manter leitura tática.
                gc.setStroke(Color.web("#163E22", 0.16));
                gc.setLineWidth(0.5);
                gc.strokeRect(px, py, cellSize, cellSize);
            }
        }

        // ========== CAMADA 2: DECORAÇÃO ==========
        for (int[] erva : decoracaoErva) {
            Celula c = mapa.getCelula(erva[0], erva[1]);
            if (c.getTipo() == TipoCelula.LIVRE && !c.temTorre()) {
                double px = erva[0] * cellSize;
                double py = erva[1] * cellSize;
                gc.setFill(Color.web("#2F8F3E", 0.72));
                switch (erva[2]) {
                    case 0 -> {
                        gc.fillOval(px + cellSize * 0.18, py + cellSize * 0.62, 5, 4);
                        gc.fillOval(px + cellSize * 0.62, py + cellSize * 0.28, 4, 4);
                    }
                    case 1 -> {
                        gc.setStroke(Color.web("#1F6B2E", 0.70));
                        gc.setLineWidth(1.5);
                        gc.strokeLine(px + cellSize * 0.38, py + cellSize * 0.70, px + cellSize * 0.42, py + cellSize * 0.48);
                        gc.strokeLine(px + cellSize * 0.70, py + cellSize * 0.78, px + cellSize * 0.73, py + cellSize * 0.58);
                    }
                    case 2 -> {
                        gc.fillOval(px + cellSize * 0.28, py + cellSize * 0.38, 7, 4);
                        gc.fillOval(px + cellSize * 0.48, py + cellSize * 0.70, 6, 4);
                    }
                    case 3 -> {
                        gc.setFill(Color.web("#3FA34D", 0.75));
                        gc.fillOval(px + cellSize * 0.52, py + cellSize * 0.50, 10, 7);
                        gc.setFill(Color.web("#1E6B32", 0.80));
                        gc.fillOval(px + cellSize * 0.57, py + cellSize * 0.44, 7, 7);
                    }
                    case 4 -> {
                        gc.setFill(Color.web("#F6D365", 0.85));
                        gc.fillOval(px + cellSize * 0.30, py + cellSize * 0.64, 3, 3);
                        gc.setFill(Color.web("#FF7EB6", 0.78));
                        gc.fillOval(px + cellSize * 0.36, py + cellSize * 0.68, 3, 3);
                    }
                    case 5 -> {
                        gc.setFill(Color.web("#236B35", 0.86));
                        gc.fillRect(px + cellSize * 0.18, py + cellSize * 0.18, 3, 11);
                        gc.setFill(Color.web("#2E8E45", 0.86));
                        gc.fillOval(px + cellSize * 0.12, py + cellSize * 0.12, 12, 9);
                    }
                }
            }
        }
        for (int[] pedra : decoracaoPedras) {
            Celula c = mapa.getCelula(pedra[0], pedra[1]);
            if (c.getTipo() == TipoCelula.LIVRE && !c.temTorre()) {
                double px = pedra[0] * cellSize;
                double py = pedra[1] * cellSize;
                switch (pedra[2]) {
                    case 0 -> {
                        gc.setFill(Color.web("#8E8F82", 0.62));
                        gc.fillOval(px + cellSize * 0.28, py + cellSize * 0.52, 8, 5);
                    }
                    case 1 -> {
                        gc.setFill(Color.web("#B7A37A", 0.46));
                        gc.fillOval(px + cellSize * 0.58, py + cellSize * 0.40, 7, 4);
                        gc.fillOval(px + cellSize * 0.66, py + cellSize * 0.48, 5, 3);
                    }
                    case 2 -> {
                        gc.setFill(Color.web("#2F7D3A", 0.70));
                        gc.fillOval(px + cellSize * 0.24, py + cellSize * 0.22, 12, 9);
                    }
                    case 3 -> {
                        gc.setFill(Color.web("#6A4F2B", 0.42));
                        gc.fillOval(px + cellSize * 0.18, py + cellSize * 0.76, 10, 4);
                    }
                }
            }
        }

        // ========== CAMADA 3: TORRES (como personagens) ==========
        for (int y = 0; y < tamanho; y++) {
            for (int x = 0; x < tamanho; x++) {
                Celula celula = mapa.getCelula(x, y);
                if (celula.temTorre()) {
                    desenharTorre(gc, celula.getTorre(), x * cellSize, y * cellSize);
                }

                // Highlight da célula selecionada para colocar torre
                if (x == celulaSelecionadaX && y == celulaSelecionadaY && celula.isLivre() && torreSelecionada != null) {
                    double px = x * cellSize;
                    double py = y * cellSize;
                    gc.setFill(Color.web("#35E0B7", 0.14));
                    gc.fillRect(px, py, cellSize, cellSize);
                    gc.setStroke(Color.web("#35E0B7", 0.75));
                    gc.setLineWidth(2);
                    gc.strokeRect(px + 1, py + 1, cellSize - 2, cellSize - 2);
                }

                // Raio de alcance da torre selecionada
                if (x == celulaSelecionadaX && y == celulaSelecionadaY && celula.temTorre()) {
                    Torre torre = celula.getTorre();
                    double px = x * cellSize;
                    double py = y * cellSize;
                    gc.setStroke(Color.web("#58B8FF", 0.18));
                    gc.setLineWidth(1);
                    gc.setLineDashes(4, 4);
                    double raio = torre.getAlcance() * cellSize;
                    gc.strokeOval(px + cellSize / 2 - raio, py + cellSize / 2 - raio, raio * 2, raio * 2);
                    gc.setLineDashes(null);
                }
            }
        }

        // ========== CAMADA 4: PROJÉTEIS ==========
        for (Projetil p : projeteis) {
            double px = p.getX();
            double py = p.getY();

            // Trail (rasto)
            gc.setStroke(p.cor.deriveColor(0, 1, 1, 0.3));
            gc.setLineWidth(1.5);
            double trailStartX = p.startX + (p.endX - p.startX) * Math.max(0, p.progresso - 0.3);
            double trailStartY = p.startY + (p.endY - p.startY) * Math.max(0, p.progresso - 0.3);
            gc.strokeLine(trailStartX, trailStartY, px, py);

            // Projétil
            gc.setFill(p.cor);
            double size = 4;
            switch (p.tipo) {
                case "arrow" -> {
                    gc.setFill(Color.web("#ffcc00"));
                    gc.fillRect(px - 1, py - 1, 3, 3);
                    // Ponta da seta
                    gc.setFill(Color.WHITE);
                    gc.fillRect(px, py, 2, 2);
                }
                case "magic" -> {
                    gc.setFill(Color.web("#9b59b6", 0.8));
                    gc.fillOval(px - 4, py - 4, 8, 8);
                    gc.setFill(Color.web("#d2b4de", 0.5));
                    gc.fillOval(px - 6, py - 6, 12, 12);
                }
                case "ice" -> {
                    gc.setFill(Color.web("#74b9ff", 0.9));
                    gc.fillOval(px - 3, py - 3, 6, 6);
                    gc.setFill(Color.web("#a8d8ff", 0.4));
                    gc.fillOval(px - 5, py - 5, 10, 10);
                }
                case "cannon" -> {
                    gc.setFill(Color.web("#e67e22"));
                    gc.fillOval(px - 4, py - 4, 8, 8);
                    gc.setFill(Color.web("#f39c12", 0.5));
                    gc.fillOval(px - 6, py - 6, 12, 12);
                }
                case "poison" -> {
                    gc.setFill(Color.web("#00b894", 0.8));
                    gc.fillOval(px - 3, py - 3, 6, 6);
                    gc.setFill(Color.web("#55efc4", 0.3));
                    gc.fillOval(px - 5, py - 5, 10, 10);
                }
                case "sword" -> {
                    gc.setFill(Color.web("#e74c3c"));
                    size = 5;
                    gc.fillRect(px - 2, py - 2, size, size);
                    gc.setFill(Color.web("#ffd700", 0.5));
                    gc.fillRect(px - 3, py - 3, size + 2, size + 2);
                }
            }
        }

        // ========== CAMADA 5: INIMIGOS (como sprites) ==========
        List<Inimigo> inimigos = jogo.getInimigosAtivos();
        for (Inimigo inimigo : inimigos) {
            if (!inimigo.isVivo() || !inimigo.isSpawned()) continue;
            desenharInimigo(gc, inimigo);
        }

        // ========== CAMADA 6: EXPLOSÕES ==========
        for (Explosao exp : explosoes) {
            gc.setFill(exp.cor.deriveColor(0, 1, 1, exp.vida * 0.5));
            gc.fillOval(exp.x - exp.raio, exp.y - exp.raio, exp.raio * 2, exp.raio * 2);
        }
    }

    // ===== Métodos de desenho detalhado =====

    private void desenharCelulaLivre(GraphicsContext gc, double px, double py) {
        // Campo verde com ligeira variação para não parecer uma grelha plana.
        gc.setFill(Color.web("#3E9A43"));
        gc.fillRect(px + 0.5, py + 0.5, cellSize - 1, cellSize - 1);
        gc.setFill(Color.web("#4FAF4F", 0.34));
        gc.fillRect(px + cellSize * 0.08, py + cellSize * 0.08, cellSize * 0.84, cellSize * 0.84);
        gc.setStroke(Color.web("#2E7D32", 0.30));
        gc.setLineWidth(1);
        gc.strokeLine(px + cellSize * 0.15, py + cellSize * 0.78, px + cellSize * 0.85, py + cellSize * 0.18);
    }

    private void desenharCelulaCaminho(GraphicsContext gc, double px, double py, int indiceCaminho) {
        if (indiceCaminho == 1) {
            // Segundo caminho: estrada de alcatrao cinzenta.
            gc.setFill(Color.web("#5D646A"));
            gc.fillRect(px + 0.5, py + 0.5, cellSize - 1, cellSize - 1);
            gc.setFill(Color.web("#737B82", 0.42));
            gc.fillRect(px + 2, py + 2, cellSize - 4, cellSize - 4);
            gc.setStroke(Color.web("#3E454A", 0.55));
            gc.setLineWidth(2);
            gc.strokeLine(px + 1, py + 2, px + cellSize - 1, py + 2);
            gc.strokeLine(px + 1, py + cellSize - 3, px + cellSize - 1, py + cellSize - 3);
            gc.setStroke(Color.web("#E6E0C8", 0.55));
            gc.setLineWidth(1.2);
            gc.setLineDashes(6, 6);
            gc.strokeLine(px + cellSize * 0.20, py + cellSize * 0.50,
                    px + cellSize * 0.80, py + cellSize * 0.50);
            gc.setLineDashes(null);
            gc.setFill(Color.web("#3F464B", 0.28));
            gc.fillOval(px + cellSize * 0.20, py + cellSize * 0.22, 5, 2);
            gc.fillOval(px + cellSize * 0.64, py + cellSize * 0.72, 4, 2);
        } else {
            // Primeiro caminho: terra batida castanha.
            gc.setFill(Color.web("#9A6A37"));
            gc.fillRect(px + 0.5, py + 0.5, cellSize - 1, cellSize - 1);
            gc.setFill(Color.web("#B88446", 0.48));
            gc.fillOval(px + cellSize * 0.08, py + cellSize * 0.12, cellSize * 0.84, cellSize * 0.70);
            gc.setFill(Color.web("#6E4722", 0.36));
            gc.fillOval(px + cellSize * 0.14, py + cellSize * 0.38, 7, 3);
            gc.fillOval(px + cellSize * 0.56, py + cellSize * 0.60, 6, 3);
            gc.fillOval(px + cellSize * 0.74, py + cellSize * 0.26, 4, 3);
            gc.setStroke(Color.web("#5F3B1C", 0.22));
            gc.setLineWidth(1);
            gc.strokeLine(px + cellSize * 0.10, py + cellSize * 0.78,
                    px + cellSize * 0.88, py + cellSize * 0.22);
        }
    }

    private void desenharCelulaCastelo(GraphicsContext gc, double px, double py) {
        // Fundo pedra castelo com gradiente
        gc.setFill(Color.web("#8B7355"));
        gc.fillRect(px + 0.5, py + 0.5, cellSize - 1, cellSize - 1);
        gc.setFill(Color.web("#6B5A42", 0.5));
        gc.fillRect(px + 2, py + 2, cellSize - 4, cellSize - 4);
        // Borda dourada/bronze brilhante
        gc.setStroke(Color.web("#C7A46B"));
        gc.setLineWidth(2);
        gc.strokeRect(px + 1, py + 1, cellSize - 2, cellSize - 2);
        // Castelo desenhado com formas
        double cx = px + cellSize * 0.5;
        double cy = py + cellSize * 0.5;
        double s = cellSize * 0.3;
        // Base do castelo
        gc.setFill(Color.web("#bdc3c7"));
        gc.fillRect(cx - s, cy - s * 0.5, s * 2, s * 1.2);
        // Torres do castelo
        gc.fillRect(cx - s, cy - s, s * 0.4, s * 0.5);
        gc.fillRect(cx + s * 0.6, cy - s, s * 0.4, s * 0.5);
        gc.fillRect(cx - s * 0.2, cy - s * 1.1, s * 0.4, s * 0.6);
        // Porta
        gc.setFill(Color.web("#5d4037"));
        gc.fillRect(cx - s * 0.2, cy + s * 0.1, s * 0.4, s * 0.6);
        // Bandeira
        gc.setFill(Color.web("#7A3B2E"));
        gc.fillRect(cx, cy - s * 1.1, s * 0.5, s * 0.3);
        gc.setStroke(Color.web("#C7A46B"));
        gc.setLineWidth(1);
        gc.strokeLine(cx, cy - s * 1.1, cx, cy - s * 0.6);
    }

    private void desenharCelulaSpawn(GraphicsContext gc, double px, double py) {
        gc.setFill(Color.web("#4E5960"));
        gc.fillRect(px + 0.5, py + 0.5, cellSize - 1, cellSize - 1);
        gc.setFill(Color.web("#697178", 0.5));
        gc.fillRect(px + 2, py + 2, cellSize - 4, cellSize - 4);
        gc.setStroke(Color.web("#D0C6A6", 0.65));
        gc.setLineWidth(1.5);
        gc.strokeRect(px + 2, py + 2, cellSize - 4, cellSize - 4);
        // Seta animada
        gc.setFill(Color.web("#E9D8A6"));
        double cx = px + cellSize * 0.5;
        double cy = py + cellSize * 0.5;
        double s = cellSize * 0.2;
        gc.fillPolygon(
                new double[]{cx - s, cx + s, cx - s},
                new double[]{cy - s, cy, cy + s}, 3);
    }

    /**
     * Desenha uma torre como uma personagem/sprite detalhada.
     */
    private void desenharTorre(GraphicsContext gc, Torre torre, double px, double py) {
        String nome = torre.getNome();
        double cx = px + cellSize * 0.5;
        double cy = py + cellSize * 0.5;
        double s = cellSize * 0.4; // escala base

        // Base discreta da torre para manter o campo visivel.
        Color corBase = getCorBaseTorre(nome);
        gc.setFill(Color.web("#D6C2A1", 0.62));
        gc.fillRoundRect(px + cellSize * 0.12, py + cellSize * 0.16,
                cellSize * 0.76, cellSize * 0.70, 10, 10);

        // Borda colorida
        gc.setStroke(corBase);
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(px + cellSize * 0.12, py + cellSize * 0.16,
                cellSize * 0.76, cellSize * 0.70, 10, 10);

        // Desenhar personagem baseado no tipo
        double charX = px + cellSize * 0.5;
        double charY = py + cellSize * 0.5;
        double size = cellSize * 0.35;

        switch (nome) {
            case "Arqueiro" -> desenharArqueiro(gc, charX, charY, size);
            case "Mago" -> desenharMago(gc, charX, charY, size);
            case "Torre de Gelo" -> desenharTorreGelo(gc, charX, charY, size);
            case "Canhão" -> desenharCanhao(gc, charX, charY, size);
            case "Torre Veneno" -> desenharTorreVeneno(gc, charX, charY, size);
            case "Campeão" -> desenharCampeao(gc, charX, charY, size);
        }

        // Barra de HP (só quando não está a 100%)
        double hpRatio = (double) torre.getHp() / torre.getMaxHp();
        if (hpRatio < 1.0) {
            gc.setFill(Color.web("#333"));
            gc.fillRect(px + 3, py + cellSize - 6, cellSize - 6, 4);
            gc.setFill(hpRatio > 0.5 ? Color.web("#2ecc71") : Color.web("#e74c3c"));
            gc.fillRect(px + 3, py + cellSize - 6, (cellSize - 6) * hpRatio, 4);
        }

        // Indicador de abrandamento
        if (torre.isAbrandada()) {
            gc.setStroke(Color.web("#9b59b6", 0.6));
            gc.setLineWidth(1);
            gc.setLineDashes(3, 3);
            gc.strokeRect(px + 2, py + 2, cellSize - 4, cellSize - 4);
            gc.setLineDashes(null);
        }
    }

    private void desenharArqueiro(GraphicsContext gc, double cx, double cy, double s) {
        // Corpo (verde floresta)
        gc.setFill(Color.web("#27ae60"));
        gc.fillOval(cx - s * 0.5, cy - s * 0.3, s, s * 1.2);
        // Cabeça
        gc.setFill(Color.web("#ffdab9"));
        gc.fillOval(cx - s * 0.3, cy - s * 0.8, s * 0.6, s * 0.55);
        // Chapéu
        gc.setFill(Color.web("#2d6b3f"));
        gc.fillPolygon(
                new double[]{cx - s * 0.4, cx, cx + s * 0.4},
                new double[]{cy - s * 0.6, cy - s * 1.2, cy - s * 0.6}, 3);
        // Arco
        gc.setStroke(Color.web("#8B4513"));
        gc.setLineWidth(1.5);
        gc.strokeArc(cx + s * 0.3, cy - s * 0.6, s * 0.5, s * 1.0, -90, 180, javafx.scene.shape.ArcType.OPEN);
        // Seta
        gc.setStroke(Color.web("#ffd700"));
        gc.setLineWidth(1);
        gc.strokeLine(cx + s * 0.55, cy - s * 0.1, cx + s * 0.55, cy + s * 0.3);
    }

    private void desenharMago(GraphicsContext gc, double cx, double cy, double s) {
        // Manto (roxo)
        gc.setFill(Color.web("#8e44ad"));
        gc.fillOval(cx - s * 0.6, cy - s * 0.3, s * 1.2, s * 1.3);
        // Cabeça
        gc.setFill(Color.web("#ffdab9"));
        gc.fillOval(cx - s * 0.3, cy - s * 0.7, s * 0.6, s * 0.5);
        // Chapéu de mago
        gc.setFill(Color.web("#6c3483"));
        gc.fillPolygon(
                new double[]{cx - s * 0.5, cx, cx + s * 0.5},
                new double[]{cy - s * 0.5, cy - s * 1.3, cy - s * 0.5}, 3);
        // Estrela no chapéu
        gc.setFill(Color.web("#ffd700"));
        gc.fillOval(cx - 2, cy - s * 0.9, 4, 4);
        // Cajado
        gc.setStroke(Color.web("#a0522d"));
        gc.setLineWidth(1.5);
        gc.strokeLine(cx + s * 0.5, cy - s * 0.4, cx + s * 0.5, cy + s * 0.8);
        // Orbe
        gc.setFill(Color.web("#e74c3c", 0.8));
        gc.fillOval(cx + s * 0.35, cy - s * 0.6, s * 0.3, s * 0.3);
    }

    private void desenharTorreGelo(GraphicsContext gc, double cx, double cy, double s) {
        // Cristal de gelo
        gc.setFill(Color.web("#74b9ff", 0.7));
        gc.fillPolygon(
                new double[]{cx, cx + s * 0.5, cx + s * 0.3, cx - s * 0.3, cx - s * 0.5},
                new double[]{cy - s, cy - s * 0.2, cy + s * 0.7, cy + s * 0.7, cy - s * 0.2}, 5);
        // Brilho
        gc.setFill(Color.web("#a8d8ff", 0.5));
        gc.fillOval(cx - s * 0.2, cy - s * 0.6, s * 0.3, s * 0.4);
        // Base
        gc.setFill(Color.web("#4a8abd"));
        gc.fillRect(cx - s * 0.5, cy + s * 0.5, s, s * 0.3);
        // Flocos
        gc.setFill(Color.web("#ffffff", 0.6));
        gc.fillOval(cx - s * 0.6, cy - s * 0.3, 3, 3);
        gc.fillOval(cx + s * 0.5, cy + s * 0.1, 3, 3);
    }

    private void desenharCanhao(GraphicsContext gc, double cx, double cy, double s) {
        // Base de madeira
        gc.setFill(Color.web("#8B4513"));
        gc.fillRect(cx - s * 0.6, cy + s * 0.2, s * 1.2, s * 0.5);
        // Rodas
        gc.setFill(Color.web("#5d4037"));
        gc.fillOval(cx - s * 0.6, cy + s * 0.3, s * 0.4, s * 0.4);
        gc.fillOval(cx + s * 0.2, cy + s * 0.3, s * 0.4, s * 0.4);
        // Corpo do canhão
        gc.setFill(Color.web("#555"));
        gc.fillRect(cx - s * 0.3, cy - s * 0.4, s * 0.6, s * 0.7);
        // Boca do canhão
        gc.setFill(Color.web("#333"));
        gc.fillRect(cx - s * 0.2, cy - s * 0.7, s * 0.4, s * 0.4);
        // Detalhe
        gc.setFill(Color.web("#e67e22"));
        gc.fillRect(cx - s * 0.15, cy - s * 0.1, s * 0.3, s * 0.1);
    }

    private void desenharTorreVeneno(GraphicsContext gc, double cx, double cy, double s) {
        // Caldeirão
        gc.setFill(Color.web("#2d3436"));
        gc.fillOval(cx - s * 0.5, cy - s * 0.1, s, s * 0.8);
        // Líquido verde
        gc.setFill(Color.web("#00b894", 0.8));
        gc.fillOval(cx - s * 0.4, cy, s * 0.8, s * 0.5);
        // Bolhas
        gc.setFill(Color.web("#55efc4", 0.6));
        gc.fillOval(cx - s * 0.1, cy - s * 0.1, s * 0.15, s * 0.15);
        gc.fillOval(cx + s * 0.15, cy + s * 0.05, s * 0.1, s * 0.1);
        // Fumo tóxico
        gc.setFill(Color.web("#00b894", 0.3));
        gc.fillOval(cx - s * 0.3, cy - s * 0.6, s * 0.3, s * 0.3);
        gc.fillOval(cx + s * 0.1, cy - s * 0.8, s * 0.25, s * 0.25);
        // Base
        gc.setFill(Color.web("#636e72"));
        gc.fillRect(cx - s * 0.3, cy + s * 0.5, s * 0.6, s * 0.2);
    }

    private void desenharCampeao(GraphicsContext gc, double cx, double cy, double s) {
        // Armadura dourada/vermelha
        gc.setFill(Color.web("#c0392b"));
        gc.fillOval(cx - s * 0.5, cy - s * 0.2, s, s * 1.1);
        // Cabeça com elmo
        gc.setFill(Color.web("#bdc3c7"));
        gc.fillOval(cx - s * 0.3, cy - s * 0.7, s * 0.6, s * 0.55);
        // Viseira do elmo
        gc.setFill(Color.web("#2c3e50"));
        gc.fillRect(cx - s * 0.2, cy - s * 0.45, s * 0.4, s * 0.15);
        // Pluma do elmo
        gc.setFill(Color.web("#e74c3c"));
        gc.fillRect(cx - s * 0.05, cy - s * 1.0, s * 0.1, s * 0.35);
        // Espada grande
        gc.setStroke(Color.web("#bdc3c7"));
        gc.setLineWidth(2);
        gc.strokeLine(cx + s * 0.5, cy - s * 0.8, cx + s * 0.5, cy + s * 0.5);
        // Guarda da espada
        gc.setFill(Color.web("#ffd700"));
        gc.fillRect(cx + s * 0.35, cy - s * 0.3, s * 0.3, s * 0.1);
        // Escudo
        gc.setFill(Color.web("#e74c3c", 0.8));
        gc.fillOval(cx - s * 0.8, cy - s * 0.3, s * 0.5, s * 0.6);
        gc.setStroke(Color.web("#ffd700"));
        gc.setLineWidth(1);
        gc.strokeOval(cx - s * 0.8, cy - s * 0.3, s * 0.5, s * 0.6);
    }

    /**
     * Desenha um inimigo como sprite detalhado (NÃO uma bola).
     */
    private void desenharInimigo(GraphicsContext gc, Inimigo inimigo) {
        double[] posVisual = inimigo.getPosicaoVisual();
        double px = posVisual[0] * cellSize;
        double py = posVisual[1] * cellSize;
        double cx = px + cellSize * 0.5;
        double cy = py + cellSize * 0.5;
        double s = cellSize * 0.3;

        switch (inimigo.getNome()) {
            case "Normal" -> {
                // Goblin verde
                gc.setFill(Color.web("#e74c3c"));
                gc.fillOval(cx - s * 0.5, cy - s * 0.2, s, s * 1.0);
                gc.setFill(Color.web("#c0392b"));
                gc.fillOval(cx - s * 0.3, cy - s * 0.6, s * 0.6, s * 0.5);
                // Olhos
                gc.setFill(Color.WHITE);
                gc.fillOval(cx - s * 0.2, cy - s * 0.45, s * 0.15, s * 0.15);
                gc.fillOval(cx + s * 0.05, cy - s * 0.45, s * 0.15, s * 0.15);
                gc.setFill(Color.BLACK);
                gc.fillOval(cx - s * 0.15, cy - s * 0.42, s * 0.08, s * 0.08);
                gc.fillOval(cx + s * 0.08, cy - s * 0.42, s * 0.08, s * 0.08);
            }
            case "Rápido" -> {
                // Ninja azul
                gc.setFill(Color.web("#2980b9"));
                gc.fillRect(cx - s * 0.4, cy - s * 0.2, s * 0.8, s * 0.9);
                gc.setFill(Color.web("#3498db"));
                gc.fillOval(cx - s * 0.3, cy - s * 0.6, s * 0.6, s * 0.5);
                // Máscara
                gc.setFill(Color.web("#1a5276"));
                gc.fillRect(cx - s * 0.35, cy - s * 0.4, s * 0.7, s * 0.15);
                // Olhos vermelhos
                gc.setFill(Color.web("#e74c3c"));
                gc.fillOval(cx - s * 0.15, cy - s * 0.38, s * 0.1, s * 0.08);
                gc.fillOval(cx + s * 0.05, cy - s * 0.38, s * 0.1, s * 0.08);
                // Linhas de velocidade
                gc.setStroke(Color.web("#74b9ff", 0.4));
                gc.setLineWidth(1);
                gc.strokeLine(cx - s * 0.8, cy - s * 0.1, cx - s * 0.5, cy - s * 0.1);
                gc.strokeLine(cx - s * 0.9, cy + s * 0.2, cx - s * 0.6, cy + s * 0.2);
            }
            case "Arqueiro Inimigo" -> {
                // Arqueiro roxo
                gc.setFill(Color.web("#8e44ad"));
                gc.fillOval(cx - s * 0.5, cy - s * 0.2, s, s * 1.0);
                gc.setFill(Color.web("#9b59b6"));
                gc.fillOval(cx - s * 0.3, cy - s * 0.6, s * 0.6, s * 0.5);
                // Capuz
                gc.setFill(Color.web("#6c3483"));
                gc.fillPolygon(
                        new double[]{cx - s * 0.35, cx, cx + s * 0.35},
                        new double[]{cy - s * 0.45, cy - s * 0.9, cy - s * 0.45}, 3);
                // Arco pequeno
                gc.setStroke(Color.web("#a0522d"));
                gc.setLineWidth(1);
                gc.strokeArc(cx + s * 0.2, cy - s * 0.4, s * 0.4, s * 0.6, -90, 180, javafx.scene.shape.ArcType.OPEN);
            }
            case "Tanque" -> {
                // Golem de pedra
                gc.setFill(Color.web("#636e72"));
                gc.fillRect(cx - s * 0.5, cy - s * 0.3, s, s * 1.1);
                gc.setFill(Color.web("#7f8c8d"));
                gc.fillRect(cx - s * 0.4, cy - s * 0.7, s * 0.8, s * 0.5);
                // Escudo
                gc.setFill(Color.web("#555"));
                gc.fillRect(cx - s * 0.7, cy - s * 0.3, s * 0.3, s * 0.6);
                // Olhos amarelos
                gc.setFill(Color.web("#f1c40f"));
                gc.fillOval(cx - s * 0.2, cy - s * 0.55, s * 0.15, s * 0.12);
                gc.fillOval(cx + s * 0.05, cy - s * 0.55, s * 0.15, s * 0.12);
                // Rachaduras
                gc.setStroke(Color.web("#2d3436", 0.5));
                gc.setLineWidth(0.5);
                gc.strokeLine(cx - s * 0.1, cy - s * 0.3, cx + s * 0.1, cy);
            }
            case "Feiticeiro" -> {
                // Feiticeiro místico
                gc.setFill(Color.web("#6c3483"));
                gc.fillOval(cx - s * 0.5, cy - s * 0.2, s, s * 1.0);
                gc.setFill(Color.web("#9b59b6"));
                gc.fillOval(cx - s * 0.3, cy - s * 0.6, s * 0.6, s * 0.5);
                // Chapéu
                gc.setFill(Color.web("#4a235a"));
                gc.fillPolygon(
                        new double[]{cx - s * 0.4, cx, cx + s * 0.4},
                        new double[]{cy - s * 0.4, cy - s * 1.1, cy - s * 0.4}, 3);
                // Orbe mágica
                gc.setFill(Color.web("#e74c3c", 0.7));
                gc.fillOval(cx + s * 0.3, cy - s * 0.1, s * 0.25, s * 0.25);
                // Aura
                gc.setStroke(Color.web("#9b59b6", 0.3));
                gc.setLineWidth(1);
                gc.strokeOval(cx - s * 0.7, cy - s * 0.5, s * 1.4, s * 1.3);
            }
            case "Boss" -> {
                // Demónio grande
                double bs = s * 1.3;
                gc.setFill(Color.web("#8b0000"));
                gc.fillOval(cx - bs * 0.5, cy - bs * 0.2, bs, bs * 1.1);
                gc.setFill(Color.web("#b22222"));
                gc.fillOval(cx - bs * 0.4, cy - bs * 0.7, bs * 0.8, bs * 0.6);
                // Chifres
                gc.setFill(Color.web("#2c2c2c"));
                gc.fillPolygon(
                        new double[]{cx - bs * 0.35, cx - bs * 0.5, cx - bs * 0.2},
                        new double[]{cy - bs * 0.5, cy - bs * 1.0, cy - bs * 0.5}, 3);
                gc.fillPolygon(
                        new double[]{cx + bs * 0.35, cx + bs * 0.5, cx + bs * 0.2},
                        new double[]{cy - bs * 0.5, cy - bs * 1.0, cy - bs * 0.5}, 3);
                // Olhos de fogo
                gc.setFill(Color.web("#f1c40f"));
                gc.fillOval(cx - bs * 0.2, cy - bs * 0.5, bs * 0.15, bs * 0.12);
                gc.fillOval(cx + bs * 0.05, cy - bs * 0.5, bs * 0.15, bs * 0.12);
                gc.setFill(Color.web("#e74c3c"));
                gc.fillOval(cx - bs * 0.17, cy - bs * 0.48, bs * 0.08, bs * 0.08);
                gc.fillOval(cx + bs * 0.08, cy - bs * 0.48, bs * 0.08, bs * 0.08);
                // Boca
                gc.setFill(Color.web("#1a1a1a"));
                gc.fillRect(cx - bs * 0.15, cy - bs * 0.2, bs * 0.3, bs * 0.1);
                // Aura escura
                gc.setStroke(Color.web("#e74c3c", 0.2));
                gc.setLineWidth(2);
                gc.strokeOval(cx - bs * 0.7, cy - bs * 0.6, bs * 1.4, bs * 1.4);
            }
        }

        // Barra de HP acima do inimigo
        double barWidth = cellSize * 0.8;
        double barX = px + (cellSize - barWidth) / 2;
        double barY = py - 3;
        double hpRatio = inimigo.getPercentagemVida();
        gc.setFill(Color.web("#2F2A24", 0.8));
        gc.fillRect(barX, barY, barWidth, 4);
        Color hpColor = hpRatio > 0.6 ? Color.web("#2ecc71") : (hpRatio > 0.3 ? Color.web("#f39c12") : Color.web("#e74c3c"));
        gc.setFill(hpColor);
        gc.fillRect(barX, barY, barWidth * hpRatio, 4);

        // Efeitos de estado
        if (inimigo.isAbrandado()) {
            gc.setStroke(Color.web("#74b9ff", 0.5));
            gc.setLineWidth(1);
            gc.setLineDashes(2, 2);
            gc.strokeOval(px + cellSize * 0.1, py + cellSize * 0.1, cellSize * 0.8, cellSize * 0.8);
            gc.setLineDashes(null);
        }
        if (inimigo.isEnvenenado()) {
            gc.setFill(Color.web("#00b894", 0.15));
            gc.fillOval(px + cellSize * 0.15, py + cellSize * 0.15, cellSize * 0.7, cellSize * 0.7);
        }
    }

    private Color getCorBaseTorre(String nome) {
        return switch (nome) {
            case "Arqueiro" -> Color.web("#27ae60");
            case "Mago" -> Color.web("#8e44ad");
            case "Torre de Gelo" -> Color.web("#3498db");
            case "Canhão" -> Color.web("#e67e22");
            case "Torre Veneno" -> Color.web("#00b894");
            case "Campeão" -> Color.web("#e74c3c");
            default -> Color.web("#95a5a6");
        };
    }

    /**
     * Atualiza os elementos da UI (labels, botões).
     */
    private void atualizarUI() {
        lblOuro.setText(String.valueOf(jogo.getJogador().getOuro()));
        lblHp.setText(jogo.getJogador().getHpCastelo() + "/" + jogo.getJogador().getMaxHpCastelo());

        String waveText = modo.isEndless() ?
                "Wave " + jogo.getNumeroWaveAtual() + " (∞)" :
                "Wave " + jogo.getNumeroWaveAtual() + "/" + jogo.getTotalWaves();
        lblWave.setText(waveText);

        lblScore.setText(String.valueOf(jogo.getJogador().getPontuacao()));

        atualizarPainelHerois();
    }

    /**
     * Mostra o popup de vitória.
     */
    private void mostrarVitoria() {
        if (gameLoop != null) gameLoop.stop();

        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("popup-overlay");

        VBox popup = new VBox(15);
        popup.setAlignment(Pos.CENTER);
        popup.setPadding(new Insets(40));
        popup.setMaxWidth(450);
        popup.setMaxHeight(400);
        popup.getStyleClass().add("popup-content");

        Text icone = new Text("+");
        icone.setFont(Font.font("Segoe UI", FontWeight.BOLD, 48));
        icone.setFill(Color.web("#2F6B45"));

        Text titulo = new Text("VITÓRIA!");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        titulo.setFill(Color.web("#1F5A38"));

        Text score = new Text("Pontuação: " + jogo.getJogador().calcularPontuacaoFinal());
        score.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        score.setFill(Color.web("#8A5B18"));

        Text inimigos = new Text("Inimigos Eliminados: " + jogo.getJogador().getInimigosEliminados());
        inimigos.setFont(Font.font("Segoe UI", 16));
        inimigos.setFill(Color.web("#243328"));

        Text wavesText = new Text("Waves Completadas: " + jogo.getJogador().getWavesCompletadas());
        wavesText.setFont(Font.font("Segoe UI", 16));
        wavesText.setFill(Color.web("#243328"));

        HBox btns = new HBox(15);
        btns.setAlignment(Pos.CENTER);

        if (modo.isEndless()) {
            ScoreManager.guardarScore(app.getNomeJogador(), jogo.getJogador().getWavesCompletadas(),
                    jogo.getJogador().calcularPontuacaoFinal(), jogo.getTempoJogo());
        }

        if (modo instanceof ModoHistoria modoH) {
            modoH.completarNivelAtual();
            Button btnProximo = new Button("Próximo Nível →");
            btnProximo.getStyleClass().add("button-gold");
            btnProximo.setOnAction(e -> app.mostrarSelecaoNivel());
            btns.getChildren().add(btnProximo);
        }

        Button btnMenu = new Button("Menu Principal");
        btnMenu.getStyleClass().add("button-secondary");
        btnMenu.setOnAction(e -> app.mostrarMenuPrincipal());
        btns.getChildren().add(btnMenu);

        popup.getChildren().addAll(icone, titulo, score, inimigos, wavesText, btns);
        overlay.getChildren().add(popup);

        StackPane root = (StackPane) getScene().getRoot();
        root.getChildren().add(overlay);

        overlay.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(500), overlay);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /**
     * Mostra o popup de derrota.
     */
    private void mostrarDerrota() {
        if (gameLoop != null) gameLoop.stop();

        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("popup-overlay");

        VBox popup = new VBox(15);
        popup.setAlignment(Pos.CENTER);
        popup.setPadding(new Insets(40));
        popup.setMaxWidth(450);
        popup.setMaxHeight(400);
        popup.getStyleClass().add("popup-content");

        Text icone = new Text("X");
        icone.setFont(Font.font("Segoe UI", FontWeight.BOLD, 48));
        icone.setFill(Color.web("#FF6577"));

        Text titulo = new Text("DERROTA");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        titulo.setFill(Color.web("#FF6577"));
        titulo.setEffect(new Glow(0.5));

        Text wave = new Text("Perdeste na Wave " + jogo.getNumeroWaveAtual());
        wave.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        wave.setFill(Color.web("#E6EDF9"));

        Text score = new Text("Pontuação: " + jogo.getJogador().getPontuacao());
        score.setFont(Font.font("Segoe UI", 16));
        score.setFill(Color.web("#B5C3D8"));

        Text inimigosText = new Text("Inimigos Eliminados: " + jogo.getJogador().getInimigosEliminados());
        inimigosText.setFont(Font.font("Segoe UI", 16));
        inimigosText.setFill(Color.web("#B5C3D8"));

        if (modo.isEndless()) {
            ScoreManager.guardarScore(app.getNomeJogador(), jogo.getJogador().getWavesCompletadas(),
                    jogo.getJogador().calcularPontuacaoFinal(), jogo.getTempoJogo());
            Text savedText = new Text("Score guardado no ranking!");
            savedText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            savedText.setFill(Color.web("#35E0B7"));
            popup.getChildren().add(savedText);
        }

        HBox btns = new HBox(15);
        btns.setAlignment(Pos.CENTER);

        Button btnRepetir = new Button("Repetir");
        btnRepetir.getStyleClass().add("button-primary");
        btnRepetir.setOnAction(e -> app.mostrarEcraJogo(modo));

        Button btnMenu = new Button("Menu Principal");
        btnMenu.getStyleClass().add("button-secondary");
        btnMenu.setOnAction(e -> app.mostrarMenuPrincipal());

        btns.getChildren().addAll(btnRepetir, btnMenu);

        popup.getChildren().addAll(icone, titulo, wave, score, inimigosText, btns);
        overlay.getChildren().add(popup);

        StackPane root = (StackPane) getScene().getRoot();
        root.getChildren().add(overlay);

        overlay.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(500), overlay);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}
