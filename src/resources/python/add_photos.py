import os
import sys

import Metashape as ms

from utils import get_arg, report_progress, get_chunk, rb


def add_photos(file, chunk_lab, folders, calib_folders, batch):
    doc = ms.Document()

    doc.open(path=file, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    batch = rb(batch)

    if chunk is not None and not batch:
        print(f"ve:ADD_PHOTOS:Images already exist!~The images were already added to the chunk.~Please remove them to continue.")
        print("vn:ADD_PHOTOS:false")
    else:
        if batch:
            doc.remove(chunk)

        chunk = doc.addChunk()
        chunk.label = chunk_lab

        for folder in folders:
            photos = os.listdir(folder)
            photos = [os.path.join(folder, photo) for photo in photos] # supplies full names

            chunk.addPhotos(
                filenames=photos,
                layout=ms.UndefinedLayout, # options: UndefinedLayout, FlatLayout, MultiframeLayout, MultiplaneLayout
                strip_extensions=True,  # if False, adds ".JPG" to image name
                load_reference=True,
                load_xmp_calibration=True,
                load_xmp_orientation=True,
                load_xmp_accuracy=False,
                load_xmp_antenna=True,
                load_rpc_txt=False,
                progress=report_progress
            )


        if not calib_folders[0] == "":
            calib_group = chunk.addCameraGroup()
            calib_group.label = "calibration images"

            for folder in calib_folders:
                photos = os.listdir(folder)
                photos = [os.path.join(folder, photo) for photo in photos]

                num_before = len(chunk.cameras)

                chunk.addPhotos(
                    filenames=photos,
                    layout=ms.UndefinedLayout, # options: UndefinedLayout, FlatLayout, MultiframeLayout, MultiplaneLayout
                    strip_extensions=True,  # if False, adds ".JPG" to image name
                    load_reference=True,
                    load_xmp_calibration=True,
                    load_xmp_orientation=True,
                    load_xmp_accuracy=False,
                    load_xmp_antenna=True,
                    load_rpc_txt=False,
                    progress=report_progress
                )

                new_cameras = chunk.cameras[num_before:]

                for camera in new_cameras:
                    camera.group = calib_group



        doc.save(file)

        print("vn:ADD_PHOTOS:true")

    del doc



if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")
    photo_folder_list = get_arg(args, "-photo_folders")
    calib_folder_list = get_arg(args, "-calib_folders")
    batch_edit = get_arg(args, "-batch")

    try:
        photo_folders = photo_folder_list.split(",")
    except TypeError:
        photo_folders = [photo_folder_list]

    try:
        calib_folders = calib_folder_list.split(",")
    except TypeError:
        calib_folders = [calib_folder_list]

    add_photos(project_file, chunk_label, photo_folders, calib_folders, batch_edit)
