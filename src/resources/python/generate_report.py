import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress, get_chunk


def generate_report(psx_file, report_file, flight_name, desc, chunk_lab):
    doc = ms.Document()

    doc.open(path=psx_file, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk is not None:
        if os.path.exists(report_file):
            print("vd: This Report file already exists!")
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

                print("vn:GENERATE_REPORT:true")

    else:
        print("vn:GENERATE_REPORT:false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    target_file = get_arg(args, "-reportFile")
    flights_name = get_arg(args, "-flightName")
    description = get_arg(args, "-description")
    chunk_label = get_arg(args, "-chunk_label")

    generate_report(project_file, target_file, flights_name, description, chunk_label)
