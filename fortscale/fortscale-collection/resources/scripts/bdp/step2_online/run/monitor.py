import os
import subprocess
import sys

from parse import create_parser


def _run_step2(user, password, args):
    ps_p = subprocess.Popen(['echo', '-e', user + '\n' + password], stdout=subprocess.PIPE)
    subprocess.call(['python', os.path.sep.join([os.path.dirname(os.path.abspath(__file__))])] + args, stdin=ps_p.stdout)


if __name__ == '__main__':
    # use the parser so the user will get the same experience as running
    # step2_online directly, e.g. printing the usage with "-h"
    arguments = create_parser().parse_args()
    args = sys.argv[1:]
    if not sys.stdin.isatty():
        user = sys.stdin.readline().strip()
        password = sys.stdin.readline().strip()
    if arguments.start is not None:
        _run_step2(args)
        i = args.index('--start')
        args.pop(i + 1)
        args.pop(i)
    while True:
        _run_step2(args)
