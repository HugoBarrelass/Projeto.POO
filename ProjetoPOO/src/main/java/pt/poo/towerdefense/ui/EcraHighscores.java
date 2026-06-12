package pt.poo.towerdefense.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import pt.poo.towerdefense.App;
import pt.poo.towerdefense.logic.ScoreManager;
import pt.poo.towerdefense.logic.ScoreManager.ScoreEntry;

import java.util.List;

/**
 * Ecrã de classificações — Top 10 highscores do Nível 6 (Endless).
 * Lê os scores do ficheiro via ScoreManager (delegação) e apresenta
 * numa tabela com cabeçalho e medalhas para os 3 primeiros.
 *
 * Conceito POO: Herança — herda de VBox.
 * Conceito POO: Delegação — usa ScoreManager.lerScores() para obter os dados.
 */
public class EcraHighscores extends VBox {

    public EcraHighscores(App app) {
        setAlignment(Pos.TOP_CENTER);
        setSpacing(25);
        setPadding(new Insets(40));
        getStyleClass().add("screen");

        Text titulo = new Text("CLASSIFICA\u00C7\u00D5ES");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        titulo.setFill(Color.web("#172018"));
        titulo.setEffect(new DropShadow(14, Color.web("#2F6B45", 0.12)));

        Text subtitulo = new Text("Top 10 dos melhores jogadores");
        subtitulo.setFont(Font.font("Segoe UI", 16));
        subtitulo.setFill(Color.web("#637166"));

        VBox criteriosBox = criarCriteriosBox();

        List<ScoreEntry> scores = ScoreManager.lerScores();

        VBox tabelaBox = new VBox(0);
        tabelaBox.setAlignment(Pos.CENTER);
        tabelaBox.setMaxWidth(700);

        HBox header = criarLinha("#", "JOGADOR", "WAVES", "SCORE", "TEMPO", "DATA", true);
        header.setStyle("-fx-background-color: rgba(255,255,255,0.86); -fx-background-radius: 14 14 0 0; " +
                "-fx-border-radius: 14 14 0 0; -fx-border-color: rgba(48,86,63,0.18); -fx-border-width: 1;");
        tabelaBox.getChildren().add(header);

        if (scores.isEmpty()) {
            VBox emptyBox = new VBox(15);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            emptyBox.setStyle("-fx-background-color: rgba(255,255,255,0.72); -fx-background-radius: 0 0 14 14; " +
                    "-fx-border-color: rgba(48,86,63,0.14); -fx-border-width: 0 1 1 1; -fx-border-radius: 0 0 14 14;");

            Text emptyIcon = new Text("--");
            emptyIcon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
            emptyIcon.setFill(Color.web("#2F6B45"));

            Text emptyText = new Text("Ainda n\u00E3o h\u00E1 classifica\u00E7\u00F5es!\nCompleta o N\u00EDvel 6 para aparecer aqui.");
            emptyText.setFont(Font.font("Segoe UI", 16));
            emptyText.setFill(Color.web("#637166"));
            emptyText.setTextAlignment(TextAlignment.CENTER);

            emptyBox.getChildren().addAll(emptyIcon, emptyText);
            tabelaBox.getChildren().add(emptyBox);
        } else {
            for (int i = 0; i < scores.size(); i++) {
                ScoreEntry score = scores.get(i);
                String posicao = getMedalha(i + 1);

                HBox linha = criarLinha(posicao, score.getNome(),
                        String.valueOf(score.getWavesSobrevividas()),
                        String.valueOf(score.getPontuacao()),
                        score.getTempoJogo(),
                        score.getData(),
                        false);

                String bgColor = i % 2 == 0 ? "rgba(255,255,255,0.70)" : "rgba(247,250,246,0.78)";
                String borderRadius = i == scores.size() - 1 ? "0 0 14 14" : "0";
                linha.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: " + borderRadius + "; " +
                        "-fx-border-color: rgba(48,86,63,0.12); -fx-border-width: 0 1 " + (i == scores.size() - 1 ? "1" : "0") + " 1; " +
                        "-fx-border-radius: " + borderRadius + ";");

                if (i < 3) {
                    String glowColor = switch (i) {
                        case 0 -> "rgba(184,135,53,0.14)";
                        case 1 -> "rgba(68,107,132,0.12)";
                        case 2 -> "rgba(47,107,69,0.10)";
                        default -> "transparent";
                    };
                    linha.setStyle(linha.getStyle() + " -fx-background-color: " + glowColor + ";");
                }

                tabelaBox.getChildren().add(linha);
            }
        }

        Button btnVoltar = new Button("\u2190 Voltar ao Menu");
        btnVoltar.getStyleClass().add("button-secondary");
        btnVoltar.setOnAction(e -> app.mostrarMenuPrincipal());

        getChildren().addAll(
                titulo,
                subtitulo,
                criteriosBox,
                tabelaBox,
                btnVoltar
        );

        setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private VBox criarCriteriosBox() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(700);
        box.setPadding(new Insets(16, 20, 16, 20));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.72); -fx-background-radius: 16; " +
                "-fx-border-radius: 16; -fx-border-color: rgba(48,86,63,0.14); -fx-border-width: 1;");

        Text titulo = new Text("Critérios para entrar na tabela");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        titulo.setFill(Color.web("#172018"));

        Label entrada = criarCriterio("Conta apenas o Nível 6 do Modo História, em modo Endless.");
        Label ordem = criarCriterio("A ordem é: mais waves sobrevividas primeiro; em empate, maior pontuação.");
        Label score = criarCriterio("Pontuação final = recompensas dos inimigos + HP restante x 50 + ouro restante.");

        box.getChildren().addAll(titulo, entrada, ordem, score);
        return box;
    }

    private Label criarCriterio(String texto) {
        Label label = new Label("- " + texto);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #637166;");
        return label;
    }

    private HBox criarLinha(String pos, String nome, String waves, String score, String tempo, String data, boolean isHeader) {
        HBox linha = new HBox();
        linha.setAlignment(Pos.CENTER);
        linha.setPadding(new Insets(12, 15, 12, 15));

        double[] larguras = {50, 180, 80, 100, 80, 130};
        String[] textos = {pos, nome, waves, score, tempo, data};

        for (int i = 0; i < textos.length; i++) {
            Text text = new Text(textos[i]);
            text.setFont(Font.font("Segoe UI", isHeader ? FontWeight.BOLD : FontWeight.NORMAL, isHeader ? 14 : 15));
            text.setFill(isHeader ? Color.web("#2F6B45") : Color.web("#172018"));

            StackPane cell = new StackPane(text);
            cell.setPrefWidth(larguras[i]);
            cell.setAlignment(Pos.CENTER);
            linha.getChildren().add(cell);
        }

        return linha;
    }

    private String getMedalha(int posicao) {
        return switch (posicao) {
            case 1 -> "#1";
            case 2 -> "#2";
            case 3 -> "#3";
            default -> "#" + posicao;
        };
    }
}
