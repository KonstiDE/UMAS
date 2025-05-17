import os
import sys

import Metashape
import Metashape as ms

from build_dem import report_progress
from utils import get_arg, report_progress


def export_dem(psx_file, dem_file):
    doc = ms.Document()

    doc.open(path=psx_file, read_only=False)

    if os.path.exists(dem_file):
        print("vd: This DEM file already exists!")
        del doc
    else:
        for chunk in doc.chunks:
            if chunk.elevation is not None:
                chunk.buildDem(
                    source_data=Metashape.PointCloudData, # options: TiePointsData, PointCloudData, DepthMapsData, ModelData, TiledModelData, ElevationData, OrthomosaicData, ImagesData
                    interpolation=Metashape.EnabledInterpolation,
                    # [, projection ]
                    # [, region ]
                    # [,classes],
                    flip_x=False,
                    flip_y=False,
                    flip_z=False,
                    resolution=0,
                    subdivide_task=True,
                    workitem_size_tiles=10,
                    max_workgroup_size=100,
                    progress=report_progress
                )

        del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    target_file = get_arg(args, "-demFile")

    export_dem(project_file, target_file)
