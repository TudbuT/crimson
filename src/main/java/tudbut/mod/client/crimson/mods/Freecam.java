package tudbut.mod.client.crimson.mods;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.utils.FreecamPlayer;
import tudbut.mod.client.crimson.utils.Module;

import java.util.Objects;

import static tudbut.mod.client.crimson.utils.Tesselator.*;

public class Freecam extends Module {
    
    public static Freecam getInstance() {
        return CRIMSON.getModule(Freecam.class);
    }
    
    GameType type;
    
    @Override
    public boolean displayOnClickGUI() {
        return true;
    }
    
    @Override
    public boolean doStoreEnabled() {
        return false;
    }
    
    public void onEnable() {
        if(CRIMSON.isIngame() && !LSD.getInstance().enabled && CRIMSON.mc.getRenderViewEntity() == CRIMSON.player) {
            EntityPlayer player = new FreecamPlayer(CRIMSON.player, CRIMSON.world);
            CRIMSON.world.spawnEntity(player);
            type = CRIMSON.mc.playerController.getCurrentGameType();
            //CRIMSON.mc.playerController.setGameType(GameType.SPECTATOR);
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
            //CRIMSON.mc.playerController.setGameType(type);
        }
        CRIMSON.mc.setRenderViewEntity(CRIMSON.mc.player);
        CRIMSON.mc.gameSettings.thirdPersonView = 0;
        CRIMSON.mc.renderChunksMany = true;
    }
    
    @Override
    public void onChat(String s, String[] args) {
    
    }
    
    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
    
        if(CRIMSON.isIngame() && enabled) {
            Entity main = CRIMSON.player;
            Entity e = CRIMSON.mc.getRenderViewEntity();
            Vec3d p = e.getPositionEyes(event.getPartialTicks()).addVector(0, -e.getEyeHeight(), 0);
            Vec3d pos = main.getPositionVector();
            float entityHalfed = main.width / 2 + 0.01f;
            float entityHeight = main.height + 0.01f;
            
            ready();
            translate(-p.x, -p.y, -p.z);
            color(0x80ff0000);
            depth(false);
            begin(GL11.GL_QUADS);
            
            // bottom
            put(pos.x - entityHalfed, pos.y - 0.01, pos.z + entityHalfed);
            put(pos.x + entityHalfed, pos.y - 0.01, pos.z + entityHalfed);
            put(pos.x + entityHalfed, pos.y - 0.01, pos.z - entityHalfed);
            put(pos.x - entityHalfed, pos.y - 0.01, pos.z - entityHalfed);
    
            next();
            
            // top
            put(pos.x - entityHalfed, pos.y + entityHeight, pos.z + entityHalfed);
            put(pos.x + entityHalfed, pos.y + entityHeight, pos.z + entityHalfed);
            put(pos.x + entityHalfed, pos.y + entityHeight, pos.z - entityHalfed);
            put(pos.x - entityHalfed, pos.y + entityHeight, pos.z - entityHalfed);
    
            next();
    
            // z -
            put(pos.x - entityHalfed, pos.y + entityHeight, pos.z - entityHalfed);
            put(pos.x + entityHalfed, pos.y + entityHeight, pos.z - entityHalfed);
            put(pos.x + entityHalfed, pos.y - 0.01, pos.z - entityHalfed);
            put(pos.x - entityHalfed, pos.y - 0.01, pos.z - entityHalfed);
    
            next();
    
            // z +
            put(pos.x - entityHalfed, pos.y + entityHeight, pos.z + entityHalfed);
            put(pos.x + entityHalfed, pos.y + entityHeight, pos.z + entityHalfed);
            put(pos.x + entityHalfed, pos.y - 0.01, pos.z + entityHalfed);
            put(pos.x - entityHalfed, pos.y - 0.01, pos.z + entityHalfed);
    
            next();
    
            // x -
            put(pos.x - entityHalfed, pos.y + entityHeight, pos.z - entityHalfed);
            put(pos.x - entityHalfed, pos.y + entityHeight, pos.z + entityHalfed);
            put(pos.x - entityHalfed, pos.y - 0.01, pos.z + entityHalfed);
            put(pos.x - entityHalfed, pos.y - 0.01, pos.z - entityHalfed);
    
            next();
    
            // y +
            put(pos.x + entityHalfed, pos.y + entityHeight, pos.z - entityHalfed);
            put(pos.x + entityHalfed, pos.y + entityHeight, pos.z + entityHalfed);
            put(pos.x + entityHalfed, pos.y - 0.01, pos.z + entityHalfed);
            put(pos.x + entityHalfed, pos.y - 0.01, pos.z - entityHalfed);
            
            end();
        }
    }
}
