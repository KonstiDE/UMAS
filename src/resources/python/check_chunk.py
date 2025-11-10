import os
import sys

import Metashape as ms

from utils import get_arg, get_chunk, rb, extend_file_name


def check_chunk(file, demFile, orthoFile, reportFile, chunk_lab):
    doc = ms.Document()

    doc.open(path=file, read_only=True)

    # add_photos_check
    found = False

    if len(doc.chunks) > 0:
        for chunk in doc.chunks:
            if chunk.label == chunk_lab:
                found = True
                break

    if not found:
        print("vn:ADD_PHOTOS_CHECK:false")
        print("vn:SET_BRIGHTNESS_CHECK:false")
        print("vn:CALIBRATE_REFLECTANCE_CHECK:false")
        print("vn:ALIGN_IMAGES_CHECK:false")
        print("vn:OPTIMIZE_CAMERAS_CHECK:false")
        print("vn:BUILD_POINT_CLOUD_CHECK:false")
        print("vn:BUILD_DEM_CHECK:false")
        print("vn:BUILD_ORTHOMOSAIC_CHECK:false")
        print("vn:EXPORT_DEM_CHECK:false")
        print("vn:EXPORT_ORTHOMOSAIC_CHECK:false")
        print("vn:GENERATE_REPORT_CHECK:false")
        print("vn:CHECK_CHUNK:true")
    else:
        chunk = get_chunk(doc.chunks, chunk_lab)

        demFile = extend_file_name(demFile, chunk_lab)
        orthoFile = extend_file_name(orthoFile, chunk_lab)
        reportFile = extend_file_name(reportFile, chunk_lab)

        if chunk is not None:
            # add_photos check
            if len(chunk.cameras) > 0:
                print("vn:ADD_PHOTOS_CHECK:true")
            else:
                print("vn:ADD_PHOTOS_CHECK:false")

            # set_brightness check
            if chunk.image_brightness == 100 and chunk.image_contrast == 100:
                print("vn:SET_BRIGHTNESS_CHECK:false")
            else:
                print("vn:SET_BRIGHTNESS_CHECK:true")

            # calibrate_reflectance check
            if chunk.meta["ReflectanceCalibration"] is not None and rb(chunk.meta["ReflectanceCalibration"]):
                print("vn:CALIBRATE_REFLECTANCE_CHECK:true")
            else:
                print("vn:CALIBRATE_REFLECTANCE_CHECK:false")


            # align_images check
            if chunk.tie_points is None:
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
            if chunk.point_cloud is None:
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


            # export_dem check
            if os.path.exists(demFile):
                print("vn:EXPORT_DEM_CHECK:true")
            else:
                print("vn:EXPORT_DEM_CHECK:false")


            # export_orthomosaic check
            if os.path.exists(orthoFile):
                print("vn:EXPORT_ORTHOMOSAIC_CHECK:true")
            else:
                print("vn:EXPORT_ORTHOMOSAIC_CHECK:false")


            # generate_report check
            if os.path.exists(reportFile):
                print("vn:GENERATE_REPORT_CHECK:true")
            else:
                print("vn:GENERATE_REPORT_CHECK:false")


            print("vn:CHECK_CHUNK:true")

        else:
            print("vn:SET_BRIGHTNESS_CHECK:false")
            print("vn:ALIGN_IMAGES_CHECK:false")
            print("vn:OPTIMIZE_CAMERAS_CHECK:false")
            print("vn:BUILD_POINT_CLOUD_CHECK:false")
            print("vn:BUILD_DEM_CHECK:false")
            print("vn:BUILD_ORTHOMOSAIC_CHECK:false")
            print("vn:EXPORT_DEM_CHECK:false")
            print("vn:EXPORT_ORTHOMOSAIC_CHECK:false")
            print("vn:GENERATE_REPORT_CHECK:false")
            print("vn:CHECK_CHUNK:true")


    ms.app.quit()

    del doc


if __name__ == "__main__":
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    dem_file = get_arg(args, "-demFile")
    ortho_file = get_arg(args, "-orthoFile")
    report_file = get_arg(args, "-reportFile")
    chunk_label = get_arg(args, "-chunk_label")

    check_chunk(project_file, dem_file, ortho_file, report_file, chunk_label)