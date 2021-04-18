package tudbut.mod.client.crimson.mods;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import tudbut.mod.client.crimson.CRIMSON;
import tudbut.mod.client.crimson.utils.Module;

public class Bright extends Module
{
    @Override
    public void onChat(String s, String[] args) {
    
    }
    
    public void onEveryTick() {
        if (enabled) {
            PotionEffect p;
            CRIMSON.player.addPotionEffect(p = new PotionEffect(
                    MobEffects.NIGHT_VISION,
                    1000,
                    127,
                    true,
                    false
            ));
            p.setPotionDurationMax(true);
        } else
           CRIMSON.player.removeActivePotionEffect(MobEffects.NIGHT_VISION);
    
    }
}
