package pt.poo.towerdefense;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pt.poo.towerdefense.model.ModoHistoria;
import pt.poo.towerdefense.ui.*;

/**
 * Ponto de entrada da aplicação JavaFX Tower Defense.
 * Estende Application (classe base do JavaFX) — o método start() é chamado
 * automaticamente pelo JavaFX quando a aplicação arranca.
 *
 * Usa o padrão Singleton (instância única via getInstance()) para permitir
 * que qualquer ecrã aceda à App e navegue entre ecrãs.
 *
 * Responsabilidades:
 * - Criar a janela principal (Stage) e a cena (Scene)
 * - Carregar o CSS global
 * - Gerir a navegação entre os vários ecrãs do jogo
 * - Manter o progresso do Modo História durante a sessão
 */
public class App extends Application {
    private static App instance;              // Referência Singleton — permite acesso global à App
    private Stage primaryStage;               // Janela principal do JavaFX
    private Scene scene;                      // Cena que contém toda a interface gráfica
    private StackPane root;                   // Container raiz — empilha os ecrãs
    private String nomeJogador;               // Nome inserido pelo jogador no ecrã inicial
    private final ModoHistoria progressoHistoria = new ModoHistoria(); // Progresso guardado do Modo História (composição)

    public static final int LARGURA = 1280;   // Largura padrão da janela em pixels
    public static final int ALTURA = 800;     // Altura padrão da janela em pixels

    /**
     * Método chamado automaticamente pelo JavaFX ao iniciar a aplicação.
     * Configura a janela, carrega o CSS e mostra o primeiro ecrã.
     * @param primaryStage a janela principal fornecida pelo JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        instance = this;                     // Guarda a instância Singleton
        this.primaryStage = primaryStage;    // Guarda referência à janela principal

        root = new StackPane();
        scene = new Scene(root, LARGURA, ALTURA);

        // Carregar CSS
        String css = getClass().getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Tower Defense");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(700);
        primaryStage.setResizable(true);

        // Mostrar ecrã de nome do jogador
        mostrarEcraNome();

        primaryStage.show();
    }

    /**
     * Mostra o ecrã para inserir o nome do jogador.
     */
    public void mostrarEcraNome() {
        root.getChildren().clear();
        root.getChildren().add(new EcraNome(this));
    }

    /**
     * Define o nome do jogador e avança para o menu principal.
     */
    public void setNomeJogador(String nome) {
        this.nomeJogador = nome;
        mostrarMenuPrincipal();
    }

    /**
     * Mostra o menu principal.
     */
    public void mostrarMenuPrincipal() {
        root.getChildren().clear();
        root.getChildren().add(new MenuPrincipal(this));
    }

    /**
     * Mostra a seleção de nível (Modo História).
     */
    public void mostrarSelecaoNivel() {
        root.getChildren().clear();
        root.getChildren().add(new SelecaoNivel(this, progressoHistoria));
    }

    /**
     * Mostra a seleção de dificuldade (Modo Praticar).
     */
    public void mostrarSelecaoDificuldade() {
        root.getChildren().clear();
        root.getChildren().add(new SelecaoDificuldade(this));
    }

    /**
     * Mostra o ecrã de jogo.
     */
    public void mostrarEcraJogo(pt.poo.towerdefense.model.ModoJogo modo) {
        root.getChildren().clear();
        root.getChildren().add(new EcraJogo(this, modo));
    }

    /**
     * Mostra o ecrã de instruções.
     */
    public void mostrarInstrucoes() {
        root.getChildren().clear();
        root.getChildren().add(new EcraInstrucoes(this));
    }

    /**
     * Mostra o ecrã de highscores.
     */
    public void mostrarHighscores() {
        root.getChildren().clear();
        root.getChildren().add(new EcraHighscores(this));
    }

    /** Retorna o nome do jogador inserido no ecrã inicial. */
    public String getNomeJogador() { return nomeJogador; }

    /** Retorna a janela principal do JavaFX (para fechar, redimensionar, etc.). */
    public Stage getPrimaryStage() { return primaryStage; }

    /** Retorna a instância Singleton da App (padrão de design). */
    public static App getInstance() { return instance; }

    /**
     * Método main — ponto de entrada da aplicação Java.
     * Chama launch() que internamente cria a instância de App e chama start().
     */
    public static void main(String[] args) {
        launch(args);   // Inicia o ciclo de vida do JavaFX
    }
}
