import os
import sys

import Metashape as ms

from utils import get_arg, get_chunk


def check_chunk(file, chunk_lab):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    # add_photos_check
    found = False

    if len(doc.chunks) > 0:
        for chunk in doc.chunks:
                if chunk.label == chunk_lab:
                    print("vn:ADD_PHOTOS_CHECK:true")
                    found = True
                    break

    if not found:
        print("vn:ADD_PHOTOS_CHECK:false")
        print("vn:SET_BRIGHTNESS_CHECK:false")
        print("vn:ALIGN_IMAGES_CHECK:false")
        print("vn:OPTIMIZE_CAMERAS_CHECK:false")
    else:
        chunk = get_chunk(doc.chunks, chunk_lab)

        if chunk is not None:

            # set_brightness check
            if chunk.image_brightness == 100 and chunk.image_contrast == 100:
                print("vn:SET_BRIGHTNESS_CHECK:false")
            else:
                print("vn:SET_BRIGHTNESS_CHECK:true")

            # align_images check
            if chunk.point_cloud is None:
                print("vn:ALIGN_IMAGES_CHECK:false")
            else:
                print("vn:ALIGN_IMAGES_CHECK:true")

            # optimize_cameras check
            sensor = chunk.sensors[0]
            if sensor is not None:
                if sensor.calibration.k4 == 0 and sensor.calibration.b1 == 0 and sensor.calibration.b2 == 0:
                    print("vn:OPTIMIZE_CAMERAS_CHECK:false")
                else:
                    print("vn:OPTIMIZE_CAMERAS_CHECK:true")

            else:
                print("vn:OPTIMIZE_CAMERAS_CHECK:false")


            # build_point_cloud / dense_cloud check
            if chunk.dense_cloud is None:
                print("vn:BUILD_POINT_CLOUD_CHECK:false")
            else:
                print("vn:BUILD_POINT_CLOUD_CHECK:true")


            # build_dem check
            if chunk.elevation is None:
                print("vn:BUILD_DEM_CHECK:false")
            else:
                print("vn:BUILD_DEM_CHECK:true")


            # build_orthomosaic check
            if chunk.orthomosaic is None:
                print("vn:BUILD_ORTHOMOSAIC_CHECK:false")
            else:
                print("vn:BUILD_ORTHOMOSAIC_CHECK:true")



        else:
            print("vn:SET_BRIGHTNESS_CHECK:false")
            print("vn:ALIGN_IMAGES_CHECK:false")
            print("vn:OPTIMIZE_CAMERAS_CHECK:false")
            print("vn:CHECK_CHUNK:false")

    del doc

    print("vn:CHECK_CHUNK:true")


if __name__ == "__main__":
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    check_chunk(project_file, chunk_label)