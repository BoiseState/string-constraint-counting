package edu.boisestate.cs;

public class Settings {

    public static final int DEFAULT_BOUNDING_LENGTH = 15;
    private boolean debug;
    private String graphFilePath;
    private int initialBoundingLength;

    public boolean getDebug() {
        return debug;
    }

    public String getGraphFilePath() {
        return graphFilePath;
    }

    public int getInitialBoundingLength() {
        return initialBoundingLength;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setGraphFilePath(String graphFilePath) {
        this.graphFilePath = graphFilePath;
    }

    public void setInitialBoundingLength(int initialBoundingLength) {
        this.initialBoundingLength = initialBoundingLength;
    }

    public Settings() {
        this.debug = false;
        this.initialBoundingLength = DEFAULT_BOUNDING_LENGTH;
        this.graphFilePath = "./graphs/beasties01.json";
    }
}
