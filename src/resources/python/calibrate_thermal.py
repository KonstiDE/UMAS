import os
import sys

import Metashape as ms

from utils import get_arg, report_progress, get_chunk, rb


def calibrate_thermal(file, chunk_lab, batch):
    # Actually this is really false within an agisoft caller procedure lol

    print("vn:CALIBRATE_THERMAL:true")


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")
    batch_edit = get_arg(args, "-batch")


    calibrate_thermal(project_file, chunk_label, batch_edit)
