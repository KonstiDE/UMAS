import os
import sys

import Metashape as ms

from utils import get_arg


def align_photos(file):
    doc = ms.Document()

    doc.open(path=file, read_only=False)

    chunk = doc.chunk if doc.chunk is not None else doc.addChunk()

    for frame in chunk.frames:
        frame.matchPhotos(
            downscale=1,  # default 1=high, 2=medium, 3=none
            generic_preselection=True,
            reference_preselection=True,
            reference_preselection_mode=ms.ReferencePreselectionSource,# options: ReferencePreselectionSource, ReferencePreselectionEstimated, ReferencePreselectionSequential
            filter_mask=False,
            mask_tiepoints=True,
            filter_stationary_points=True,
            keypoint_limit=40000,
            keypoint_limit_per_mpx=1000,
            tiepoint_limit=4000,
            keep_keypoints=False,
            # [, pairs]
            # [,cameras],
            guided_matching=False,
            reset_matches=False,
            subdivide_task=True,
            workitem_size_cameras=20,
            workitem_size_pairs=80,
            max_workgroup_size=100,
            progress=report_progress
        )

    # doc.save()
    # chunk = doc.chunk

    # Perform photo alignment for the chunk. (-> creates sparse point cloud)
    # chunk.alignCameras(
    #     # [cameras][, point_clouds],
    #     min_image=2,
    #     adaptive_fitting=False,
    #     reset_alignment=False,
    #     subdivide_task=True
    #     # [, progress]
    # )

    # doc.save()


def report_progress(progress):
    print("p|{}".format(progress / 100))


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-project_file")

    align_photos(project_file)