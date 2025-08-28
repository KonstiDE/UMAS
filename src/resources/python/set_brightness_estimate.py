import sys

import Metashape as ms
import math

from utils import get_arg, get_chunk


def estimate_brightness_and_contrast(psx, chunk_lab):
    doc = ms.Document()

    doc.open(path=psx, read_only=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    bs = []
    cs = []

    if chunk is not None:
        if len(chunk.cameras) > 10:
            for index in range(0, len(chunk.cameras), 10):
                camera = chunk.cameras[index]

                if not camera.photo:
                    continue

                b, c = estimate(camera.photo)
                bs.append(b)
                cs.append(c)
        else:
            print("vn:SET_BRIGHTNESS_ESTIMATE:false")




    print(f"vn:SET_BRIGHTNESS_ESTIMATE:{sum(bs) / len(bs)}#{sum(cs) / len(cs)}")

    del doc


def estimate(photo):
    image = photo.image("8") # image thumbnail
    buf = image.tostring()

    n_channels = 3

    # compute grayscale brightness per pixel
    gray_vals = []
    count = 0
    for i in range(0, len(buf), n_channels * 1000):
        r, g, b = buf[i], buf[i+1], buf[i+2]
        gray = (r + g + b) / 3.0
        gray_vals.append(gray)
        count += 1

    # mean brightness
    mean_brightness = sum(gray_vals) / count

    # standard deviation = contrast
    variance = sum((g - mean_brightness) ** 2 for g in gray_vals) / count
    contrast = math.sqrt(variance)

    return mean_brightness, contrast


if __name__ == '__main__':
    args = sys.argv[1:]

    psx = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    estimate_brightness_and_contrast(psx, chunk_label)
