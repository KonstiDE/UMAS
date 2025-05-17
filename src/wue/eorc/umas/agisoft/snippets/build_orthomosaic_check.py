import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress


def export_orthomosaic(psx_file, ortho_file):
    doc = ms.Document()

    doc.open(path=psx_file, read_only=True)

    if os.path.exists(ortho_file):
        print("vn: false")
    else:
        print("vn: true")

    del doc

if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    target_file = get_arg(args, "-orthoFile")

    export_orthomosaic(project_file, target_file)
