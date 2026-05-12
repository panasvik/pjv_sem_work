package cz.fel.cvut.pjv.dgnmstr.view;

public enum View {
    START_MENU("start-menu-view.fxml"),
    LEVEL("level-view.fxml"),
    HERO;
    private final String relativePath;

    View(String relativePath) {
        this.relativePath = relativePath;
    }

    View(){
        this.relativePath = null;
    }

    public String getRelativePath() {
        return relativePath;
    }

}
