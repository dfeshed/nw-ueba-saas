import argparse
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers
from bdp_utils.data_sources import data_source_to_score_tables


def _positive_int_type(i):
    if type(i) != int or i < 1:
        raise argparse.ArgumentTypeError('must be positive integer')
    return i


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host,
                                              parsers.start_optional,
                                              parsers.online_manager],
                                     formatter_class=argparse.RawDescriptionHelpFormatter,
                                     prog='step2_online/run',
                                     description=
'''Scoring to aggregation step
---------------------------
Step prerequisites:
    Data should be provided in impala scores tables. The step will run on
    all of the available tables (there's no option to specify a subset of
    data sources).

Step results:
    Raw scored events will be aggregated, and the results will appear in
    mongo collections (all those starting with "aggr_").

Inner workings:
    This step supports running in online mode only. The data will be processed
    by batches (the size is determined by the --batch_size argument) until
    there's no more data available in impala. The processing is done by calling
    fortscale-collection-1.1.0-SNAPSHOT.jar directly (without using BDP).
    Once there's no more data available the script will wait until there's
    more data in the tables specified by the --block_on_data_sources argument
    (once these tables have the data, a new batch will run on all of the data
    sources). Because the script never finishes, and because we don't want to
    validate every batch once it ends (because we don't want the script to wait
    for the validations - we want to start the next batch as soon as possible),
    it's only promised that all the data up until some delay has been validated.
    This delay is controlled by the --validation_batches_delay argument.
    The validations include making sure all events have been processed.
    If --start argument is not specified, it's assumed that step2_online has
    been executed in the past, and the start time will be inferred from
    FeatureBucketMetadata collection, i.e.: if it has already been executed
    before, it must be that there are buckets that haven't been synced yet
    (because they're synced only when an event from the next hour is processed,
    but once this event is processed, a new bucket is opened). If, however,
    this is not the case (there's no FeatureBucketMetadata that hasn't been
    synced), the script will fail.

Usage examples:
    python step2_online/run --start "8 may 1987" --block_on_data_sources ssh ntlm --batch_size 1 --polling_interval 3 --wait_between_batches 0 --min_free_memory_gb 1 6
    ''')
    parser.add_argument('--validation_batches_delay',
                        action='store',
                        dest='validation_batches_delay',
                        help="The delay (in batches) used when validating, i.e. - whenever the n'th batch was sent "
                             "to aggregations, the (n - validation_batches_delay)'th batch is validated. Default is 1",
                        type=_positive_int_type,
                        default=1)
    parser.add_argument('--batch_size',
                        action='store',
                        dest='batch_size',
                        help='The batch size (in hours) to pass to the step',
                        type=int,
                        required=True)
    parser.add_argument('--block_on_data_sources',
                        nargs='+',
                        action='store',
                        dest='block_on_data_sources',
                        help='The data sources to wait for before starting to run a batch. If not specified, '
                             'the data sources which are active all the time (in every single hour) will be '
                             'used',
                        choices=set(data_source_to_score_tables.keys()))
    parser.add_argument('--calc_block_on_tables_based_on_days',
                        action='store',
                        dest='calc_block_on_tables_based_on_days',
                        help='If --block_on_data_sources is not specified, you should specify how many days '
                             'back should be analyzed in order to find what tables to block on',
                        type=int)
    parser.add_argument('--build_models_interval_in_hours',
                        action='store',
                        dest='build_models_interval_in_hours',
                        help='The logic time interval (in hours) for building models. '
                             'If not specified, no models will be built '
                             '(they can, however be built by an external entity).',
                        type=int)
    parser.add_argument('--build_entity_models_interval_in_hours',
                        action='store',
                        dest='build_entity_models_interval_in_hours',
                        help='The logic time interval (in hours) for building entity models. '
                             'If not specified, no entity models will be built '
                             '(they can, however be built by an external entity).',
                        type=int)
    return parser
