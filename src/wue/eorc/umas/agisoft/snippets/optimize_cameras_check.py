import os
import sys

import Metashape as ms

from utils import get_arg


def align_photos_check(file):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    if len(doc.chunks) > 0:
        all_chunks_optimized = True

        for chunk in doc.chunks:
            for sensor in chunk.sensors:
                if sensor.calibration.k4 == 0 and sensor.calibration.b1 == 0 and sensor.calibration.b2 == 0:
                    all_chunks_optimized = False
                    break

        del doc
        if all_chunks_optimized:
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