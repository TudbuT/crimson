package tudbut.mod.client.crimson.mods;

import tudbut.mod.client.crimson.gui.GuiCRIMSON;
import tudbut.mod.client.crimson.utils.Module;

public class ChatColor extends Module {
    static ChatColor instance;
    // Use "> " instead of ">"
    private boolean useSpace = false;
    
    public int prefix = 0;
    
    public static final String[][] prefixes = new String[][] {
            {"Green", ">"},
            {"Blue", "'"},
            {"Black", "#"},
            {"Gold", "$"},
            {"Red", "£"},
            {"Aqua", "^"},
            {"Yellow", "&"},
            {"Dark Blue", "\\"},
            {"Dark Red", "%"},
            {"Gray", "."}
    };
    public static boolean bold = false;
    public static boolean italic = false;
    public static boolean underline = false;
    public static String addition = "";
    
    {
        updateButtons();
    }
    
    public ChatColor() {
        instance = this;
    }
    
    public static ChatColor getInstance() {
        return instance;
    }
    
    // Return the correct string
    public String get() {
        return (enabled ? (useSpace ? (prefixes[prefix][1] + addition + " ") : (prefixes[prefix][1] + addition)) : "");
    }
    
    public void updateButtons() {
        subButtons.clear();
        subButtons.add(new GuiCRIMSON.Button("Add space: " + useSpace, text -> {
            useSpace = !useSpace;
            text.set("Add space: " + useSpace);
        }));
        subButtons.add(new GuiCRIMSON.Button("Color: " + prefixes[prefix][0], text -> {
            prefix++;
            if(prefix >= prefixes.length)
                prefix = 0;
            text.set("Color: " + prefixes[prefix][0]);
        }));
        subButtons.add(new GuiCRIMSON.Button("Bold: " + bold, text -> {
            bold = !bold;
            addition = "";
            if(bold)
                addition += "++";
            if(italic)
                addition += "**";
            if(underline)
                addition += "||";
            text.set("Bold: " + bold);
        }));
        subButtons.add(new GuiCRIMSON.Button("Italic: " + italic, text -> {
            italic = !italic;
            addition = "";
            if(bold)
                addition += "++";
            if(italic)
                addition += "**";
            if(underline)
                addition += "||";
            text.set("Italic: " + italic);
        }));
        subButtons.add(new GuiCRIMSON.Button("Underline: " + underline, text -> {
            underline = !underline;
            addition = "";
            if(bold)
                addition += "++";
            if(italic)
                addition += "**";
            if(underline)
                addition += "||";
            text.set("Underline: " + underline);
        }));
    }
    
    @Override
    public void onSubTick() {
    
    }
    
    @Override
    public void onChat(String s, String[] args) {
    
    }
    
    @Override
    public void updateConfig() {
        cfg.put("space", useSpace + "");
        cfg.put("char", prefix + "");
        cfg.put("bold", bold + "");
        cfg.put("italic", bold + "");
        cfg.put("underline", underline + "");
    }
    
    @Override
    public void loadConfig() {
        useSpace = Boolean.parseBoolean(cfg.get("space"));
        prefix = Integer.parseInt(cfg.get("char"));
        bold = Boolean.parseBoolean(cfg.get("bold"));
        italic = Boolean.parseBoolean(cfg.get("italic"));
        underline = Boolean.parseBoolean(cfg.get("underline"));
    
        addition = "";
        if(bold)
            addition += "++";
        if(italic)
            addition += "**";
        if(underline)
            addition += "||";
        updateButtons();
    }
    
    @Override
    public int danger() {
        return 1;
    }
}
