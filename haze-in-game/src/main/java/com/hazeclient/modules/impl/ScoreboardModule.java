package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.settings.BooleanSetting;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScoreboardModule extends Module {

    private final BooleanSetting removeNumbers;
    private final BooleanSetting showShadow;

    public ScoreboardModule() {
        super("Scoreboard", "Custom stylized scoreboard", Category.HUD);
        removeNumbers = addSetting(new BooleanSetting("Remove Numbers", "Hide the red numbers on the right", true));
        showShadow = addSetting(new BooleanSetting("Show Shadow", "Draw drop shadow behind scoreboard", true));
        
        setBgStyle(BackgroundStyle.ROUNDED);
        setBgOpacity(0.6f);
        setSize(140, 100);
    }

    @Override
    public void render(DrawContext context, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean inEditor = mc.currentScreen instanceof com.hazeclient.gui.HUDEditor;
        
        Scoreboard scoreboard = mc.world != null ? mc.world.getScoreboard() : null;
        ScoreboardObjective objective = null;
        if (scoreboard != null) {
            objective = scoreboard.getObjectiveForSlot(1); // Sidebar slot
        }
        
        List<String> lines = new ArrayList<>();
        String title = "Scoreboard";
        
        if (inEditor && objective == null) {
            // Mock scoreboard
            title = "§e§lHaze Server";
            lines.add(" ");
            lines.add("Rank: §cAdmin");
            lines.add("Coins: §612,450");
            lines.add("Kills: §a42");
            lines.add("  ");
            lines.add("§7play.haze.net");
        } else if (objective != null) {
            title = objective.getDisplayName().getString();
            Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(objective);
            // Sort by score (Minecraft does this internally, we simulate logic here for raw draw)
            List<ScoreboardPlayerScore> sorted = new ArrayList<>(scores);
            sorted.sort((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
            
            for (int i = 0; i < Math.min(sorted.size(), 15); i++) {
                ScoreboardPlayerScore score = sorted.get(i);
                String playerName = score.getPlayerName(); // In reality we'd need team prefix/suffix
                
                if (removeNumbers.get()) {
                    lines.add(playerName);
                } else {
                    lines.add(playerName + " §c" + score.getScore());
                }
            }
        }
        
        if (lines.isEmpty()) {
            setSize(0, 0);
            return;
        }
        
        HazeTheme theme = HazeTheme.get();
        
        // Calculate size
        int maxWidth = mc.textRenderer.getWidth(title);
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, mc.textRenderer.getWidth(line));
        }
        maxWidth += 16;
        int height = 24 + (lines.size() * mc.textRenderer.fontHeight) + 8;
        
        setSize(maxWidth, height);
        
        // Optional outer shadow
        if (showShadow.get()) {
            HazeRenderer.drawShadow(context, 0, 0, maxWidth, height, 4, 0x000000, 6, 0.4f);
        }
        
        // Background is handled by Module's base render logic since setBgStyle is ROUNDED
        // We just draw the text
        
        // Title
        HazeRenderer.drawCenteredText(context, mc.textRenderer, title, maxWidth / 2, 8, theme.getTextPrimary());
        HazeRenderer.drawHorizontalSeparator(context, 4, 20, maxWidth - 8, theme.getCardBorder());
        
        // Lines
        int y = 26;
        for (String line : lines) {
            HazeRenderer.drawText(context, mc.textRenderer, line, 8, y, theme.getTextPrimary(), 1f);
            y += mc.textRenderer.fontHeight;
        }
    }
}
