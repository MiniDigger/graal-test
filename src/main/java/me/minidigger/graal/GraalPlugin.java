package me.minidigger.graal;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class GraalPlugin extends JavaPlugin implements Listener {

    private Map<String, String> langs = new HashMap<String, String>() {
        {
            put("js", ".js");
            put("python", ".py");
            put("ruby", ".rb");
            //TODO add more langs
        }
    };

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String src = String.join(" ", args);
        Context polyglot = createContext(sender);

        String lang = label.replace("graal", "");
        if (!langs.containsKey(lang)) {
            sender.sendMessage(ChatColor.RED + "Language " + lang + " not found!");
            return true;
        }

        try {
            Value result;
            if (args[0].endsWith(langs.get(lang))) {
                try {
                    File file = new File(getDataFolder(), args[0]);
                    if (!file.exists()) {
                        sender.sendMessage(ChatColor.RED + "Could not file script file " + args[0]);
                        return true;
                    }
                    result = polyglot.eval(Source.newBuilder(lang, file).build());
                } catch (IOException e) {
                    e.printStackTrace();
                    return true;
                }
            } else {
                result = polyglot.eval(lang, src);
            }

            sender.sendMessage(result.asString());
        } catch (PolyglotException ex) {
            sender.sendMessage(ChatColor.RED + "Error: " + ex.getMessage());
            ex.printStackTrace();
        }

        return true;
    }

    private Context createContext(CommandSender sender) {
        Context context = Context.newBuilder(langs.keySet().toArray(new String[0])).allowHostAccess(true).build();

        context.getPolyglotBindings().putMember("sender", sender);
        context.getPolyglotBindings().putMember("plugin", this);

        return context;
    }

    public void registerEvent(String id, Class<? extends Event> eventClass, EventExecutor executor) {
        //TODO use id to unregister events eventually

        Bukkit.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.NORMAL, executor, this);
    }
}
