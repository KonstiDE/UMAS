def get_arg(args, arg):
    return args[args.index(arg) + 1]

def report_progress(f):
    print("vp: {}".format(str(f)))