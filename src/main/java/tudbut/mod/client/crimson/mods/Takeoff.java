package tudbut.mod.client.crimson.mods;

import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.utils.ChatUtils;
import tudbut.mod.client.crimson.utils.FlightBot;
import tudbut.mod.client.crimson.utils.Module;
import tudbut.obj.Atomic;

public class Takeoff extends Module {
    
    boolean isTakingOff = false;
    
    @Override
    public boolean displayOnClickGUI() {
        return false;
    }
    
    @Override
    public void onEnable() {
        ChatUtils.print("Starting elytra...");
        isTakingOff = true;
        FlightBot.activate(new Atomic<>(CRIMSON.mc.player.getPositionVector().addVector(0, 4, 0)));
        ChatUtils.print("Bot started.");
    }
    
    @Override
    public void onTick() {
        if(!FlightBot.isFlying() && isTakingOff && CRIMSON.player.isElytraFlying()) {
            FlightBot.deactivate();
            isTakingOff = false;
            enabled = false;
            onDisable();
            ChatUtils.print("Elytra started.");
        }
    }
    
    @Override
    public void onChat(String s, String[] args) {
    }
}
