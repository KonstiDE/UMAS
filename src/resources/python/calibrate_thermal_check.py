import os
import sys

import Metashape as ms

from utils import get_arg, get_chunk, rb


def calibrate_thermal_check(file, chunk_lab):
    # conversion to tifs check should be here or can be within the java exec process idc

    print("vn:CALIBRATE_THERMAL_CHECK:true")


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")


    calibrate_thermal_check(project_file, chunk_label)