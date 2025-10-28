import os
import sys

import Metashape as ms

from utils import get_arg, get_chunk


def build_point_cloud_check(file, chunk_lab):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk.point_cloud is not None:
        print("vn:BUILD_POINT_CLOUD_CHECK:true")
    else:
        print("vn:BUILD_POINT_CLOUD_CHECK:false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    build_point_cloud_check(project_file, chunk_label)