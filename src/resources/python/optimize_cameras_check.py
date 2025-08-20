import os
import sys

import Metashape as ms

from utils import get_arg, get_chunk


def align_photos_check(file, chunk_lab):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    chunk = get_chunk(doc.chunks, chunk_lab)
    sensor = chunk.sensors[0]

    if chunk is not None and sensor is not None:
        if sensor.calibration.k4 == 0 and sensor.calibration.b1 == 0 and sensor.calibration.b2 == 0:
            print("vn:OPTIMIZE_CAMERAS_CHECK:false")
        else:
            print("vn:OPTIMIZE_CAMERAS_CHECK:true")

    else:
        print("vn:OPTIMIZE_CAMERAS_CHECK:false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    align_photos_check(project_file, chunk_label)