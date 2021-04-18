package tudbut.mod.client.crimson.mods;

import org.lwjgl.input.Keyboard;
import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.utils.ChatUtils;
import tudbut.mod.client.crimson.utils.Module;

public class Bind extends Module {
    @Override
    public boolean defaultEnabled() {
        return true;
    }
    
    @Override
    public boolean displayOnClickGUI() {
        return false;
    }
    
    @Override
    public void onSubTick() {
    
    }
    
    @Override
    public void onChat(String s, String[] args) {
    
    }
    
    @Override
    public void onEveryChat(String s, String[] args) {
        if(s.equals("help")) {
            
            ChatUtils.print("§a§lBinds");
            for (int i = 0; i < CRIMSON.modules.length; i++) {
                ChatUtils.print("§aModule: " + CRIMSON.modules[i].toString());
                if(CRIMSON.modules[i].key.key != null)
                    ChatUtils.print("State: " + Keyboard.getKeyName(CRIMSON.modules[i].key.key));
                for (String kb : CRIMSON.modules[i].customKeyBinds.keySet()) {
                    if(CRIMSON.modules[i].customKeyBinds.get(kb).key != null)
                        ChatUtils.print("Function " + kb + ": " + Keyboard.getKeyName(CRIMSON.modules[i].customKeyBinds.get(kb).key));
                }
            }
            
            return;
        }
        
        for (int i = 0; i < CRIMSON.modules.length; i++) {
            if (args[0].equalsIgnoreCase(CRIMSON.modules[i].getClass().getSimpleName().toLowerCase())) {
                if(args.length == 2) {
                    int key = Keyboard.getKeyIndex(args[1].toUpperCase());
                    if(key == Keyboard.KEY_NONE) {
                        CRIMSON.modules[i].customKeyBinds.get(args[1]).key = null;
                    }
                    else
                        CRIMSON.modules[i].key.key = key;
                }
                else if (args.length == 3) {
                    if (CRIMSON.modules[i].customKeyBinds.containsKey(args[1])) {
                        CRIMSON.modules[i].customKeyBinds.get(args[1]).key = Keyboard.getKeyIndex(args[2].toUpperCase());
                    }
                }
                else
                    CRIMSON.modules[i].key.key = null;
            }
        }
    }
}
