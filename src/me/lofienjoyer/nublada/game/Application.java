package me.lofienjoyer.nublada.game;

import me.lofienjoyer.nublada.Nublada;

public class Application {

    public static void main(String[] args) {
        Nublada instance = new Nublada();
        instance.init();
        instance.setCurrentScene(new WorldScene());
        instance.loop();
        instance.dispose();
    }

}
