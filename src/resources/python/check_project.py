import os
import sys

import Metashape as ms

from utils import get_arg


def check_project(file):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    # add_photos_check
    if len(doc.chunks) < 1:
        print("vn:ADD_PHOTOS_CHECK:false")
    else:
        print("vn:ADD_PHOTOS_CHECK:true")


    # set_brightness
    try:
        if doc.chunk.image_brightness == 100 and doc.chunk.image_contrast == 100:
            print("vn:SET_BRIGHTNESS:false")
        else:
            print("vn:SET_BRIGHTNESS:true")
    except Exception as _:
        print("vn:SET_BRIGHTNESS:false")

    del doc


if __name__ == "__main__":
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")

    check_project(project_file)