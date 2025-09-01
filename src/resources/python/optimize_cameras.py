import os
import sys

import Metashape as ms

from utils import get_arg, report_progress, get_chunk, rb


def optimize_cameras(
        file, chunk_lab, fitf, fitk1, fitk2, fitk3, fitk4, fitcxcy, fitp1, fitp2, fitb1, fitb2,
        adaptive_fitting, estimate_tie_cov, fit_additional):

    doc = ms.Document()

    doc.open(path=file, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk is not None:
        chunk.optimizeCameras(
            fit_f=rb(fitf),
            fit_cx=rb(fitcxcy),
            fit_cy=rb(fitcxcy),
            fit_b1=rb(fitb1),
            fit_b2=rb(fitb2),
            fit_k1=rb(fitk1),
            fit_k2=rb(fitk2),
            fit_k3=rb(fitk3),
            fit_k4=rb(fitk4),
            fit_p1=rb(fitp1),
            fit_p2=rb(fitp2),
            fit_corrections=rb(fit_additional),  # default False
            adaptive_fitting=rb(adaptive_fitting),  # default False
            tiepoint_covariance=rb(estimate_tie_cov),  # default False
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

    fitf = get_arg(args, "-fitf")
    fitk1 = get_arg(args, "-fitk1")
    fitk2 = get_arg(args, "-fitk2")
    fitk3 = get_arg(args, "-fitk3")
    fitk4 = get_arg(args, "-fitk4")

    fitcxcy = get_arg(args, "-fitcxcy")
    fitp1 = get_arg(args, "-fitp1")
    fitp2 = get_arg(args, "-fitp2")
    fitb1 = get_arg(args, "-fitb1")
    fitb2 = get_arg(args, "-fitb2")

    adaptive_fitting = get_arg(args, "-adaptivecameramodelfitting")
    estimate_tie_cov = get_arg(args, "-estimatetiepointcovariance")
    fit_additional = get_arg(args, "-fitadditionalcorrections")

    optimize_cameras(project_file, chunk_label, fitf, fitk1, fitk2, fitk3, fitk4, fitcxcy, fitp1, fitp2, fitb1, fitb2,
                     adaptive_fitting, estimate_tie_cov, fit_additional)
