import sys

import Metashape as ms
import math

from utils import get_arg, get_chunk


def estimate_brightness_and_contrast(psx, chunk_lab):
    doc = ms.Document()

    doc.open(path=psx, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk is not None:
        for camera in chunk.cameras:
            if not camera.photo:
                continue

            b, c = estimate(camera.photo)
            print(f"{camera.label}: Brightness={b:.2f}, Contrast={c:.2f}")


def estimate(photo):
    image = photo.image()
    buf = image.tostring()

    n_channels = image.channels
    pixels = image.width * image.height

    # compute grayscale brightness per pixel
    gray_vals = []
    for i in range(0, len(buf), n_channels):
        r, g, b = buf[i], buf[i+1], buf[i+2]
        gray = (r + g + b) / 3.0
        gray_vals.append(gray)

    # mean brightness
    mean_brightness = sum(gray_vals) / pixels

    # standard deviation = contrast
    variance = sum((g - mean_brightness) ** 2 for g in gray_vals) / pixels
    contrast = math.sqrt(variance)

    return mean_brightness, contrast


if __name__ == '__main__':
    args = sys.argv[1:]

    psx = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    estimate_brightness_and_contrast(psx, chunk_label)
