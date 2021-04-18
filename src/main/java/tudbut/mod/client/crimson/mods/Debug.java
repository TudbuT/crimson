package tudbut.mod.client.crimson.mods;

import tudbut.mod.client.crimson.utils.Module;

// Placeholder module
public class Debug extends Module {
    static Debug instance;
    
    public Debug() {
        instance = this;
    }
    
    public static Debug getInstance() {
        return instance;
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
}
