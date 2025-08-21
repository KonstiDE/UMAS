import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress, get_chunk


def build_dem_check(file, chunk_lab):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk is not None:
        if chunk.elevation is None:
            print("vn:BUILD_DEM_CHECK:false")
        else:
            print("vn:BUILD_DEM_CHECK:true")
    else:
        print("vn:BUILD_DEM_CHECK:false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    build_dem_check(project_file, chunk_label)
