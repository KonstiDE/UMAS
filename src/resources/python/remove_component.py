import os
import sys

import Metashape as ms

from utils import get_arg, get_chunk, rb


def remove_component(file, chunk_lab, agisoft_task):

    doc = ms.Document()

    doc.open(path=file, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk is not None:
        if agisoft_task == "ADD_PHOTOS":
            if len(chunk.cameras) > 0:
                for camera in chunk.cameras:
                    chunk.remove(camera)

        elif agisoft_task == "REFLECTANCE_CALIBRATION":
            if chunk.meta["ReflectanceCalibration"] is not None and rb(chunk.meta["ReflectanceCalibration"]):
                chunk.meta["ReflectanceCalibration"] = "False"

        elif agisoft_task == "ALIGN_IMAGES":
            if chunk.tie_points is not None:
                chunk.tie_points = None

        elif agisoft_task == "BUILD_POINT_CLOUD":
            if chunk.depth_maps is not None:
                chunk.remove(chunk.depth_maps)

            if chunk.point_cloud is not None:
                chunk.remove(chunk.point_cloud)

        elif agisoft_task == "BUILD_DEM":
            if chunk.elevation is not None:
                chunk.remove(chunk.elevation)

        elif agisoft_task == "BUILD_ORTHOMOSAIC":
            if chunk.orthomosaic is not None:
                chunk.remove(chunk.orthomosaic)

        doc.save()

        print("vn:REMOVE_COMPONENT:true")

    else:
        print("ve:REMOVE_COMPONENT:Could not remove component!~The chunk on where you request to remove something does not exist.~Please restart UMAS.")
        print("vn:REMOVE_COMPONENT:false")

    del doc

if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    agisoft_task = get_arg(args, "-agisofttask")

    remove_component(project_file, chunk_label, agisoft_task)