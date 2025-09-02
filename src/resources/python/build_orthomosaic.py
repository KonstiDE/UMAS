import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress, get_chunk, rb


def build_ortho(file, chunk_lab, surface, blending_mode, refine_seamlines, enable_hole_filling,
                enable_ghosting_filter, enable_backface_culling, batch):

    doc = ms.Document()

    doc.open(path=file, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if surface == "DEM":
        surface_mode = ms.ElevationData
    else:
        surface_mode = ms.ElevationData

    if belnding_mode == "Mosaic (default)":
        blending = ms.MosaicBlending
    elif blending_mode == "Average":
        blending = ms.AverageBlending
    elif blending_mode == "Diabled":
        blending = ms.DisabledBlending
    else:
        blending = ms.MosaicBlending


    if chunk is not None:
        if chunk.orthomosaic is not None and not batch:
            print(f"ve:Chunk {chunk_lab} already has a orthomosaic!~For this chunk, an Orthomosaic was already processed which cannot be overwritten.~Please remove the current Orthomosaic.")
            print("vn:BUILD_ORTHOMOSAIC:false")
        else:
            if batch:
                chunk.remove(chunk.orthomosaic)

            chunk.buildOrthomosaic(
                surface_data=surface_mode,
                blending_mode=blending, # options: AverageBlending, MosaicBlending, (MinBlending, MaxBlending,) DisabledBlending,
                fill_holes=rb(enable_hole_filling),
                ghosting_filter=rb(enable_ghosting_filter),
                cull_faces=rb(enable_backface_culling),
                refine_seamlines=rb(refine_seamlines),
                # [, projection ]
                # [, region],
                resolution=0,
                resolution_x=0,
                resolution_y=0,
                flip_x=False,
                flip_y=False,
                flip_z=False,
                subdivide_task=True,
                workitem_size_cameras=20,
                workitem_size_tiles=10,
                max_workgroup_size=100,
                progress=report_progress
            )

            doc.save()

            print("vn:BUILD_ORTHOMOSAIC:true")

    else:
        print("vn:BUILD_ORTHOMOSAIC:false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")
    batch_edit = get_arg(args, "-batch")

    surface = get_arg(args, "-surface")
    belnding_mode = get_arg(args, "-blendingmode")

    refine_seamlines = get_arg(args, "-refineseamlines")
    enable_hole_filling = get_arg(args, "-enableholefilling")
    enable_ghosting_filter = get_arg(args, "-enableghostingfilter")
    enable_backface_culling = get_arg(args, "-enablebackfaceculling")

    build_ortho(project_file, chunk_label, surface, belnding_mode, refine_seamlines, enable_hole_filling,
                enable_ghosting_filter, enable_backface_culling, batch_edit)
