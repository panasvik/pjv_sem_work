package cz.fel.cvut.pjv.dgnmstr.view;

public enum View {
    START_MENU("start-menu-view.fxml"),
    LEVEL("level-view.fxml");
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
