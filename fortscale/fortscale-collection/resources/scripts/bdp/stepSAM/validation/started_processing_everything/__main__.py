import argparse
import logging
import os
import sys

from validation import validate_started_processing_everything

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils import parsers, colorer


def create_parser():
    return argparse.ArgumentParser(parents=[parsers.host,
                                            parsers.data_source_mandatory,
                                            parsers.end_optional])


if __name__ == '__main__':
    colorer.colorize()
    logger = logging.getLogger('stepSAM.validation')
    logging.basicConfig(format='%(message)s')
    logger.setLevel(logging.INFO)

    parser = create_parser()
    arguments = parser.parse_args()
    if not validate_started_processing_everything(host=arguments.host,
                                                  data_source=arguments.data_source,
                                                  end_time_epoch=arguments.end,
                                                  allowed_gap_from_end_in_seconds=0,
                                                  stuck_timeout_inside_allowed_gap_in_seconds=-1,
                                                  stuck_timeout_outside_allowed_gap_in_seconds=-1):
        sys.exit(1)
