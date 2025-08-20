import sys

import Metashape as ms

from utils import get_arg, get_chunk


def set_brightness_check(psx, chunk_lab):
    doc = ms.Document()
    doc.open(psx, read_only=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk is not None:
        if chunk.image_brightness == 100 and chunk.image_contrast == 100:
            print("vn:SET_BRIGHTNESS_CHECK:false")
        else:
            print("vn:SET_BRIGHTNESS_CHECK:true")
    else:
        print("vn:SET_BRIGHTNESS_CHECK:false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    psx = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    set_brightness_check(psx, chunk_label)