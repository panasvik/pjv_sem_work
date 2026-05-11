package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class HeroView {
    public Parent build(PlayerProfile profile) {
        HBox root = new HBox();
        Image heroImg = new Image( getClass().getResourceAsStream("/cz/fel/cvut/pjv/dgnmstr/view/HeroImg.png"));
        ImageView heroImgView = new ImageView(heroImg);
        heroImgView.setPreserveRatio(true);
        heroImgView.fitHeightProperty().bind(root.heightProperty());

        VBox right = new VBox(10);
        HBox.setHgrow(right, Priority.ALWAYS);
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> SceneManager.getInstance().activate(View.START_MENU));
        right.getChildren().add(backButton);

        root.getChildren().addAll(heroImgView, backButton);
        return root;
    }
}
