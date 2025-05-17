import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress


def export_orthomosaic(psx_file, dem_file):
    doc = ms.Document()

    doc.open(path=psx_file, read_only=False)

    if os.path.exists(dem_file):
        print("vd: This DEM file already exists!")
        del doc
    else:
        for chunk in doc.chunks:
            if chunk.orthomosaic is not None:
                chunk.buildOrthomosaic(
                    surface_data=Metashape.ElevationData,
                    blending_mode=Metashape.MosaicBlending,
                    # options: AverageBlending, MosaciBlending, MinBlending, MaxBlending, DisabledBlending,
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

        del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    target_file = get_arg(args, "-orthoFile")

    export_orthomosaic(project_file, target_file)
