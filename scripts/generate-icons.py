import os
from PIL import Image

source = "assets-logo/HazeClient.png"
if not os.path.exists(source):
    print("Source not found")
    exit(1)

img = Image.open(source).convert("RGBA")

# Generate webp for UI
ui_dir = "xmcl-keystone-ui/src/assets"
img.save(os.path.join(ui_dir, "logo.webp"), "WEBP")
print("Saved logo.webp")

# Generate sizes for electron
icon_dir = "xmcl-electron-app/icons"
sizes = {
    "dark@256x256.png": 256,
    "dark@SmallTile.png": 71,
    "dark@SmallTile.scale-400.png": 284,
    "dark@Square150x150Logo.png": 150,
    "dark@Square150x150Logo.scale-400.png": 600,
    "dark@Square44x44Logo.png": 44,
    "dark@Square44x44Logo.targetsize-256.png": 256,
    "dark@Square44x44Logo.targetsize-256_altform-unplated.png": 256,
    "dark@StoreLogo.png": 50,
    "dark@StoreLogo.scale-400.png": 200,
    "dark@tray.png": 16,
    "large-dark@LargeTile.png": 310,
    "large-dark@LargeTile.scale-200.png": 620,
    "large-light@LargeTile.scale-200_theme-light.png": 620,
    "large-light@LargeTile.theme-light.png": 310,
    "light@256x256.png": 256,
    "light@SmallTile.scale-400_theme-light.png": 284,
    "light@SmallTile.theme-light.png": 71,
    "light@Square150x150Logo.scale-400_theme-light.png": 600,
    "light@Square150x150Logo.theme-light.png": 150,
    "light@Square44x44Logo.targetsize-256_altform-lightunplated.png": 256,
    "light@Square44x44Logo.targetsize-256_theme-light.png": 256,
    "light@Square44x44Logo.theme-light.png": 44,
    "light@StoreLogo.scale-400_theme-light.png": 200,
    "light@StoreLogo.theme-light.png": 50,
    "light@tray.png": 16,
}

wide_sizes = {
    "wide-dark@Wide310x150Logo.png": (310, 150),
    "wide-dark@Wide310x150Logo.scale-150.png": (465, 225),
    "wide-dark@Wide310x150Logo.scale-200.png": (620, 300),
    "wide-dark@Wide310x150Logo.scale-400.png": (1240, 600),
    "wide-light@Wide310x150Logo.scale-200_theme-light.png": (620, 300),
    "wide-light@Wide310x150Logo.scale-400_theme-light.png": (1240, 600),
    "wide-light@Wide310x150Logo.theme-light.png": (310, 150),
    "wide-light@Wie310x150Logo.scale-150_theme-light.png": (465, 225),
}

for name, size in sizes.items():
    resized = img.resize((size, size), Image.Resampling.LANCZOS)
    resized.save(os.path.join(icon_dir, name))

for name, size in wide_sizes.items():
    # Crop or fit into wide sizes
    bg = Image.new("RGBA", size, (0,0,0,0))
    # Fit the square icon in the center of the wide banner
    sq_size = min(size[0], size[1])
    resized = img.resize((sq_size, sq_size), Image.Resampling.LANCZOS)
    x = (size[0] - sq_size) // 2
    y = (size[1] - sq_size) // 2
    bg.paste(resized, (x, y), mask=resized)
    bg.save(os.path.join(icon_dir, name))

# icns and ico
img_icns = img.resize((256, 256), Image.Resampling.LANCZOS)
img_icns.save(os.path.join(icon_dir, "dark.icns"), format="ICNS")
img_icns.save(os.path.join(icon_dir, "light.icns"), format="ICNS")

img_ico = img.resize((256, 256), Image.Resampling.LANCZOS)
img_ico.save(os.path.join(icon_dir, "dark.ico"), format="ICO", sizes=[(256, 256)])
img_ico.save(os.path.join(icon_dir, "light.ico"), format="ICO", sizes=[(256, 256)])

print("Done")
