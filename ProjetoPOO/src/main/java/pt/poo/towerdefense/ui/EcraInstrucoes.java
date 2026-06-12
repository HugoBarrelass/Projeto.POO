package pt.poo.towerdefense.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import pt.poo.towerdefense.App;

/**
 * Ecrã de instruções com 3 tabs: Regras, Heróis e Inimigos.
 * Usa TabPane (componente JavaFX) para organizar o conteúdo.
 *
 * Conceito POO: Herança — herda de VBox.
 * Usa métodos auxiliares privados para criar cada seção (encapsulamento).
 */
public class EcraInstrucoes extends VBox {

    public EcraInstrucoes(App app) {
        setAlignment(Pos.TOP_CENTER);
        setSpacing(20);
        setPadding(new Insets(30));
        getStyleClass().add("screen");

        Text titulo = new Text("INSTRU\u00C7\u00D5ES");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        titulo.setFill(Color.web("#172018"));
        titulo.setEffect(new DropShadow(14, Color.web("#2F6B45", 0.12)));

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabRegras = new Tab("Regras", criarConteudoRegras());
        tabRegras.setStyle("-fx-font-size: 14px;");
        Tab tabHerois = new Tab("Her\u00F3is", criarConteudoHerois());
        tabHerois.setStyle("-fx-font-size: 14px;");
        Tab tabInimigos = new Tab("Inimigos", criarConteudoInimigos());
        tabInimigos.setStyle("-fx-font-size: 14px;");

        tabPane.getTabs().addAll(tabRegras, tabHerois, tabInimigos);

        Button btnVoltar = new Button("\u2190 Voltar ao Menu");
        btnVoltar.getStyleClass().add("button-secondary");
        btnVoltar.setOnAction(e -> app.mostrarMenuPrincipal());

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getChildren().addAll(
                titulo,
                tabPane,
                btnVoltar
        );

        setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private ScrollPane criarConteudoRegras() {
        VBox conteudo = new VBox(15);
        conteudo.setPadding(new Insets(20));
        conteudo.setStyle("-fx-background-color: rgba(255,255,255,0.78); -fx-background-radius: 14;");

        conteudo.getChildren().addAll(
                criarTituloSecao("Como Jogar"),
                criarTexto("Tower Defense \u00E9 um jogo onde defendes o teu castelo colocando her\u00F3is (torres) num mapa em grelha."),
                criarTexto("Os inimigos percorrem caminhos predefinidos e tens de os eliminar antes que cheguem ao castelo."),
                criarTituloSecao("Regras Principais"),
                criarTexto("\u2022 Her\u00F3is s\u00F3 podem ser colocados em c\u00E9lulas livres (fora do caminho)."),
                criarTexto("\u2022 Cada her\u00F3i tem um custo em ouro. Ganhas ouro ao derrotar inimigos."),
                criarTexto("\u2022 Completar uma wave concede um pequeno b\u00F3nus para preparar a seguinte."),
                criarTexto("\u2022 Os her\u00F3is atacam automaticamente os inimigos dentro do seu alcance."),
                criarTexto("\u2022 Alguns inimigos atacam os her\u00F3is e podem destru\u00ED-los."),
                criarTexto("\u2022 Ao vender um her\u00F3i, recebes 50% do custo de volta."),
                criarTexto("\u2022 Se um her\u00F3i for destru\u00EDdo, recebes 30% de volta."),
                criarTexto("\u2022 As waves ficam progressivamente mais dif\u00EDceis."),
                criarTituloSecao("Condi\u00E7\u00F5es de Vit\u00F3ria e Derrota"),
                criarTexto("Vit\u00F3ria: sobreviver a todas as waves do n\u00EDvel."),
                criarTexto("Derrota: o HP do castelo chega a 0."),
                criarTituloSecao("Sistema de Pontua\u00E7\u00E3o"),
                criarTexto("O score \u00E9 guardado apenas no N\u00EDvel 6 (Endless) do Modo Hist\u00F3ria."),
                criarTexto("Ap\u00F3s as 15 waves base, o jogo continua com dificuldade crescente!"),
                criarTexto("Score = Recompensas dos inimigos eliminados + HP restante \u00D7 50 + Ouro restante")
        );

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }

    private ScrollPane criarConteudoHerois() {
        VBox conteudo = new VBox(15);
        conteudo.setPadding(new Insets(20));
        conteudo.setStyle("-fx-background-color: rgba(255,255,255,0.78); -fx-background-radius: 14;");

        String[][] herois = {
                {"ARQ", "Arqueiro", "20", "3", "65", "100", "R\u00E1pido", "Ataque consistente a alvo \u00FAnico."},
                {"MAG", "Mago", "40", "4", "115", "80", "Lento", "Dano em \u00E1rea (splash) at\u00E9 3 inimigos."},
                {"GEL", "Torre de Gelo", "8", "3", "100", "90", "M\u00E9dio", "Abranda at\u00E9 3 inimigos em 35%."},
                {"CAN", "Canh\u00E3o", "160", "2", "140", "120", "Lento", "Explos\u00E3o secund\u00E1ria parcial; dano aumentado contra blindados."},
                {"VEN", "Torre Veneno", "20", "3", "120", "100", "R\u00E1pido", "Aplica 10 de dano por segundo durante 3s."},
                {"CAM", "Campe\u00E3o", "120", "3", "250", "200", "M\u00E9dio", "Dano 2\u00D7 contra Boss. M\u00E1x 2 no mapa. S\u00F3 compr\u00E1vel quando o Boss aparece."}
        };

        for (String[] h : herois) {
            conteudo.getChildren().add(criarCardInfo(h[0], h[1],
                    "Dano: " + h[2] + " | Alcance: " + h[3] + " | Custo: " + h[4] + "g | HP: " + h[5] + " | Vel: " + h[6],
                    h[7]));
        }

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }

    private ScrollPane criarConteudoInimigos() {
        VBox conteudo = new VBox(15);
        conteudo.setPadding(new Insets(20));
        conteudo.setStyle("-fx-background-color: rgba(255,255,255,0.78); -fx-background-radius: 14;");

        String[][] inimigos = {
                {"NOR", "Normal", "130", "1.0", "5", "Sem habilidade especial."},
                {"RAP", "R\u00E1pido", "80", "2.0", "7", "Velocidade alta, dif\u00EDcil de acertar."},
                {"ARQ", "Arqueiro Inimigo", "100", "0.8", "10", "Ataca os her\u00F3is enquanto anda pelo caminho."},
                {"TAN", "Tanque", "400", "0.5", "20", "Armadura 20% (reduz dano recebido)."},
                {"FEI", "Feiticeiro", "90", "0.7", "14", "Abranda a velocidade de ataque dos her\u00F3is em 35% durante 3s."},
                {"BOS", "Boss", "1200", "0.3", "70", "Armadura 30%, ataca her\u00F3is, tira 3 HP ao castelo."}
        };

        for (String[] i : inimigos) {
            conteudo.getChildren().add(criarCardInfo(i[0], i[1],
                    "Vida: " + i[2] + " | Vel: " + i[3] + " | Recompensa: " + i[4] + "g",
                    i[5]));
        }

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }

    private Text criarTituloSecao(String texto) {
        Text t = new Text(texto);
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        t.setFill(Color.web("#2F6B45"));
        return t;
    }

    private Text criarTexto(String texto) {
        Text t = new Text(texto);
        t.setFont(Font.font("Segoe UI", 15));
        t.setFill(Color.web("#243328"));
        return t;
    }

    private VBox criarCardInfo(String codigo, String nome, String stats, String descricao) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.82); -fx-background-radius: 14; " +
                "-fx-border-radius: 14; -fx-border-color: rgba(48,86,63,0.14); -fx-border-width: 1;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label codigoBadge = new Label(codigo);
        codigoBadge.setAlignment(Pos.CENTER);
        codigoBadge.setMinWidth(46);
        codigoBadge.setPadding(new Insets(4, 8, 4, 8));
        codigoBadge.setStyle("-fx-background-color: #E5EFE5; -fx-background-radius: 999; " +
                "-fx-border-color: rgba(47,107,69,0.20); -fx-border-radius: 999; " +
                "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #2F6B45;");

        Text nomeText = new Text(nome);
        nomeText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        nomeText.setFill(Color.web("#172018"));

        header.getChildren().addAll(codigoBadge, nomeText);

        Label statsText = new Label(stats);
        statsText.setWrapText(true);
        statsText.setMaxWidth(Double.MAX_VALUE);
        statsText.setStyle("-fx-font-size: 12px; -fx-text-fill: #2F6B45;");

        Label descText = new Label(descricao);
        descText.setWrapText(true);
        descText.setMaxWidth(Double.MAX_VALUE);
        descText.setStyle("-fx-font-size: 13px; -fx-text-fill: #637166;");

        card.getChildren().addAll(header, statsText, descText);

        return card;
    }
}
