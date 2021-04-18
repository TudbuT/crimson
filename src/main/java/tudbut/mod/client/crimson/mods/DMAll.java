package tudbut.mod.client.crimson.mods;

import net.minecraft.client.network.NetworkPlayerInfo;
import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.utils.ChatUtils;
import tudbut.mod.client.crimson.utils.Module;
import tudbut.mod.client.crimson.utils.ThreadManager;

import java.util.Objects;

public class DMAll extends Module {
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
        ChatUtils.print("Sending...");
        
        // This would stop the game if it wasn't in a separate thread
        ThreadManager.run(() -> {
            // Loop through all players
            for (NetworkPlayerInfo info : Objects.requireNonNull(CRIMSON.mc.getConnection()).getPlayerInfoMap().toArray(new NetworkPlayerInfo[0])) {
                try {
                    // Send a DM to the player
                    CRIMSON.mc.player.sendChatMessage("/tell " + info.getGameProfile().getName() + " " + s);
                    // Notify the user
                    ChatUtils.print("Sent to " + info.getGameProfile().getName());
                    // I hate antispam
                    Thread.sleep(TPATools.getInstance().delay);
                }
                catch (Throwable ignore) { }
            }
            ChatUtils.print("Done!");
        });
    }
}
