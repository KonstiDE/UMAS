import os
import sys

import Metashape
import Metashape as ms

from utils import get_arg, report_progress, get_chunk, rb


def build_point_cloud(file, chunk_lab, quality, depthFiltering, reuseDepthMaps,
                      calculatePointColors, calculatePointConfidence, batch, export_path):

    doc = ms.Document()

    doc.open(path=file, read_only=False, ignore_lock=True)

    chunk = get_chunk(doc.chunks, chunk_lab)

    # Ultra = 1
    # High = 2
    # Medium = 4
    # Low = 8
    # Lowest = 16

    if quality == "Ultra High":
        quality_mode = 1
    elif quality == "High":
        quality_mode = 2
    elif quality == "Medium":
        quality_mode = 4
    elif quality == "Low":
        quality_mode = 8
    elif quality == "Lowest":
        quality_mode = 16
    else:
        quality_mode = 1

    if depthFiltering == "Disabled":
        filter_mode = ms.NoFiltering
    elif depthFiltering == "Mild":
        filter_mode = ms.MildFiltering
    elif depthFiltering == "Moderate":
        filter_mode = ms.ModerateFiltering
    elif depthFiltering == "Aggressive":
        filter_mode = ms.AggressiveFiltering
    else:
        filter_mode = ms.MildFiltering

    batch = rb(batch)

    if chunk.point_cloud is not None and not batch:
        print("ve:BUILD_POINT_CLOUD:Chunk already has a point cloud!~For this chunk, a dense (point) cloud was already processed which cannot be overwritten.~Please remove the current dense (point) cloud.")
        print("vn:BUILD_POINT_CLOUD:false")
    else:
        if batch:
            try:
                chunk.remove(chunk.depth_maps)
                chunk.remove(chunk.point_cloud)
            except Exception as ignored:
                pass

        chunk.buildDepthMaps(
            downscale=quality_mode,
            filter_mode=filter_mode,
            # [, cameras],
            reuse_depth=rb(reuseDepthMaps),
            max_neighbors=16,
            subdivide_task=True,
            workitem_size_cameras=20,
            max_workgroup_size=100,
            progress=report_progress
        )

        chunk.buildPointCloud(
            point_colors=rb(calculatePointColors),
            point_confidence=rb(calculatePointConfidence),
            keep_depth=True,
            max_neighbors=100,
            subdivide_task=True,
            workitem_size_cameras=20,
            max_workgroup_size=100,
            progress=report_progress
        )

        chunk.exportPointCloud(
            path=export_path,
            source_data=ms.PointCloudData,
            format=ms.PointCloudFormatXYZ
        )

        doc.save()

        print("vn:BUILD_POINT_CLOUD:true")

        del doc


if __name__ == '__main__':
    args = sys.argv[1:]

    project_file = get_arg(args, "-psxFile")
    chunk_label = get_arg(args, "-chunk_label")
    batch_edit = get_arg(args, "-batch")

    quality = get_arg(args, "-quality")
    depthFiltering = get_arg(args, "-depthfiltering")
    reuseDepthMaps = get_arg(args, "-reusedepthmaps")
    calculatePointColors = get_arg(args, "-calculatepointcolors")
    calculatePointConfidence = get_arg(args, "-calculatepointconfidence")
    export_path = get_arg(args, "-export_path")

    build_point_cloud(project_file, chunk_label, quality, depthFiltering, reuseDepthMaps,
                      calculatePointColors, calculatePointConfidence, batch_edit, export_path)