import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress, get_chunk


def build_point_cloud(file, chunk_lab):
    doc = ms.Document()

    doc.open(path=file, read_only=False)

    chunk = get_chunk(doc.chunks, chunk_lab)

    if chunk.dense_cloud is not None:
        print("vd: All chunks already have point cloud!")

    else:
        chunk.buildDepthMaps(
            downscale=1, # 4, options: 1: ultra high, 2: high, 3: not working!!, 4: medium, 5: ?
            filter_mode=Metashape.MildFiltering,
            # options: NoFiltering, MildFiltering, ModerateFiltering, AggressiveFiltering
            # [, cameras],
            reuse_depth=False,
            max_neighbors=16,
            subdivide_task=True,
            workitem_size_cameras=20,
            max_workgroup_size=100,
            progress=report_progress
        )

        chunk.buildDenseCloud(
            point_colors=True,
            point_confidence=True,  # default False
            keep_depth=True,
            max_neighbors=100,
            subdivide_task=True,
            workitem_size_cameras=20,
            max_workgroup_size=100,
            progress=report_progress
        )

        doc.save()

        print("vn:BUILD_POINT_CLOUD:true")

        del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")

    build_point_cloud(project_file, chunk_label)