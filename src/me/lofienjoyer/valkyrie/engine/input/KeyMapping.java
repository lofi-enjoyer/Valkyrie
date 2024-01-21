package me.lofienjoyer.valkyrie.engine.input;

import me.lofienjoyer.valkyrie.engine.config.Config;

public class KeyMapping {
    public static int MOVE_FORWARD;
    public static int MOVE_BACKWARDS;
    public static int MOVE_LEFT;
    public static int MOVE_RIGHT;
    public static int JUMP;
    public static int TOGGLE_WIREFRAME;
    public static int TOGGLE_DEBUG_MODE;
    public static int TOGGLE_VSYNC;
    public static int CALL_GC;
    public static void update() {
        var config = Config.getInstance();

        MOVE_FORWARD = config.get("keys.move_forward", Integer.class);
        MOVE_BACKWARDS = config.get("keys.move_backwards", Integer.class);
        MOVE_LEFT = config.get("keys.move_left", Integer.class);
        MOVE_RIGHT = config.get("keys.move_right", Integer.class);
        JUMP = config.get("keys.jump", Integer.class);
        TOGGLE_WIREFRAME = config.get("keys.toggle_wireframe", Integer.class);
        TOGGLE_DEBUG_MODE = config.get("keys.toggle_debug_mode", Integer.class);
        TOGGLE_VSYNC = config.get("keys.toggle_vsync", Integer.class);
        CALL_GC = config.get("keys.call_gc", Integer.class);
    }
}
