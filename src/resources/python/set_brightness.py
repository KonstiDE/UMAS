import sys

import Metashape as ms

from utils import get_arg, get_chunk


def set_brightness(psx, brightness, contrast, chunk_label):
    doc = ms.Document()

    doc.open(path=psx, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_label)

    if chunk is not None:
        chunk.image_brightness = float(brightness)
        chunk.image_contrast = float(contrast)

        doc.save()

        print("vn:SET_BRIGHTNESS:true")
    else:
        print("vn:SET_BRIGHTNESS:false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    psx = get_arg(args, "-psxFile")
    brightness = get_arg(args, "-brightness")
    contrast = get_arg(args, "-contrast")
    chunk_label = get_arg(args, "-chunk_label")

    set_brightness(psx, brightness, contrast, chunk_label)