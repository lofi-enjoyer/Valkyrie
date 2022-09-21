package me.lofienjoyer.nublada.engine.scripting;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Script {

    private final Invocable invocable;

    public Script(String path) {
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("Nashorn");
        scriptEngine.put("directory", path);
        try {
            scriptEngine.eval(new FileReader(path + "/main.js"));
        } catch (ScriptException | FileNotFoundException e) {
            e.printStackTrace();
        }
        invocable = (Invocable) scriptEngine;
    }

    public void callFunction(String function, Object... params) {
        try {
            invocable.invokeFunction(function, params);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
