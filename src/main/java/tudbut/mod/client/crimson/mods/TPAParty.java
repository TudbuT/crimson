package tudbut.mod.client.crimson.mods;

import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.utils.Module;

public class TPAParty extends Module {
    
    static TPAParty instance;
    
    public TPAParty() {
        instance = this;
    }
    
    public static TPAParty getInstance() {
        return instance;
    }
    
    @Override
    public void onSubTick() {
    
    }
    
    @Override
    public void onChat(String s, String[] args) {
    
    }
    
    @Override
    public boolean onServerChat(String s, String formatted) {
        if (s.contains("has requested to teleport to you.") && !s.startsWith("<")) {
            // Accept TPA requests
            CRIMSON.player.sendChatMessage("/tpaccept");
        }
        return false;
    }
    
    @Override
    public int danger() {
        return 4;
    }
}
