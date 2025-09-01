def get_arg(args, arg):
    return args[args.index(arg) + 1]

def get_chunk(cs, l):
    for c in cs:
        if c.label == l:
            return c

    return None

def rb(s):
    return "True" in s

def report_progress(f):
    print("vp: {}".format(str(f)))