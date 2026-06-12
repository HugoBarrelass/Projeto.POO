package pt.poo.towerdefense.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import pt.poo.towerdefense.App;
import pt.poo.towerdefense.model.ModoPraticar;
import pt.poo.towerdefense.model.enums.Dificuldade;

/**
 * Ecrã de seleção de dificuldade para o Modo Praticar.
 * Apresenta 3 cards (Fácil, Médio, Difícil) com as informações
 * de cada configuração e um botão JOGAR que cria um ModoPraticar.
 *
 * Conceito POO: Herança — herda de VBox.
 * Conceito POO: Polimorfismo — ao clicar JOGAR, cria new ModoPraticar(dif)
 *               que é passado como ModoJogo ao Jogo (polimorfismo de subtipos).
 */
public class SelecaoDificuldade extends VBox {

    public SelecaoDificuldade(App app) {
        setAlignment(Pos.CENTER);
        setSpacing(30);
        setPadding(new Insets(40));
        getStyleClass().add("screen");

        Text titulo = new Text("MODO PRATICAR");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        titulo.setFill(Color.web("#172018"));
        titulo.setEffect(new DropShadow(14, Color.web("#2F6B45", 0.12)));

        Text subtitulo = new Text("Seleciona a dificuldade");
        subtitulo.setFont(Font.font("Segoe UI", 16));
        subtitulo.setFill(Color.web("#637166"));

        HBox cardsBox = new HBox(25);
        cardsBox.setAlignment(Pos.CENTER);
        cardsBox.getChildren().addAll(
                criarCard(app, Dificuldade.FACIL, "01", "#35E0B7"),
                criarCard(app, Dificuldade.MEDIO, "02", "#F5C451"),
                criarCard(app, Dificuldade.DIFICIL, "03", "#FF6577")
        );

        Button btnVoltar = new Button("\u2190 Voltar ao Menu");
        btnVoltar.getStyleClass().add("button-secondary");
        btnVoltar.setOnAction(e -> app.mostrarMenuPrincipal());

        getChildren().addAll(
                titulo,
                subtitulo,
                cardsBox,
                btnVoltar
        );

        setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private VBox criarCard(App app, Dificuldade dif, String icone, String cor) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setPrefWidth(280);
        card.setPrefHeight(300);
        String baseStyle = "-fx-background-color: rgba(255,255,255,0.82); " +
                "-fx-background-radius: 18; -fx-border-radius: 18; " +
                "-fx-border-color: " + cor + "44; -fx-border-width: 1; -fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(38,53,41,0.12), 24, 0.12, 0, 8);";
        card.setStyle(baseStyle);

        Text iconeText = new Text(icone);
        iconeText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        iconeText.setFill(Color.web(cor));

        Text nome = new Text(dif.getNome());
        nome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        nome.setFill(Color.web(cor));

        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.getChildren().addAll(
            criarInfoText("Mapa: " + dif.getTamanhoMapa() + "\u00D7" + dif.getTamanhoMapa()),
            criarInfoText("Caminhos: " + dif.getNumeroCaminhos()),
            criarInfoText("Waves: " + dif.getNumeroWaves()),
            criarInfoText("Ouro: " + dif.getOuroInicial()),
            criarInfoText("HP: " + dif.getHpCastelo())
        );

        Button btnJogar = new Button("JOGAR");
        btnJogar.getStyleClass().add("button-primary");
        btnJogar.setMinWidth(200);
        btnJogar.setOnAction(e -> app.mostrarEcraJogo(new ModoPraticar(dif)));

        card.getChildren().addAll(iconeText, nome, infoBox, btnJogar);

        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #FFFFFF; " +
                    "-fx-background-radius: 18; -fx-border-radius: 18; " +
                    "-fx-border-color: " + cor + "; -fx-border-width: 1; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(38,53,41,0.18), 24, 0, 0, 8);");
            card.setScaleX(1.03);
            card.setScaleY(1.03);
        });
        card.setOnMouseExited(e -> {
            card.setStyle(baseStyle);
            card.setScaleX(1);
            card.setScaleY(1);
        });

        return card;
    }

    private Text criarInfoText(String texto) {
        Text t = new Text(texto);
        t.setFont(Font.font("Segoe UI", 14));
        t.setFill(Color.web("#637166"));
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }
}
