import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress, get_chunk


def build_ortho(file, chunk_lab):
    doc = ms.Document()

    doc.open(path=file, read_only=False)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk.orthomosaic is not None:
        print(f"vd: Chunk {chunk_lab} already has a orthomosaic!")

    else:
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

        print("vn:BUILD_ORTHOMOSAIC:true")

        del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    build_ortho(project_file, chunk_label)
