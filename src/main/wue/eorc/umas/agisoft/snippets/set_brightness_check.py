import sys

import Metashape as ms

from utils import get_arg


def set_brightness_check(psx):
    doc = ms.Document()
    doc.open(psx, read_only=True)

    try:
        if doc.chunk.image_brightness == 100 and doc.chunk.image_contrast == 100:
            print("vn: false")
        else:
            print("vn: true")
    except Exception as _:
        print("vn: false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    psx = get_arg(args, "-psxFile")

    set_brightness_check(psx)