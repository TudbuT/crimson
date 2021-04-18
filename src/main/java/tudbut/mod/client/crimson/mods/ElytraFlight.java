package tudbut.mod.client.crimson.mods;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.utils.FlightBot;
import tudbut.mod.client.crimson.utils.Module;

public class ElytraFlight extends Module {
    boolean init;
    
    @Override
    public void onTick() {
        if(CRIMSON.mc.world == null) {
            init = false;
            return;
        }
        EntityPlayerSP player = CRIMSON.player;
    
        boolean blockMovement = FlightBot.tickBot();
        
        if(init) {
            if (CRIMSON.player == CRIMSON.mc.getRenderViewEntity()) {
                if (!blockMovement) {
                    Vec2f movementVec = player.movementInput.getMoveVector();
    
                    float f1 = MathHelper.sin(player.rotationYaw * 0.017453292F);
                    float f2 = MathHelper.cos(player.rotationYaw * 0.017453292F);
                    double x = movementVec.x * f2 - movementVec.y * f1;
                    double y = (player.movementInput.jump ? 1 : 0) + (player.movementInput.sneak ? -1 : 0);
                    double z = movementVec.y * f2 + movementVec.x * f1;
                    float d = (float) Math.sqrt(x * x + y * y + z * z);
    
                    if (d < 1) {
                        d = 1;
                    }
    
                    player.motionX = x / d;
                    player.motionY = y / d;
                    player.motionZ = z / d;
                }
            }
            else {
                player.motionX = 0;
                player.motionY = 0;
                player.motionZ = 0;
            }
            negateElytraFallMomentum(player);
            
        } else if(player.isElytraFlying()) {
            player.motionX = 0;
            player.motionY = 0;
            player.motionZ = 0;
            init = true;
    
            negateElytraFallMomentum(player);
        }
    
        if (!player.isElytraFlying()) {
            player.capabilities.isFlying = false;
            init = false;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Z) && CRIMSON.mc.currentScreen == null && init) {
            init = false;
            player.capabilities.isFlying = true;
        }
    }
    
    public void negateElytraFallMomentum(EntityPlayer player) {
        if (!player.isInWater()) {
            if (!player.isInLava()) {
                Vec3d vec3d = player.getLookVec();
                float f = player.rotationPitch * 0.017453292F;
                double d = vec3d.lengthVector();
                float f1 = MathHelper.cos(f);
                f1 = (float) ((double) f1 * (double) f1 * Math.min(1.0D, d / 0.4D));
                player.motionY -= -0.08D + (double) f1 * 0.06D;
            }
        }
    }
    
    @Override
    public void onDisable() {
    }
    
    @Override
    public void onChat(String s, String[] args) {
    }
    
    @Override
    public int danger() {
        return 2;
    }
}
