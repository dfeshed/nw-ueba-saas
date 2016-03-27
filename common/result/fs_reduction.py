import re


def _find_f_name_to_scorer_names(lines, reducers):
    f_names = list(reducers.iterkeys())
    f_name_to_scorer_names = {}
    for l in lines:
        match = re.search('fortscale\.aggr_event\..*\.(' + '|'.join(f_names) + ').fortscale.scorers=(.+)', l)
        if match is not None:
            f_name, scorer_name = match.groups()
            if f_name_to_scorer_names.has_key(f_name):
                raise Exception('duplicate F name: ' + f_name)
            if scorer_name in f_name_to_scorer_names.itervalues():
                raise Exception('duplicate scorer: ' + scorer_name)
            f_name_to_scorer_names[match.group(1)] = scorer_name
    return f_name_to_scorer_names

def _update_reducer_if_needed(l, f_name_to_scorer_names, reducers):
    match = re.search('(fortscale\.aggr_event\..*\.(' + '|'.join(f_name_to_scorer_names.itervalues()) +
                      ')\.reduction\.configs=)', l)
    if match is not None:
        prefix, scorer_name = match.groups()
        f_name = [entry[0] for entry in f_name_to_scorer_names.iteritems() if entry[1] == scorer_name][0]
        reducer = reducers[f_name]
        l = prefix + '{"reductionConfigs":[{"reducingFeatureName":"aggregated_feature_value","reducingFactor":' + \
            str(reducer['reducing_factor']) + ',"maxValueForFullyReduce":' + str(reducer['max_value_for_fully_reduce']) + \
            ',"minValueForNotReduce":' + str(reducer['min_value_for_not_reduce']) + '}]}' + '\n'
    return l

def _transform_to_reducer_if_needed(l, f_name_to_scorer_names, reducers):
    match = re.search('fortscale\.aggr_event\..*\.(' + '|'.join(f_name_to_scorer_names.itervalues()) + ')\.', l)
    if match is not None:
        scorer_name = match.group(1)
        prefix = l[:l.index('.fortscale') + len('.fortscale')]
        suffix = scorer_name.find('_scorer')
        if suffix < 0:
            print 'warning: scorer name is not according to convention - ' + scorer_name
            suffix = len(scorer_name)
        base_scorer_name = scorer_name[:suffix] + '_base_scorer'
        if l.endswith('output.field.name=score\n'):
            f_name = [entry[0] for entry in f_name_to_scorer_names.iteritems() if entry[1] == scorer_name][0]
            reducer = reducers[f_name]
            l += prefix + '.score.' + scorer_name + '.scorer=low-values-score-reducer' + '\n'
            l += prefix + '.score.' + scorer_name + '.base.scorer=' + base_scorer_name + '\n'
            l += prefix + '.score.' + scorer_name + \
                 '.reduction.configs={"reductionConfigs":[{"reducingFeatureName":"aggregated_feature_value","reducingFactor":' + \
                 str(reducer['reducing_factor']) + ',"maxValueForFullyReduce":' + str(reducer['max_value_for_fully_reduce']) + \
                 ',"minValueForNotReduce":' + str(reducer['min_value_for_not_reduce']) + '}]}' + '\n'
            l += prefix + '.score.' + base_scorer_name + '.output.field.name=baseScore' + '\n'
        else:
            l = l.replace(scorer_name, base_scorer_name)
    return l

def update(conf_lines, reducers):
    f_name_to_scorer_names = _find_f_name_to_scorer_names(conf_lines, reducers)
    fs_with_low_values_scorer_to_scorer_name = {}
    fs_with_non_low_values_scorer_to_scorer_name = {}
    for l in conf_lines:
        match = re.search('fortscale\.aggr_event\..*\.(' + '|'.join(f_name_to_scorer_names.itervalues()) + ')\.scorer=(.+)', l)
        if match is not None:
            scorer_name, scorer_type = match.groups()
            f_name = [entry[0] for entry in f_name_to_scorer_names.iteritems() if entry[1] == scorer_name][0]
            if scorer_type == 'low-values-score-reducer':
                fs_with_low_values_scorer_to_scorer_name[f_name] = f_name_to_scorer_names[f_name]
            else:
                fs_with_non_low_values_scorer_to_scorer_name[f_name] = f_name_to_scorer_names[f_name]
    res = ''
    for l in conf_lines:
        l = _update_reducer_if_needed(l, fs_with_low_values_scorer_to_scorer_name, reducers)
        l = _transform_to_reducer_if_needed(l, fs_with_non_low_values_scorer_to_scorer_name, reducers)
        res += l
    return res