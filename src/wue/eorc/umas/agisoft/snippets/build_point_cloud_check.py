import os
import sys

import Metashape as ms

from utils import get_arg


def build_point_cloud_check(file):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    if len(doc.chunks) > 0:
        all_have_point_cloud = True

        for chunk in doc.chunks:
            if chunk.point_cloud is None:
                all_have_point_cloud = False
                break

        del doc
        if all_have_point_cloud:
            print("vn: true")
        else:
            print("vn: false")

    else:
        del doc
        print("vn: false")


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")

    build_point_cloud_check(project_file)