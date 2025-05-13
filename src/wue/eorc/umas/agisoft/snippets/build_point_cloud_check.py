import os
import sys

import Metashape as ms

from utils import get_arg


def align_photos_check(file):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    if len(doc.chunks) > 0:
        all_aligned_already = True

        for chunk in doc.chunks:
            if chunk.point_cloud is None:
                all_aligned_already = False
                break

        del doc
        if all_aligned_already:
            print("vn: true")
        else:
            print("vn: false")

    else:
        del doc
        print("vn: false")


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")

    align_photos_check(project_file)