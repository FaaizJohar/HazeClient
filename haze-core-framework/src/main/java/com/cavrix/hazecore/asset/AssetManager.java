package com.cavrix.hazecore.asset;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssetManager {
    // Thread pool for async asset loading
    private final ExecutorService assetLoaderPool = Executors.newFixedThreadPool(2);

    public void initialize() {
        System.out.println("[HazeCore] Initializing Asset Manager...");
    }

    public void loadAssetAsync(String path, Runnable callback) {
        assetLoaderPool.submit(() -> {
            // Load asset bytes/image
            System.out.println("[HazeCore] Loaded asset async: " + path);
            if (callback != null) {
                callback.run();
            }
        });
    }

    public void shutdown() {
        assetLoaderPool.shutdown();
    }
}
