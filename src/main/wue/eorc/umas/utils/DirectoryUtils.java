package wue.eorc.umas.utils;

import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.ImageType;
import wue.eorc.umas.enums.ProcessingChain;
import wue.eorc.umas.enums.Sensor;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.Flight;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DirectoryUtils {

    public static boolean createFolderStructure(Flight flight) throws UMASException {
        File baseDir = new File(flight.getFlightDirectory());

        return switch (flight.getProcessingChain()){
            case AGISOFT -> {
                List<File> files = switch (flight.getUav()){
                    case MAVICM2, PHAMTOM -> List.of(
                            createDeepFolder(baseDir, "0_Images", "0_RGB"),
                            createDeepFolder(baseDir, "1_Agisoft"),
                            createDeepFolder(baseDir, "2_Reports"),
                            createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                            createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                            createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                            createDeepFolder(baseDir, "4_RawOutput")
                    );
                    case MAVICM3M -> List.of(
                            createDeepFolder(baseDir, "0_Images", "0_RGB"),
                            createDeepFolder(baseDir, "0_Images", "1_MS"),
                            createDeepFolder(baseDir, "0_Images", "2_CALIB"),
                            createDeepFolder(baseDir, "1_Agisoft"),
                            createDeepFolder(baseDir, "2_Reports"),
                            createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                            createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                            createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                            createDeepFolder(baseDir, "4_RawOutput")
                    );
                    case MAVICM3T, MAVICM4T ->  List.of(
                            createDeepFolder(baseDir, "0_Images", "0_RGB"),
                            createDeepFolder(baseDir, "0_Images", "1_T"),
                            createDeepFolder(baseDir, "0_Images", "2_TCal"),
                            createDeepFolder(baseDir, "1_Agisoft"),
                            createDeepFolder(baseDir, "2_Reports"),
                            createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                            createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                            createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                            createDeepFolder(baseDir, "4_RawOutput")
                    );
                    case M300 -> switch (flight.getSensor()){
                        case ALTUM, MXDUAL -> List.of(
                                createDeepFolder(baseDir, "0_Images", "0_RGB"),
                                createDeepFolder(baseDir, "0_Images", "1_MS"),
                                createDeepFolder(baseDir, "1_Agisoft"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        case H20T -> List.of(
                                createDeepFolder(baseDir, "0_Images", "0_RGB"),
                                createDeepFolder(baseDir, "0_Images", "1_T"),
                                createDeepFolder(baseDir, "0_Images", "2_TCal"),
                                createDeepFolder(baseDir, "1_Agisoft"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        default -> null;

                    };
                    case M600 -> switch (flight.getSensor()){
                        case ALTUM, MXDUAL ->  List.of(
                                createDeepFolder(baseDir, "0_Images"),
                                createDeepFolder(baseDir, "1_Agisoft"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        case NANOHP ->  List.of(
                                createDeepFolder(baseDir, "0_Images"),
                                createDeepFolder(baseDir, "1_NHProjects"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_GNSS"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        default -> null;
                    };
                    case WINGTRA -> switch (flight.getSensor()){
                        case ALTUM ->  List.of(
                                createDeepFolder(baseDir, "0_Images", "0_RGB"),
                                createDeepFolder(baseDir, "0_Images", "1_MS"),
                                createDeepFolder(baseDir, "1_Agisoft"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        case NIKONRGB -> List.of(
                                createDeepFolder(baseDir, "0_Images", "0_RGB"),
                                createDeepFolder(baseDir, "1_Agisoft"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        default -> null;
                    };
                    case TRINITY -> switch (flight.getSensor()){
                        case ALTUMPT ->  List.of(
                                createDeepFolder(baseDir, "0_Images"),
                                createDeepFolder(baseDir, "1_Agisoft"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        case D2M -> List.of(
                                createDeepFolder(baseDir, "0_Images", "A"),
                                createDeepFolder(baseDir, "0_Images", "D"),
                                createDeepFolder(baseDir, "0_Images", "S"),
                                createDeepFolder(baseDir, "0_Images", "W"),
                                createDeepFolder(baseDir, "0_Images", "X"),
                                createDeepFolder(baseDir, "1_Agisoft"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        case Q2 -> List.of(
                                createDeepFolder(baseDir, "0_YSData"),
                                createDeepFolder(baseDir, "1_Base", "0_Raw"),
                                createDeepFolder(baseDir, "1_Base", "1_Processed", "EMLID"),
                                createDeepFolder(baseDir, "1_Base", "1_Processed", "QBASE"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_TrinityLog"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        default -> null;
                    };
                };

                if(files != null){
                    yield files.stream().allMatch(File::mkdirs);
                }else{
                    throw new UMASException(ErrorType.INTERNAL, "Could not find folder stucture for combination of UAV \"" + flight.getUav() + "\" and sensor \"" +  flight.getSensor() + "\"");
                }
            }
            case TERRA -> {
                List<File> files = switch (flight.getUav()){
                    case MAVICM2 -> List.of(
                            createDeepFolder(baseDir, "0_Images"),
                            createDeepFolder(baseDir, "1_TerraFiles"),
                            createDeepFolder(baseDir, "2_Reports"),
                            createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                            createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                            createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                            createDeepFolder(baseDir, "4_RawOutput")
                    );
                    case MAVICM3M -> List.of(
                            createDeepFolder(baseDir, "0_Images", "0_RGB"),
                            createDeepFolder(baseDir, "0_Images", "1_MS"),
                            createDeepFolder(baseDir, "0_Images", "2_CALIB"),
                            createDeepFolder(baseDir, "1_TerraFiles"),
                            createDeepFolder(baseDir, "2_Reports"),
                            createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                            createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                            createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                            createDeepFolder(baseDir, "4_RawOutput")
                    );
                    case MAVICM3T, MAVICM4T ->  List.of(
                            createDeepFolder(baseDir, "0_Images", "0_RGB"),
                            createDeepFolder(baseDir, "0_Images", "1_T"),
                            createDeepFolder(baseDir, "0_Images", "2_TCal"),
                            createDeepFolder(baseDir, "1_TerraFiles"),
                            createDeepFolder(baseDir, "2_Reports"),
                            createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                            createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                            createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                            createDeepFolder(baseDir, "4_RawOutput")
                    );
                    case PHAMTOM ->  List.of(
                            createDeepFolder(baseDir, "0_Images", "0_RGB"),
                            createDeepFolder(baseDir, "1_TerraFiles"),
                            createDeepFolder(baseDir, "2_Reports"),
                            createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                            createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                            createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                            createDeepFolder(baseDir, "4_RawOutput")
                    );
                    case M300 -> switch (flight.getSensor()){
                        case ALTUM, MXDUAL -> List.of(
                                createDeepFolder(baseDir, "0_Images"),
                                createDeepFolder(baseDir, "1_TerraFiles"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        case H20T -> List.of(
                                createDeepFolder(baseDir, "0_Images", "0_RGB"),
                                createDeepFolder(baseDir, "0_Images", "1_T"),
                                createDeepFolder(baseDir, "0_Images", "2_TCal"),
                                createDeepFolder(baseDir, "1_TerraFiles"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        default -> null;

                    };
                    case M600 -> switch (flight.getSensor()){
                        case ALTUM, MXDUAL ->  List.of(
                                createDeepFolder(baseDir, "0_Images"),
                                createDeepFolder(baseDir, "1_TerraFiles"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        case LIAIRV ->  List.of(
                                createDeepFolder(baseDir, "0_BaseStation"),
                                createDeepFolder(baseDir, "1_LiAirVData"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        case NANOHP ->  List.of(
                                createDeepFolder(baseDir, "0_Images"),
                                createDeepFolder(baseDir, "1_NHProjects"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_GNSS"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        default -> null;
                    };
                    case WINGTRA -> switch (flight.getSensor()){
                        case ALTUM, NIKONRGB ->  List.of(
                                createDeepFolder(baseDir, "0_Images"),
                                createDeepFolder(baseDir, "1_TerraFiles"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        default -> null;
                    };
                    case TRINITY -> switch (flight.getSensor()){
                        case ALTUMPT ->  List.of(
                                createDeepFolder(baseDir, "0_Images"),
                                createDeepFolder(baseDir, "1_TerraFiles"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        case D2M -> List.of(
                                createDeepFolder(baseDir, "0_Images", "A"),
                                createDeepFolder(baseDir, "0_Images", "D"),
                                createDeepFolder(baseDir, "0_Images", "S"),
                                createDeepFolder(baseDir, "0_Images", "W"),
                                createDeepFolder(baseDir, "0_Images", "X"),
                                createDeepFolder(baseDir, "1_TerraFiles"),
                                createDeepFolder(baseDir, "2_Reports"),
                                createDeepFolder(baseDir, "3_FlightFiles", "0_Log"),
                                createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                                createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                                createDeepFolder(baseDir, "4_RawOutput")
                        );
                        default -> null;
                    };
                };

                if(files != null){
                    yield files.stream().allMatch(File::mkdirs);
                }else{
                    throw new UMASException(ErrorType.INTERNAL, "Could not find folder stucture for combination of UAV \"" + flight.getUav() + "\" and sensor \"" +  flight.getSensor() + "\"");
                }
            }
            case OTHER -> {
                List<File> files = flight.getSensor() == Sensor.Q2 ?
                    List.of(
                            createDeepFolder(baseDir, "0_YSData"),
                            createDeepFolder(baseDir, "1_Base", "0_Raw"),
                            createDeepFolder(baseDir, "1_Base", "1_Processed", "EMLID"),
                            createDeepFolder(baseDir, "1_Base", "1_Processed", "QBASE"),
                            createDeepFolder(baseDir, "2_Reports"),
                            createDeepFolder(baseDir, "3_FlightFiles", "0_TrinityLog"),
                            createDeepFolder(baseDir, "3_FlightFiles", "1_Plan"),
                            createDeepFolder(baseDir, "3_FlightFiles", "2_Other"),
                            createDeepFolder(baseDir, "4_RawOutput")
                    ) : null;
                if(files != null){
                    yield files.stream().allMatch(File::mkdirs);
                }else{
                    throw new UMASException(ErrorType.INTERNAL, "Could not find folder stucture for combination of UAV \"" + flight.getUav() + "\" and sensor \"" +  flight.getSensor() + "\"");
                }
            }
            case BOTH -> false;
        };

    }

    private static File createDeepFolder(File baseDir, String... toThePeterCopter){
        return Paths.get(baseDir.getAbsolutePath(), toThePeterCopter).toFile();
    }

    public static void fillImageTypeDirs(Flight flight){
        for (ImageType type : flight.getImageTypes().keySet()) {
            switch (type) {
                case RGB -> {
                    File rgbDir = Paths.get(flight.getFlightDirectory(), "0_Images", "0_RGB").toFile();
                    if(rgbDir.exists()) flight.getImageTypes().put(type, rgbDir.getAbsolutePath());
                }
                case MULTISPECTRAL -> {
                    File msDir = Paths.get(flight.getFlightDirectory(), "0_Images", "1_MS").toFile();
                    if(msDir.exists()) flight.getImageTypes().put(type, msDir.getAbsolutePath());
                }
                case CALIBRATION -> {
                    File calibDir = Paths.get(flight.getFlightDirectory(), "0_Images", "2_CALIB").toFile();
                    if(calibDir.exists()) flight.getImageTypes().put(type, calibDir.getAbsolutePath());
                }
                case IR -> {
                    File irDir1 = Paths.get(flight.getFlightDirectory(), "0_Images", "1_T").toFile();
                    File irDir2 = Paths.get(flight.getFlightDirectory(), "0_Images", "1_TCal").toFile();
                    if(irDir1.exists() && irDir2.exists()) flight.getImageTypes().put(type, irDir2.getAbsolutePath());
                }
            }
        }

    }

    public static Path getCacheFolder(final String osName) {
        if (osName.toLowerCase().contains("mac")) {
            return Paths.get(System.getProperty("user.home"), "Library", "Caches");
        }

        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return Paths.get(System.getProperty("user.home"), ".cache");
        }

        if (osName.contains("indows")) {
            return Paths.get(System.getenv("LOCALAPPDATA"));
        }

        return Paths.get(System.getProperty("user.home"), "caches");
    }

    public static File initCache() throws IOException {
        File cacheFolder = Paths.get(
                getCacheFolder(System.getProperty("os.name")).toFile().getAbsolutePath(),
                "UMAS"
        ).toFile();

        if (!cacheFolder.exists()) {
            boolean success = cacheFolder.mkdir();
            if (!success) {
                throw new IOException("Unable to create cache dir under \"" + cacheFolder.getAbsolutePath() + "\"");
            }
        }
        return cacheFolder;
    }

    public static String figureAgisoftFilePath(Flight flight) {
        return Paths.get(flight.getFlightDirectory(), flight.getProcessingChain() == ProcessingChain.AGISOFT ?
                        "1_Agisoft/" + flight.getProjectFileNameAgisoft() :
                        "2_Agisoft/" + flight.getProjectFileNameAgisoft()).toFile().getAbsolutePath();
    }

    public static String figureExportPath(Flight flight) {
        return Paths.get(flight.getFlightDirectory(), flight.getProcessingChain() == ProcessingChain.AGISOFT ?
                "4_RawOutput/" : "5_RawOutput/").toFile().getAbsolutePath();
    }

    public static String figureReportPath(Flight flight) {
        return Paths.get(flight.getFlightDirectory(), flight.getProcessingChain() == ProcessingChain.AGISOFT ?
                "2_Reports/": "3_Reports/").toFile().getAbsolutePath();
    }

}
