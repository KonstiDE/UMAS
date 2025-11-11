import os
import sys

import Metashape as ms

from utils import get_arg, report_progress, get_chunk, rb


def calibrate_reflectance(file, chunk_lab, batch):
    print("vn:CALIBRATE_REFLECTANCE:true")

"""     doc = ms.Document()
    doc.read_only = False

    doc.open(path=file, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    batch = rb(batch)

    if chunk.meta["ReflectanceCalibration"] is not None and not rb(chunk.meta["ReflectanceCalibration"]):
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

        chunk.meta["ReflectanceCalibration"] = "True"

        doc.save()

        print("vn:CALIBRATE_REFLECTANCE:true")

        del doc """


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")
    batch_edit = get_arg(args, "-batch")


    calibrate_reflectance(project_file, chunk_label, batch_edit)
