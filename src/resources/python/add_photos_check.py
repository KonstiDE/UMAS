import os
import sys

import Metashape as ms

from utils import get_arg, get_chunk


def add_photos_check(file, chunk_lab):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk is None:
        print("vn:ADD_PHOTOS_CHECK:true")
    else:
        if len(chunk.cameras > 0):
            print("vn:ADD_PHOTOS_CHECK:true")
        else:
            print("vn:ADD_PHOTOS_CHECK:false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    add_photos_check(project_file, chunk_label)
