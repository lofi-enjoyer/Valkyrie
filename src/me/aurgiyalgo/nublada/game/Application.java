package me.aurgiyalgo.nublada.game;

import me.aurgiyalgo.nublada.Nublada;

public class Application {

    public static void main(String[] args) {
        Nublada instance = new Nublada();
        instance.setCurrentScene(new WorldScene());
        instance.init();
        instance.dispose();
    }

}
