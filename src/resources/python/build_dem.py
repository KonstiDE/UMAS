import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress, get_chunk


def build_dem(file, chunk_lab, coordinate_system, source_data, quality, interpolation):
    doc = ms.Document()

    doc.open(path=file, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    chunk.crs = ms.CoordinateSystem(coordinate_system)

    if source_data == "Tie points":
        source_data_mode = ms.TiePointsData
    elif source_data == "Depth maps":
        source_data_mode = ms.DepthMapsData
    elif source_data == "Dense cloud":
        source_data_mode = ms.PointCloudData
    else:
        source_data_mode = ms.PointCloudData

    if interpolation == "Enabled (default)":
        interpolation_mode = ms.EnabledInterpolation
    elif interpolation == "Disabled":
        interpolation_mode = ms.DisabledInterpolation
    elif interpolation == "Extrapolated":
        interpolation_mode = ms.ExtrapolatedInterpolation
    else:
        interpolation_mode = ms.EnabledInterpolation


    if chunk is not None:
        if chunk.elevation is not None:
            print(f"vd: Chunk {chunk_lab} already has a dem!")
        else:
            chunk.buildDem(
                source_data=source_data_mode, # options: TiePointsData, PointCloudData, DepthMapsData, ModelData, TiledModelData, ElevationData, OrthomosaicData, ImagesData
                interpolation=interpolation_mode,
                #[, projection ]
                #[, region ]
                #[,classes],
                flip_x=False,
                flip_y=False,
                flip_z=False,
                resolution=0,
                subdivide_task=True,
                workitem_size_tiles=10,
                max_workgroup_size=100,
                progress=report_progress
            )

            doc.save()

            print("vn:BUILD_DEM:true")

    else:
        print("vn:BUILD_DEM:false")

    del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    coordinate_system = get_arg(args, "-coordinatesystem")
    source_data = get_arg(args, "-sourcedata")
    quality = get_arg(args, "-quality")
    interpolation = get_arg(args, "-interpolation")

    build_dem(project_file, chunk_label, coordinate_system, source_data, quality, interpolation)
