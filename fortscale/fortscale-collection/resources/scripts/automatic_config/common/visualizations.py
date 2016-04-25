import copy

import config
from utils.io import print_verbose

if config.show_graphs:
    import matplotlib.pyplot as plt

def show_hist(hist, maxx = 100, bins = 1000, block = True, name = None):
    if name is not None:
        print_verbose(name + ':')
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
    if name is not None:
        fig.canvas.set_window_title(name)
    fig.set_figwidth(20)
    fig.set_figheight(3)
    plt.xlim(0, max(maxx, max(hist.iterkeys())))
    plt.hist(list(hist.iterkeys()),
             weights = list(hist.itervalues()),
             bins = bins,
             histtype = 'stepfilled')
    plt.xlabel('score', fontsize = 20)
    plt.ylabel('count', fontsize = 20)
    (plt if block else fig).show()
