package xyz.upperlevel.spigot.gui.script;

import com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.upperlevel.spigot.gui.SlimyGuis;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ScriptSystem {
    private final File classPath;
    private final ClassLoader loader;
    private final ScriptEngineManager engineManager;
    private Map<String, String> extensionsToEngineName;
    private Map<String, Script> scripts = new HashMap<>();

    public ScriptSystem(File classPath, File scriptEngineConfig) {
        this.classPath = classPath;
        try {
            loader = new URLClassLoader(new URL[]{classPath.toURI().toURL()}, getClass().getClassLoader());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Cannot find classpath: " + classPath);
        }
        engineManager = new ScriptEngineManager(loader);
        engineManager.put("Bukkit", Bukkit.getServer());
        try {
            ScriptEngine engine = engineManager.getEngineByName("js");
            engine.eval("Bukkit.getLogger().info(\"JS engine works!\")");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        reloadConfig(scriptEngineConfig);
    }

    public void reloadConfig(File configFile) {
        extensionsToEngineName = new HashMap<>();

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection section = config.getConfigurationSection("engines");
        for(Map.Entry<String, Object> obj : section.getValues(false).entrySet())
            extensionsToEngineName.put(obj.getKey(), obj.getValue().toString());
    }

    public boolean load(String id, Script script) throws ScriptException {
        return scripts.putIfAbsent(id, script) == null;
    }

    public Script load(String id, String script, String ext) throws ScriptException {
        final String engineName = extensionsToEngineName.get(ext);
        if(engineName == null)
            throw new IllegalArgumentException("Cannot find engine for \"" + ext + "\"");
        final ScriptEngine engine = engineManager.getEngineByName(engineName);
        if(engine == null)
            throw new IllegalStateException("Cannot find engine \"" + engineName + "\"");
        Script s = Script.of(engine, script);
        return load(id, s) ? s : null;
    }

    public Script load(File file) throws IOException, ScriptException {
        String fileName = file.getName();
        int lastDot =  fileName.lastIndexOf('.');
        final String id = fileName.substring(0, lastDot);
        final String ext = fileName.substring(lastDot + 1);
        return load(id, Files.toString(file, StandardCharsets.UTF_8), ext);
    }

    public void loadFolder(File folder) {
        if(!folder.isDirectory()) {
            SlimyGuis.logger().severe("Error: " + folder + " isn't a folder");
            return;
        }
        File[] files = folder.listFiles();
        if(files == null) {
            SlimyGuis.logger().severe("Error reading files in " + folder);
            return;
        }
        for(File file : files) {
            Script res;
            try {
                res = load(file);
            } catch (FileNotFoundException e) {
                SlimyGuis.logger().severe("Cannot find file " + e);
                continue;
            } catch (ScriptException e) {
                SlimyGuis.logger().log(Level.SEVERE, "Script error in file " + file.getName(), e);
                continue;
            } catch (Exception e) {
                SlimyGuis.logger().log(Level.SEVERE, "Unknown error while reading script " + file.getName(), e);
                continue;
            }
            if(res == null)
                SlimyGuis.logger().severe("Cannot load file " + file.getName() + ": id already used!");
            else
                SlimyGuis.logger().info("Loaded script " + file.getName() + " with " + res.getEngine().getClass().getSimpleName() + (res instanceof PrecompiledScript ? " (compiled)" : ""));
        }
    }

    public void clearScripts() {
        scripts.clear();
    }

    public Script get(String id) {
        return scripts.get(id);
    }
}
