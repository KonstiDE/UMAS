import os
import sys

import Metashape as ms

from utils import get_arg


def add_photos_check(file):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    if len(doc.chunks) < 1:
        print("vn: false")
    else:
        print("vn: true")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")

    add_photos_check(project_file)
