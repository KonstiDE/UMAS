import os
import sys

import Metashape as ms

from utils import get_arg


def align_photos_check(file):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    if len(doc.chunks) > 0:
        all_aligned_already = True

        for chunk in doc.chunks:
            if chunk.tie_points is None:
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

        del doc
        if all_aligned_already:
            print("vn: true")
        else:
            print("vn: false")

    else:
        del doc
        print("vn: false")

def report_progress(f):
    print("vp: {}".format(str(f)))


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")

    align_photos_check(project_file)
