import os
import sys

import Metashape as ms

from utils import get_arg, get_chunk, rb


def calibrate_reflectance_check(file, chunk_lab):
    print("vn:CALIBRATE_REFLECTANCE_CHECK:true")

    """ doc = ms.Document()
    doc.read_only = False

    doc.open(path=file, read_only=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk.meta["ReflectanceCalibration"] is not None and rb(chunk.meta["ReflectanceCalibration"]):
        print("vn:CALIBRATE_REFLECTANCE_CHECK:true")
    else:
        print("vn:CALIBRATE_REFLECTANCE_CHECK:false")

        del doc """


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")


    calibrate_reflectance_check(project_file, chunk_label)
