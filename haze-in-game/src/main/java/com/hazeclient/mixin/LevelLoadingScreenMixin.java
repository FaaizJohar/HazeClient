package com.hazeclient.mixin;

import com.hazeclient.render.HazeColors;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin extends Screen {

    // Removed broken progress provider
    protected LevelLoadingScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        HazeTheme theme = HazeTheme.get();
        
        // Solid background
        HazeRenderer.drawRect(context, 0, 0, width, height, theme.getSurfaceBase());
        
        // Brand logo
        HazeRenderer.drawCenteredScaledText(context, textRenderer, "HAZE", width / 2, height / 2 - 40, theme.getTextPrimary(), 2.5f);
        
        // Loading bar
        int barW = 200;
        int barH = 4;
        int barX = (width - barW) / 2;
        int barY = height / 2 + 10;
        
        // Fake progress since we removed the tracker
        float progress = 0.5f;
        
        HazeRenderer.drawRoundedRect(context, barX, barY, barW, barH, 2, theme.getSurfaceElevated());
        HazeRenderer.drawRoundedRect(context, barX, barY, (int)(barW * progress), barH, 2, theme.getAccentColor());
        
        // Percentage text
        String text = String.format("Loading World... %d%%", (int)(progress * 100));
        HazeRenderer.drawCenteredText(context, textRenderer, text, width / 2, barY + 12, theme.getTextSecondary());
        
        ci.cancel(); // Skip vanilla rendering
    }
}
