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
