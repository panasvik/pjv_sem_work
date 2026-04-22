package cz.fel.cvut.pjv.dgnmstr.view;

public enum View {
    START_MENU("start-menu-view.fxml"),
    MULTIPLAYER(""),
    SINGLEPLAYER("single-player-menu-view.fxml"),
    MAIN_MENU("menu-view.fxml"),
    LEVEL(""),
    PAUSE("");

    private final String relativePath;

    View(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public String toString() {
        return "View{" +
                "relativePath='" + relativePath + '\'' +
                '}';
    }
}
