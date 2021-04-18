package tudbut.mod.client.crimson.mods;

import net.minecraft.entity.player.EntityPlayer;
import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.gui.GuiCRIMSON;
import tudbut.mod.client.crimson.utils.LSDRenderer;
import tudbut.mod.client.crimson.utils.Module;

import java.lang.reflect.Field;
import java.util.Objects;

public class LSD extends Module {
    public static LSD getInstance() {
        return CRIMSON.getModule(LSD.class);
    }
    
    int mode = 0x00;
    
    {
        try {
            subButtons.add(new GuiCRIMSON.Button("Mode: " + getMode(mode), text -> {
                mode++;
                if(mode > 0x0a)
                    mode = 0x00;
                try {
                    text.set("Mode: " + getMode(mode));
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }));
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    private String getMode(int mode) throws IllegalAccessException {
        Class<LSDRenderer> clazz = LSDRenderer.class;
        Field[] fields = clazz.getDeclaredFields();
        
        for (int i = 0; i < fields.length; i++) {
            if(fields[i].getInt(null) == mode && !fields[i].getName().equals("mode")) {
                return fields[i].getName();
            }
        }
        
        return null;
    }
    
    @Override
    public boolean displayOnClickGUI() {
        return true;
    }
    
    @Override
    public void onChat(String s, String[] args) {
    
    }
    
    @Override
    public boolean doStoreEnabled() {
        return false;
    }
    
    @Override
    public void onTick() {
        LSDRenderer.mode = mode;
    }
    
    public void onEnable() {
        if(CRIMSON.isIngame() && !Freecam.getInstance().enabled) {
            EntityPlayer player = new LSDRenderer(CRIMSON.player, CRIMSON.world);
            CRIMSON.world.spawnEntity(player);
            CRIMSON.mc.renderChunksMany = true;
            //CRIMSON.mc.skipRenderWorld = true;
            CRIMSON.mc.setRenderViewEntity(player);
        }
        else
            enabled = false;
    }
    
    @Override
    public int danger() {
        return 1;
    }
    
    @Override
    public void onDisable() {
        if(CRIMSON.isIngame()) {
            CRIMSON.world.removeEntity(Objects.requireNonNull(CRIMSON.mc.getRenderViewEntity()));
            CRIMSON.mc.setRenderViewEntity(CRIMSON.mc.player);
            CRIMSON.mc.renderChunksMany = true;
        }
    }
    
}
