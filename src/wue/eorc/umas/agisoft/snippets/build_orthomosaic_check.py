import os
import sys

import Metashape as ms

from utils import get_arg


def build_ortho_check(file):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    if len(doc.chunks) > 0:
        all_have_ortho = True

        for chunk in doc.chunks:
            if chunk.orthomosaic is None:
                all_have_ortho = False
                break

        del doc
        if all_have_ortho:
            print("vn: true")
        else:
            print("vn: false")

    else:
        del doc
        print("vn: false")


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")

    build_ortho_check(project_file)
