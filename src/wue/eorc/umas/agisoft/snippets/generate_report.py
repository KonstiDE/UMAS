import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress


def generate_report(psx_file, report_file, flight_name, desc):
    doc = ms.Document()

    doc.open(path=psx_file, read_only=False)

    if os.path.exists(report_file):
        print("vd: This Report file already exists!")
        del doc
    else:
        for chunk in doc.chunks:
            chunk.exportReport(
                path=report_file,
                title=flight_name,
                description=desc,  # os.listdir(dirs[7])
                font_size=12,
                page_numbers=True,
                include_system_info=True,
                # [, user_settings]
                progress=report_progress
            )

        del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    target_file = get_arg(args, "-reportFile")
    flights_name = get_arg(args, "-flightName")
    description = get_arg(args, "-description")

    generate_report(project_file, target_file, flights_name, description)
