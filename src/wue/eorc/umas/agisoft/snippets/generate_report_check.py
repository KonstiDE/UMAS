import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress


def generate_report_check(psx_file, report_file):
    doc = ms.Document()

    doc.open(path=psx_file, read_only=True)

    if os.path.exists(report_file):
        print("vn: true")
    else:
        print("vn: false")

    del doc

if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    target_file = get_arg(args, "-reportFile")

    generate_report_check(project_file, target_file)
