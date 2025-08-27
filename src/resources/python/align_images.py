import os
import sys

import Metashape as ms

from utils import get_arg, report_progress, get_chunk


def align_photos(file, chunk_lab,
                 accuracy,
                 generic_preselection,
                 reference_preselection,
                 reference_preselection_mode,
                 key_point_limit,
                 key_point_limit_per_mpx,
                 tie_point_limit,
                 exclude_stationary_tie_points,
                 guided_image_matching,
                 adaptive_camera_model_fitting):

    if accuracy == "Highest":
        downscale = 0
    elif accuracy == "High":
        downscale = 1
    elif accuracy == "Medium":
        downscale = 2
    elif accuracy == "Low":
        downscale = 3
    elif accuracy == "Lowest":
        downscale = 4
    else:
        downscale = 0

    if reference_preselection_mode == "Source":
        reference_preselection_mode = ms.ReferencePreselectionSource
    elif reference_preselection_mode == "Estimated":
        reference_preselection_mode = ms.ReferencePreselectionEstimated
    else:
        reference_preselection_mode = ms.ReferencePreselectionSequential

    doc = ms.Document()

    doc.open(path=file, read_only=False)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk.point_cloud is not None:
        print(f"vd: Chunk {chunk_lab} is already aligned!")
        print("vn:ALIGN_IMAGES:true")
    else:
        for frame in chunk.frames:
            frame.matchPhotos(
                downscale=downscale, # default 0=highest, 1=high, 2=medium, 3=low, 4=lowest
                generic_preselection=bool(generic_preselection),
                reference_preselection=bool(reference_preselection),
                reference_preselection_mode=reference_preselection_mode,
                # options: ReferencePreselectionSource, ReferencePreselectionEstimated, ReferencePreselectionSequential
                filter_mask=False,
                mask_tiepoints=True,
                filter_stationary_points=bool(exclude_stationary_tie_points),
                keypoint_limit=float(key_point_limit),
                keypoint_limit_per_mpx=float(key_point_limit_per_mpx),
                tiepoint_limit=float(tie_point_limit),
                keep_keypoints=False,
                # [, pairs]
                # [,cameras],
                guided_matching=bool(guided_image_matching),
                reset_matches=True,
                subdivide_task=True,
                workitem_size_cameras=20,
                workitem_size_pairs=80,
                max_workgroup_size=100,
                progress=report_progress
            )

            chunk.alignCameras(
                # [cameras][, point_clouds],
                min_image=2,
                adaptive_fitting=bool(adaptive_camera_model_fitting),
                reset_alignment=False,
                subdivide_task=True,
                progress=report_progress
            )
        doc.save()

        print("vn:ALIGN_IMAGES:true")

        del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    accuracy = get_arg(args, "-accuracy")
    generic_preselection = get_arg(args, "-genericpreselection")
    reference_preselection = get_arg(args, "-referencepreselection")
    reference_preselection_mode = get_arg(args, "-referencepreselectioncombo")
    key_point_limit = get_arg(args, "-keypointlimit")
    key_point_limit_per_mpx = get_arg(args, "-keypointlimitpermpx")
    tie_point_limit = get_arg(args, "-tiepointlimit")
    exclude_stationary_tie_points = get_arg(args, "-excludestationarytiepoints")
    guided_image_matching = get_arg(args, "-guidedimagematching")
    adaptive_camera_model_fitting = get_arg(args, "-adaptivecameramodelfitting")

    align_photos(
        project_file,
        chunk_label,
        accuracy,
        generic_preselection,
        reference_preselection,
        reference_preselection_mode,
        key_point_limit,
        key_point_limit_per_mpx,
        tie_point_limit,
        exclude_stationary_tie_points,
        guided_image_matching,
        adaptive_camera_model_fitting
    )