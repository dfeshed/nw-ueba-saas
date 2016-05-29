import datetime
import time
import sys
import os

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from common.utils.io import print_verbose
from fs_reduction.algorithm.fs_reduction import calc_fs_reducers
from fs_reduction.data.fs import Fs
from common.results.store import Store
from common import config
from common.utils.score import score_to_weight_linear


def _main(should_query, should_run_algo):
    script_start_time = time.time()
    store = Store(config.interim_results_path + '/results.json')
    fs = Fs(config.interim_results_path + '/fs', config.mongo_ip)
    if should_query:
        fs.query(config.START_TIME, config.END_TIME, should_save_every_day = True)
    if should_run_algo:
        fs_reducers = calc_fs_reducers(score_to_weight_linear, fs = fs)
        store.set('fs_reducers', fs_reducers)
    print_verbose("The script's run time was", datetime.timedelta(seconds = int(time.time() - script_start_time)))

def load_data():
    _main(should_query = True, should_run_algo = False)

def run_algo():
    _main(should_query = False, should_run_algo = True)

def load_data_and_run_algo():
    _main(should_query = True, should_run_algo = True)
