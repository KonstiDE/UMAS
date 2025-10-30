def get_arg(args, arg):
    return args[args.index(arg) + 1]

def get_chunk(cs, l):
    for c in cs:
        if c.label == l:
            return c

    return None

def rb(s):
    return "true" in s or "True" in s

def report_progress(f):
    print("vp: {}".format(str(f)))

def extend_file_name(file, extension):
    if file[-4:] == ".tif":
        return file.replace(".tif", f"_{extension}.tif")
    elif file[-4:] == ".pdf":
        return file.replace(".pdf", f"_{extension}.pdf")

    return file