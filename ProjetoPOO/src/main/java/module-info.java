module pt.poo.towerdefense {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens pt.poo.towerdefense to javafx.fxml;
    opens pt.poo.towerdefense.ui to javafx.fxml;

    exports pt.poo.towerdefense;
    exports pt.poo.towerdefense.ui;
    exports pt.poo.towerdefense.model;
    exports pt.poo.towerdefense.model.enums;
    exports pt.poo.towerdefense.model.torre;
    exports pt.poo.towerdefense.model.inimigo;
    exports pt.poo.towerdefense.logic;
}
