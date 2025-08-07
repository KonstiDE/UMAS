import sys

import Metashape as ms

from utils import get_arg


def set_brightness(psx, brightness, contrast):
    doc = ms.Document()
    doc.open(psx)

    for chunk in doc.chunks:
        chunk.image_brightness = float(brightness)
        chunk.image_contrast = float(contrast)

    doc.save(path=psx,
             archive=True)

    del doc

    print("vn: true")


if __name__ == '__main__':
    args = sys.argv[1:]

    psx = get_arg(args, "-psxFile")
    brightness = get_arg(args, "-brightness")
    contrast = get_arg(args, "-contrast")

    set_brightness(psx, brightness, contrast)