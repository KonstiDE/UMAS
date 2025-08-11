import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress


def build_ortho(file):
    doc = ms.Document()

    doc.open(path=file, read_only=False)

    all_have_dem = True

    for chunk in doc.chunks:
        if chunk.elevation is None:
            all_have_dem = False

    if all_have_dem:
        print("vd: All chunks already have orthomosaic!")
        del doc

    else:
        for chunk in doc.chunks:
            if chunk.elevation is None:
                chunk.buildOrthomosaic(
                    surface_data=Metashape.ElevationData,
                    blending_mode=Metashape.MosaicBlending, # options: AverageBlending, MosaciBlending, MinBlending, MaxBlending, DisabledBlending,
                    fill_holes=True,
                    ghosting_filter=False,
                    cull_faces=False,
                    refine_seamlines=False,
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

        del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")

    build_ortho(project_file)
