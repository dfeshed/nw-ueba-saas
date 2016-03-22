import copy

import config
from utils import print_verbose

if config.show_graphs:
    import matplotlib.pyplot as plt

def normalize_hist_by_unreliability(hist):
    res = {}
    non_empty_scores = sorted(hist.iterkeys(), reverse = True)
    while len(non_empty_scores) > 0:
        score = non_empty_scores.pop(0)
        max_count = hist.get(score)
        while max_count >= 1:
            max_count = max(max_count * .9, hist.get(score, 0))
            res[score] = max_count
            if len(non_empty_scores) > 0 and non_empty_scores[0] == score:
                non_empty_scores.pop(0)
            score -= 1
    return res

def show_hist(hist, maxx = 100):
    print_verbose('Area under histogram:', sum(hist.itervalues()))
    print_verbose(hist)
    if not config.show_graphs:
        return
    if len(hist) < 2:
        # if hist has only one entry, matplotlib will fail to plot it:
        hist = copy.deepcopy(hist)
        hist[0] = hist.get(0, 0.00001)
        hist[1] = hist.get(1, 0.00001)
    fig, ax = plt.subplots()
    fig.set_figwidth(20)
    fig.set_figheight(3)
    plt.xlim(0, max(maxx, max(hist.iterkeys())))
    plt.hist(list(hist.iterkeys()),
             weights = list(hist.itervalues()),
             bins = 1000,
             histtype = 'stepfilled')
    plt.xlabel('score', fontsize = 20)
    plt.ylabel('count', fontsize = 20)
    plt.show()