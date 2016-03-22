import copy

import config
from utils import print_verbose

if config.show_graphs:
    import matplotlib.pyplot as plt

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

def plot_threshold_effect(hists):
    value_thresholds = sorted([0] + [v + 1 for v in list(hists[False].iterkeys()) + list(hists[True].iterkeys())])
    false_eliminated = {}
    true_preserved = {}
    for value_threshold in value_thresholds:
        false_eliminated[value_threshold] = sum([count for value, count in hists[False].iteritems() if value < value_threshold])
        true_preserved[value_threshold] = sum([count for value, count in hists[True].iteritems() if value >= value_threshold])
    print_verbose('true preserved:', true_preserved)
    print_verbose('false eliminated:', false_eliminated)
    if not config.show_graphs:
        return
    plt.figure()
    plt.xlabel('False Positive Eliminated')
    plt.ylabel('True Positive Preserved')
    plt.xlim([0.0, max(false_eliminated.itervalues()) + 1])
    plt.ylim([0.0, max(true_preserved.itervalues()) + 1])
    plt.plot([false_eliminated[v] for v in value_thresholds],
             [true_preserved[v] for v in value_thresholds],
             '-o')
    for xy, label in [((false_eliminated[v], true_preserved[v]), v) for v in value_thresholds]:
        plt.annotate(label, xy = xy, textcoords = 'data', fontsize = 14)
    plt.show()
