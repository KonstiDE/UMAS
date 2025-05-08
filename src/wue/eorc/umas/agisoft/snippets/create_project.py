import os
import sys

import Metashape as ms

from utils import get_arg


def create_project(psx):
    doc = ms.Document()
    doc.save(path=psx,
             archive=True)
    del doc

    print("vn: true")


if __name__ == '__main__':
    args = sys.argv[1:]

    psx = get_arg(args, "-psxFile")

    create_project(psx)
