import os
import sys

import Metashape as ms

from utils import get_arg


def add_photos(file, folders):
    doc = ms.Document()

    doc.open(path=file, read_only=False)
    chunk = doc.addChunk()

    for folder in folders:
        photos = os.listdir(folder)
        photos = [os.path.join(folder, photo) for photo in photos] # supplies full names

        chunk.addPhotos(
            photos,
            layout=ms.UndefinedLayout, # options: UndefinedLayout, FlatLayout, MultiframeLayout, MultiplaneLayout
            strip_extensions=True,  # if False, adds ".JPG" to image name
            load_reference=True,
            load_xmp_calibration=True,
            load_xmp_orientation=True,
            load_xmp_accuracy=False,
            load_xmp_antenna=True,
            load_rpc_txt=False
        )

    doc.save(file)

    del doc

    print("vn: true")


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    photo_folder_list = get_arg(args, "-photo_folder")

    try:
        photo_folders = photo_folder_list.split(",")
    except TypeError:
        photo_folders = [photo_folder_list]

    add_photos(project_file, photo_folders)
