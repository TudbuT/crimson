package tudbut.mod.client.crimson.events;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import tudbut.mod.client.crimson.CRIMSON;

import java.util.ArrayList;
import java.util.List;

public class ParticleLoop {
    
    // The particles to be rendered
    static List<Particle> particleMap = new ArrayList<>();
    
    // Add a particle to the particleMap
    public static void register(Particle particle) {
        particleMap.add(particle);
    }
    
    // Render the particles
    public static void run() {
        // Get particles as array (bugfix for crash)
        Particle[] particles = particleMap.toArray(new Particle[0]);
        
        // Render
        for (int i = 0; i < particles.length; i++) {
            // Only render if the particle asks to be rendered, otherwise remove
            if (particles[i].summon()) {
                // Spawn it
                Vec3d pos = particles[i].getPosition();
                if (CRIMSON.mc.world != null)
                    CRIMSON.mc.world.spawnParticle(particles[i].getType(), true, pos.x, pos.y, pos.z, 0, 0, 0);
            } else
                particleMap.remove(particles[i]);
        }
    }
    
    public interface Particle {
        // Should it be spawned?
        boolean summon();
        
        EnumParticleTypes getType();
        
        Vec3d getPosition();
    }
}
