import os
import sys

import Metashape as ms

from utils import get_arg, report_progress, get_chunk, rb


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
                 adaptive_camera_model_fitting,
                 batch):

    # Highest = 0
    # High = 1
    # Medium = 2
    # Low = 4
    # Lowest = 8

    if accuracy == "Highest":
        downscale = 0
    elif accuracy == "High":
        downscale = 1
    elif accuracy == "Medium":
        downscale = 2
    elif accuracy == "Low":
        downscale = 4
    elif accuracy == "Lowest":
        downscale = 8
    else:
        downscale = 0

    if reference_preselection_mode == "Source":
        reference_preselection_mode = ms.ReferencePreselectionSource
    elif reference_preselection_mode == "Estimated":
        reference_preselection_mode = ms.ReferencePreselectionEstimated
    else:
        reference_preselection_mode = ms.ReferencePreselectionSequential

    doc = ms.Document()
    doc.read_only = False

    doc.open(path=file, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    batch = rb(batch)

    if chunk.tie_points is not None and not batch:
        print(f"ve:ALIGN_IMAGES:Chunk is already aligned!~The images within this chunk already have been aligned to each other.~Please remove the current alignment first.")
        print("vn:ALIGN_IMAGES:false")
    else:
        for frame in chunk.frames:
            frame.matchPhotos(
                downscale=downscale,
                generic_preselection=rb(generic_preselection),
                reference_preselection=rb(reference_preselection),
                reference_preselection_mode=reference_preselection_mode,
                filter_mask=False,
                mask_tiepoints=True,
                filter_stationary_points=rb(exclude_stationary_tie_points),
                keypoint_limit=float(key_point_limit),
                keypoint_limit_per_mpx=float(key_point_limit_per_mpx),
                tiepoint_limit=float(tie_point_limit),
                keep_keypoints=False,
                # [, pairs]
                # [,cameras],
                guided_matching=rb(guided_image_matching),
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
                adaptive_fitting=rb(adaptive_camera_model_fitting),
                reset_alignment=True,
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
    batch_edit = get_arg(args, "-batch")

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
        adaptive_camera_model_fitting,
        batch_edit
    )