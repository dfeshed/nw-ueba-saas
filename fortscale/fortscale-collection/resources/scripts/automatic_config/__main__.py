import argparse
import sys

from alphas_and_betas import main as weights_main
from common import config
from common.results.committer import update_configurations
from common.results.store import Store
from fs_reduction import main as fs_main


def print_results():
    store = Store(config.interim_results_path + '/results.json')
    if store.is_empty():
        print 'No results. You should first run command for calculating weights or fs reductions.'
    else:
        print 'Results:'
        print store


def create_parser():
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(help='commands')

    weights_parser = subparsers.add_parser('weights',
                                           help='Calculate alphas and betas for entity events')
    weights_parser.add_argument('--load',
                                action='store_const',
                                dest='cb',
                                const=weights_main.load_data,
                                help='Load needed data from mongo and store it for latter use')
    weights_parser.add_argument('--algo',
                                action='store_const',
                                dest='cb',
                                const=weights_main.run_algo,
                                help='Run the algorithm on already loaded data (use "--load" to load the data from mongo)')
    weights_parser.add_argument('--run',
                                action='store_const',
                                dest='cb',
                                const=weights_main.load_data_and_run_algo,
                                help='Load needed data from mongo and then run the algorithm')

    fs_parser = subparsers.add_parser('fs',
                                      help='Calculate low-values-score-reducer configurations for scored Fs')
    fs_parser.add_argument('--load',
                           action='store_const',
                           dest='cb',
                           const=fs_main.load_data,
                           help='Load needed data from mongo and store it for latter use')
    fs_parser.add_argument('--algo',
                           action='store_const',
                           dest='cb',
                           const=fs_main.run_algo,
                           help='Run the algorithm on already loaded data (use "--load" to load the data from mongo)')
    fs_parser.add_argument('--run',
                           action='store_const',
                           dest='cb',
                           const=fs_main.load_data_and_run_algo,
                           help='Load needed data from mongo and then run the algorithm')

    results_parser = subparsers.add_parser('results',
                                           help='Manipulate the results calculated by the other commands')
    results_parser.add_argument('--show',
                                action='store_const',
                                dest='cb',
                                const=print_results,
                                help='Show the results calculated so far')
    results_parser.add_argument('--commit',
                                action='store_const',
                                dest='cb',
                                const=update_configurations,
                                help='Commit the results to the real production configuration files')

    return parser


if __name__ == '__main__':
    args = sys.argv[1:]
    parser = create_parser()
    arguments = parser.parse_args(args)
    if arguments.cb is None:
        parser.parse_args(args + ['-h'])
    else:
        arguments.cb()
