import datetime
import sys
import time

sys.path.append('..')
from common.utils import print_verbose
from fs_reduction.algorithm.fs_reduction import calc_fs_reducers, score_to_weight_linear
from fs_reduction.data.fs import Fs
from common.result.store import Store
from common import config


def main():
    start_time = time.time()
    store = Store('store.json')
    fs = Fs('mongo/fs')
    fs.query(config.mongo_ip)
    fs_reducers = calc_fs_reducers(score_to_weight_linear, fs = fs)
    print_verbose("The script's run time was", datetime.timedelta(seconds = int(time.time() - start_time)))
    store.set('fs_reducers', fs_reducers)
    return fs, fs_reducers

if __name__ == '__main__':
    main()
