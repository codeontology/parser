import argparse
import sys
import subprocess
import shutil


def init(args):
    """
    Run program.
    :param args:    The program arguments.
    :return:        Nothing, really.
    """
    jars = "target/*"
    main_class = "org.codeontology.frontend.JPPFrontEnd"
    mem_ctrl = "-Xmx3036M"
    java_args = ["java", "-cp", jars, mem_ctrl, main_class]

    java_args.extend(args[0:-2])

    if args[-1] == "d":
        java_args.extend(["d"])
    else:
        java_args.extend(["nd"])

    subprocess.call(java_args)


def parse(args):
    parser = argparse.ArgumentParser(description='Process some integers.')
    parser.add_argument('out', metavar='out', type=str, nargs=1)
    parser.add_argument('src', metavar='src', type=str, nargs=1)
    parser.add_argument('download', metavar='download', type=str, nargs=1)
    parser.add_argument('cp', metavar='cp', type=str, nargs='*')

    parsed_args = parser.parse_args(args)

    src = parsed_args.src[0]
    out = parsed_args.out[0]
    download = parsed_args.download[0]

    if len(parsed_args.cp) == 0:
        cp = ""
    else:
        cp = parsed_args.cp

    args_list = [src, out, cp, download]

    return args_list


if __name__ == '__main__':
    args = parse(sys.argv[1:])
    init(args)
    shutil.rmtree("./spooned")
    shutil.rmtree("./.rdf")
