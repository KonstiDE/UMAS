import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, get_chunk


def export_orthomosaic_check(psx_file, dem_file, chunk_lab):
    doc = ms.Document()

    doc.open(path=psx_file, read_only=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk is not None:
        if os.path.exists(dem_file):
            print("vn:EXPORT_ORTHOMOSAIC_CHECK:true")
        else:
            print("vn:EXPORT_ORTHOMOSAIC_CHECK:false")
    else:
        print("vn:EXPORT_ORTHOMOSAIC_CHECK:false")

    del doc

if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    target_file = get_arg(args, "-orthoFile")
    chunk_label = get_arg(args, "-chunk_label")

    export_orthomosaic_check(project_file, target_file, chunk_label)
