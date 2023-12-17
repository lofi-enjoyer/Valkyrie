package me.lofienjoyer.valkyrie.game;

import me.lofienjoyer.valkyrie.Valkyrie;

public class Application {

    public static void main(String[] args) {
        Valkyrie instance = new Valkyrie();
        instance.init();
        instance.setCurrentScene(new WorldScene());
        instance.loop();
        instance.dispose();
    }

}
