package pt.poo.towerdefense.ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import pt.poo.towerdefense.App;

/**
 * Ecrã inicial para inserir o nome do jogador.
 * É o primeiro ecrã que aparece ao abrir o jogo.
 * O jogador só pode continuar se inserir um nome válido (não vazio).
 *
 * Conceito POO: Herança — herda de VBox (layout vertical JavaFX).
 * Usa listeners (programação orientada a eventos) para habilitar/desabilitar o botão.
 */
public class EcraNome extends VBox {

    public EcraNome(App app) {
        setAlignment(Pos.CENTER);
        setSpacing(22);
        setPadding(new Insets(50));
        getStyleClass().add("screen");

        // Título
        Text titulo = new Text("TOWER DEFENSE");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 48));
        titulo.setFill(Color.web("#172018"));
        titulo.setEffect(new DropShadow(18, Color.web("#2F6B45", 0.12)));

        // Campo de nome
        VBox nomeBox = new VBox(10);
        nomeBox.setAlignment(Pos.CENTER);
        nomeBox.setMaxWidth(400);
        nomeBox.setPadding(new Insets(28));
        nomeBox.getStyleClass().add("panel-dark");

        Text nomeLabel = new Text("Insere o teu nome, guerreiro:");
        nomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        nomeLabel.setFill(Color.web("#243328"));

        TextField nomeField = new TextField();
        nomeField.setPromptText("O teu nome...");
        nomeField.setMaxWidth(350);
        nomeField.getStyleClass().add("text-field");

        Button btnContinuar = new Button("ENTRAR NO JOGO");
        btnContinuar.getStyleClass().add("button-gold");
        btnContinuar.setMinWidth(350);
        btnContinuar.setDisable(true);

        // Habilitar botão quando há texto
        nomeField.textProperty().addListener((obs, old, novo) -> {
            btnContinuar.setDisable(novo.trim().isEmpty());
        });

        // Enter para continuar
        nomeField.setOnAction(e -> {
            if (!nomeField.getText().trim().isEmpty()) {
                app.setNomeJogador(nomeField.getText().trim());
            }
        });

        btnContinuar.setOnAction(e -> {
            if (!nomeField.getText().trim().isEmpty()) {
                app.setNomeJogador(nomeField.getText().trim());
            }
        });

        nomeBox.getChildren().addAll(nomeLabel, nomeField, btnContinuar);

        getChildren().addAll(
                titulo,
                nomeBox
        );

        // Animação de entrada
        setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
