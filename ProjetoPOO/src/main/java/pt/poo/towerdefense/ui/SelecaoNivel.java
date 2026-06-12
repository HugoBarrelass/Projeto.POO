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
import pt.poo.towerdefense.model.ModoHistoria;
import pt.poo.towerdefense.model.Nivel;

import java.util.List;

/**
 * Ecrã de seleção de nível para o Modo História.
 * Apresenta 6 botões em grelha 2×3, com indicação visual
 * de níveis desbloqueados, bloqueados e completados.
 *
 * Conceito POO: Herança — herda de VBox.
 * Conceito POO: Associação — usa ModoHistoria para aceder aos níveis.
 */
public class SelecaoNivel extends VBox {
    private final ModoHistoria modoHistoria;

    public SelecaoNivel(App app, ModoHistoria modoHistoria) {
        this.modoHistoria = modoHistoria;

        setAlignment(Pos.CENTER);
        setSpacing(25);
        setPadding(new Insets(40));
        getStyleClass().add("screen");

        // Título
        Text titulo = new Text("MODO HISTÓRIA");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        titulo.setFill(Color.web("#172018"));
        titulo.setEffect(new DropShadow(14, Color.web("#2F6B45", 0.12)));

        Text subtitulo = new Text("Seleciona o nível para jogar");
        subtitulo.setFont(Font.font("Segoe UI", 16));
        subtitulo.setFill(Color.web("#637166"));

        // Grelha 2×3
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);

        List<Nivel> niveis = modoHistoria.getNiveis();
        for (int i = 0; i < niveis.size(); i++) {
            Nivel nivel = niveis.get(i);
            VBox card = criarCardNivel(app, nivel);
            grid.add(card, i % 3, i / 3);
        }

        // Botão voltar
        Button btnVoltar = new Button("← Voltar ao Menu");
        btnVoltar.getStyleClass().add("button-secondary");
        btnVoltar.setOnAction(e -> app.mostrarMenuPrincipal());

        getChildren().addAll(
                titulo,
                subtitulo,
                grid,
                btnVoltar
        );

        // Animação
        setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private VBox criarCardNivel(App app, Nivel nivel) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(220);
        card.setPrefHeight(180);

        boolean desbloqueado = nivel.isDesbloqueado();
        boolean completado = nivel.isCompletado();

        // Ícone
        String icone = completado ? "OK" : (desbloqueado ? "ABERTO" : "FECHADO");
        Text iconeText = new Text(icone);
        iconeText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        iconeText.setFill(completado ? Color.web("#2F6B45") :
                (desbloqueado ? Color.web("#446B84") : Color.web("#8A948B")));

        // Número do nível
        Text numero = new Text("Nível " + nivel.getNumero());
        numero.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        numero.setFill(desbloqueado ? Color.web("#172018") : Color.web("#8A948B"));

        // Info do nível
        String info = nivel.getTamanhoMapa() + "×" + nivel.getTamanhoMapa() +
                " | " + nivel.getNumeroCaminhos() + " caminho" + (nivel.getNumeroCaminhos() > 1 ? "s" : "") +
                " | " + nivel.getNumeroWaves() + " waves";
        if (nivel.isEndless()) info += " + ∞";
        Text infoText = new Text(info);
        infoText.setFont(Font.font("Segoe UI", 11));
        infoText.setFill(Color.web("#637166"));
        infoText.setTextAlignment(TextAlignment.CENTER);

        // Estilo do card
        if (completado) {
            card.setStyle("-fx-background-color: #F7FAF6; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: rgba(47,107,69,0.34); -fx-border-width: 1; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(38,53,41,0.12), 18, 0, 0, 5);");
        } else if (desbloqueado) {
            card.setStyle("-fx-background-color: rgba(255,255,255,0.82); -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: rgba(68,107,132,0.22); -fx-border-width: 1; -fx-cursor: hand;");

            card.setOnMouseEntered(e -> {
                card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #446B84; -fx-border-width: 1; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(38,53,41,0.14), 20, 0, 0, 5);");
            });
            card.setOnMouseExited(e -> {
                card.setStyle("-fx-background-color: rgba(255,255,255,0.82); -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: rgba(68,107,132,0.22); -fx-border-width: 1; -fx-cursor: hand;");
            });
        } else {
            card.setStyle("-fx-background-color: rgba(255,255,255,0.50); -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: rgba(99,113,102,0.12); -fx-border-width: 1; -fx-opacity: 0.70;");
        }

        card.getChildren().addAll(iconeText, numero, infoText);

        // Clique para iniciar nível
        if (desbloqueado) {
            card.setOnMouseClicked(e -> {
                modoHistoria.selecionarNivel(nivel.getNumero());
                app.mostrarEcraJogo(modoHistoria);
            });
        }

        return card;
    }
}
