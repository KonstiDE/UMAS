import os
import sys

import Metashape as ms

from utils import get_arg


def create_project(p, n):
    doc = ms.Document()
    doc.save(path=os.path.join(p, n),
             archive=True)
    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    path = get_arg(args, "-path")
    name = get_arg(args, "-name")

    create_project(path, name)
