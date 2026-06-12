package pt.poo.towerdefense.ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import pt.poo.towerdefense.App;

/**
 * Menu principal do jogo com 5 opções: Modo História, Modo Praticar,
 * Classificações, Instruções e Sair.
 * Herda de VBox (layout vertical do JavaFX) e usa animações de entrada.
 *
 * Conceito POO: Herança — herda de VBox (componente JavaFX).
 * Conceito POO: Associação — recebe a App no construtor para navegar entre ecrãs.
 */
public class MenuPrincipal extends VBox {

    public MenuPrincipal(App app) {
        setAlignment(Pos.CENTER);
        setSpacing(16);
        setPadding(new Insets(40));
        getStyleClass().add("screen");

        Text titulo = new Text("TOWER DEFENSE");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 42));
        titulo.setFill(Color.web("#172018"));

        DropShadow shadow = new DropShadow(14, Color.web("#2F6B45", 0.12));
        titulo.setEffect(shadow);

        Text bemVindo = new Text("Bem-vindo, " + app.getNomeJogador() + "!");
        bemVindo.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        bemVindo.setFill(Color.web("#637166"));

        // Separador
        Region separador = new Region();
        separador.setPrefHeight(20);

        // Botões do menu
        VBox botoesBox = new VBox(15);
        botoesBox.setAlignment(Pos.CENTER);
        botoesBox.setMaxWidth(350);
        botoesBox.setPadding(new Insets(20));
        botoesBox.getStyleClass().add("panel-dark");

        Button btnHistoria = criarBotaoMenu("Modo História", "Progride por 6 níveis épicos!");
        btnHistoria.setOnAction(e -> app.mostrarSelecaoNivel());

        Button btnPraticar = criarBotaoMenu("Modo Praticar", "Treina livremente!");
        btnPraticar.setOnAction(e -> app.mostrarSelecaoDificuldade());

        Button btnClassificacoes = criarBotaoMenu("Classificações", "Top 10 melhores jogadores");
        btnClassificacoes.setOnAction(e -> app.mostrarHighscores());

        Button btnInstrucoes = criarBotaoMenu("Instruções", "Como jogar Tower Defense");
        btnInstrucoes.setOnAction(e -> app.mostrarInstrucoes());

        Button btnSair = criarBotaoMenu("Sair", "Até à próxima, guerreiro!");
        btnSair.getStyleClass().remove("button-primary");
        btnSair.getStyleClass().addAll("button-secondary", "button-danger");
        btnSair.setOnAction(e -> app.getPrimaryStage().close());

        botoesBox.getChildren().addAll(btnHistoria, btnPraticar, btnClassificacoes, btnInstrucoes, btnSair);

        getChildren().addAll(
                titulo,
                bemVindo,
                separador,
                botoesBox
        );

        // Animação de entrada com cada botão a entrar com delay
        setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        for (int i = 0; i < botoesBox.getChildren().size(); i++) {
            var btn = botoesBox.getChildren().get(i);
            btn.setTranslateX(-50);
            btn.setOpacity(0);
            TranslateTransition slide = new TranslateTransition(Duration.millis(400), btn);
            slide.setFromX(-50);
            slide.setToX(0);
            slide.setDelay(Duration.millis(200 + i * 100));
            FadeTransition fade = new FadeTransition(Duration.millis(400), btn);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.millis(200 + i * 100));
            slide.play();
            fade.play();
        }
    }

    private Button criarBotaoMenu(String texto, String descricao) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("button-primary");
        btn.setMinWidth(350);
        btn.setMinHeight(50);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        // Tooltip com descrição
        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(descricao);
        tooltip.setFont(Font.font(13));
        javafx.scene.control.Tooltip.install(btn, tooltip);

        return btn;
    }
}
