import os
import sys

import Metashape as ms

from utils import get_arg, report_progress, get_chunk


def align_photos_check(file, chunk_lab):
    doc = ms.Document()

    doc.open(path=file, read_only=False)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk is not None:
        chunk.optimizeCameras(
            fit_f=True,
            fit_cx=True,
            fit_cy=True,
            fit_b1=False,
            fit_b2=False,
            fit_k1=True,
            fit_k2=True,
            fit_k3=True,
            fit_k4=False,
            fit_p1=True,
            fit_p2=True,
            fit_corrections=True,  # default False
            adaptive_fitting=True,  # default False
            tiepoint_covariance=True,  # default False
            progress=report_progress
        )

        doc.save()

        print("vn:OPTIMIZE_CAMERAS:true")

    else:
        print("vn:OPTIMIZE_CAMERAS:false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    align_photos_check(project_file, chunk_label)
