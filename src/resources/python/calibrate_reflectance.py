import os
import sys

import Metashape as ms

from utils import get_arg, report_progress, get_chunk, rb


def calibrate_reflectance(file, chunk_lab, batch):
    doc = ms.Document()
    doc.read_only = False

    doc.open(path=file, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    batch = rb(batch)

    anything_calibrated = False

    for cam in chunk.cameras:
        if cam.sensor.calibration and hasattr(cam.sensor.calibration, "reflectance"):
            anything_calibrated = True
            break

    if anything_calibrated and not batch:
        print(f"ve:CALIBRATE_REFLECTANCE:Chunk is already aligned!~The images within this chunk already have been calibrated.~Please remove the current calibration first.")
        print("vn:CALIBRATE_REFLECTANCE:false")
    else:
        chunk.locateReflectancePanels(
            progress=report_progress
        )

        chunk.calibrateReflectance(
            use_reflectance_panels=True,
            use_sun_sensor=False,
            progress=report_progress
        )

        doc.save()

        print("vn:CALIBRATE_REFLECTANCE:true")

        del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")
    batch_edit = get_arg(args, "-batch")


    calibrate_reflectance(project_file, chunk_label, batch_edit)
