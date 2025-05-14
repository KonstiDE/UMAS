import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg


def build_point_cloud(file):
    doc = ms.Document()

    doc.open(path=file, read_only=False)

    all_aligned_already = True

    for chunk in doc.chunks:
        if chunk.point_cloud is None:
            all_aligned_already = False

    if all_aligned_already:
        print("vd: All chunks are already aligned!")
        del doc

    else:
        for chunk in doc.chunks:
            if chunk.point_cloud is None:
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

        doc.save()

        del doc


def report_progress(f):
    print("vp: {}".format(str(f)))


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")

    build_point_cloud(project_file)