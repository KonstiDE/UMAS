import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, get_chunk


def generate_report_check(psx_file, report_file, chunk_lab):
    doc = ms.Document()

    doc.open(path=psx_file, read_only=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk is not None:
        if os.path.exists(report_file):
            print("vn:GENERATE_REPORT_CHECK:true")
        else:
            print("vn:GENERATE_REPORT_CHECK:false")

    else:
        print("vn:GENERATE_REPORT_CHECK:false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    target_file = get_arg(args, "-reportFile")
    chunk_label = get_arg(args, "-chunk_label")

    generate_report_check(project_file, target_file, chunk_label)
