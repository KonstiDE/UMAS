import Metashape


def version_number():
    print("vn:CHECK_VERSION:Agisoft Metasahpe {}.{}.{}, Build: {}".format(
        Metashape.version.major,
        Metashape.version.minor,
        Metashape.version.micro,
        Metashape.version.build,
    ))


if __name__ == '__main__':
    version_number()
