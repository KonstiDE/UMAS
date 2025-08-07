import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress


def build_dem_check(psx_file):
    doc = ms.Document()

    doc.open(path=psx_file, read_only=True)

    if len(doc.chunks) > 0:
        all_have_dem = True

        for chunk in doc.chunks:
            if chunk.elevation is None:
                all_have_dem = False
                break

        del doc
        if all_have_dem:
            print("vn: true")
        else:
            print("vn: false")

    else:
        del doc
        print("vn: false")


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")

    build_dem_check(project_file)
