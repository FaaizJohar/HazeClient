package com.hazeclient.render.particles;

import com.hazeclient.render.HazeRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleEngine {

    private static class Particle {
        float x, y;
        float vx, vy;
        float size;
        float alpha;
        
        Particle(float x, float y, float vx, float vy, float size, float alpha) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.size = size; this.alpha = alpha;
        }
    }

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private int width, height;
    
    private final int MAX_PARTICLES = 100;
    private final int CONNECT_DISTANCE = 80;

    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        particles.clear();
        for (int i = 0; i < MAX_PARTICLES; i++) {
            spawnParticle();
        }
    }

    private void spawnParticle() {
        float x = random.nextFloat() * width;
        float y = random.nextFloat() * height;
        float vx = (random.nextFloat() - 0.5f) * 1.5f;
        float vy = (random.nextFloat() - 0.5f) * 1.5f;
        float size = random.nextFloat() * 1.5f + 1.0f;
        float alpha = random.nextFloat() * 0.5f + 0.3f;
        particles.add(new Particle(x, y, vx, vy, size, alpha));
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Update & Render
        for (Particle p : particles) {
            p.x += p.vx;
            p.y += p.vy;
            
            // Mouse repel
            float dx = mouseX - p.x;
            float dy = mouseY - p.y;
            float distToMouse = (float) Math.sqrt(dx * dx + dy * dy);
            if (distToMouse < 100) {
                float force = (100 - distToMouse) / 500f;
                p.x -= dx * force;
                p.y -= dy * force;
            }
            
            // Bounds check
            if (p.x < 0 || p.x > width) p.vx *= -1;
            if (p.y < 0 || p.y > height) p.vy *= -1;
            
            // Render point
            int color = HazeRenderer.withAlpha(0xFFFFFFFF, p.alpha);
            HazeRenderer.drawCircle(context, (int) p.x, (int) p.y, (int) p.size, color);
        }
        
        // Draw connections
        for (int i = 0; i < particles.size(); i++) {
            Particle p1 = particles.get(i);
            for (int j = i + 1; j < particles.size(); j++) {
                Particle p2 = particles.get(j);
                float dx = p1.x - p2.x;
                float dy = p1.y - p2.y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                
                if (dist < CONNECT_DISTANCE) {
                    float opacity = 1.0f - (dist / CONNECT_DISTANCE);
                    // HazeRenderer.drawLine(context, p1.x, p1.y, p2.x, p2.y, lineColor);
                }
            }
        }
    }
}
