package tudbut.mod.client.crimson.mods;

import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.utils.Module;

public class Prefix extends Module {
    {
        enabled = true;
    }
    
    public boolean displayOnClickGUI() {
        return false;
    }
    
    @Override
    public void onSubTick() {
    }
    
    @Override
    public void onEverySubTick() {
        enabled = true;
    }
    
    @Override
    public void onChat(String s, String[] args) {
        // Set the prefix
        CRIMSON.prefix = s;
    }
}
