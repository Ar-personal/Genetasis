package main;

import engine.charts.PieChart;
import engine.graphics.Window;

public class Main {

    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new Game();
            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = true;
            opts.showFps = true;
            opts.compatibleProfile = true;
            opts.antialiasing = true;
            opts.showTriangles = false;
            opts.unlockFrameRate = false;
            opts.updateAmt = 10;
            GameEngine gameEng = new GameEngine("GAME", vSync, opts, gameLogic);
            gameEng.run();

        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}