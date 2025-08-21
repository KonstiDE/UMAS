import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress, get_chunk


def build_dem(file, chunk_lab):
    doc = ms.Document()

    doc.open(path=file, read_only=False)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk is not None:
        if chunk.elevation is not None:
            print(f"vd: Chunk {chunk_lab} already has a dem!")
        else:
            chunk.buildDem(
                source_data=Metashape.PointCloudData, # options: TiePointsData, PointCloudData, DepthMapsData, ModelData, TiledModelData, ElevationData, OrthomosaicData, ImagesData
                interpolation=Metashape.EnabledInterpolation,
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

    build_dem(project_file, chunk_label)
