from PIL import Image, ImageDraw
import random
import os

random.seed(42)

def make_icon(size):
    img = Image.new("RGB", (size, size))
    draw = ImageDraw.Draw(img)

    # Cielo al tramonto: gradiente arancione -> viola scuro
    top = (255, 140, 60)
    bottom = (40, 20, 70)
    for y in range(size):
        t = y / size
        r = int(top[0] * (1 - t) + bottom[0] * t)
        g = int(top[1] * (1 - t) + bottom[1] * t)
        b = int(top[2] * (1 - t) + bottom[2] * t)
        draw.line([(0, y), (size, y)], fill=(r, g, b))

    # Sole
    sun_r = size * 0.16
    sun_cx, sun_cy = size * 0.5, size * 0.36
    draw.ellipse(
        [sun_cx - sun_r, sun_cy - sun_r, sun_cx + sun_r, sun_cy + sun_r],
        fill=(255, 210, 120),
    )

    # Skyline: edifici a silhouette scura, altezze varie
    ground_y = size * 0.98
    x = 0
    silhouette = (25, 15, 35)
    while x < size:
        w = random.randint(int(size * 0.08), int(size * 0.16))
        h = random.randint(int(size * 0.28), int(size * 0.62))
        y0 = ground_y - h
        draw.rectangle([x, y0, x + w, ground_y], fill=silhouette)

        # finestrelle accese
        win_color = (255, 220, 140)
        wx = x + w * 0.15
        while wx < x + w - w * 0.15:
            wy = y0 + h * 0.15
            while wy < ground_y - h * 0.1:
                if random.random() > 0.4:
                    ws = max(1, int(size * 0.01))
                    draw.rectangle([wx, wy, wx + ws, wy + ws], fill=win_color)
                wy += size * 0.05
            wx += size * 0.045
        x += w + int(size * 0.01)

    # Strada in basso con striscia centrale
    road_h = size * 0.08
    draw.rectangle([0, size - road_h, size, size], fill=(30, 30, 35))
    dash_w = size * 0.06
    gap = size * 0.04
    xx = 0
    while xx < size:
        draw.rectangle(
            [xx, size - road_h / 2 - 2, xx + dash_w, size - road_h / 2 + 2],
            fill=(230, 200, 90),
        )
        xx += dash_w + gap

    return img


def generate_all():
    sizes = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }

    os.makedirs("icons_output", exist_ok=True)

    for folder, sz in sizes.items():
        icon = make_icon(sz)
        out_dir = f"icons_output/{folder}"
        os.makedirs(out_dir, exist_ok=True)
        icon.save(f"{out_dir}/ic_launcher.png")
        icon.save(f"{out_dir}/ic_launcher_round.png")

    big = make_icon(512)
    big.save("icons_output/icon_preview.png")
    print("Icone generate in icons_output/")


if __name__ == "__main__":
    generate_all()
