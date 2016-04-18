import copy
import re


def _find_name_to_scorer_names(lines, reducers):
    names = list(reducers.iterkeys())
    name_to_scorer_names = {}
    for l in lines:
        match = re.search('fortscale\.(?:aggr|entity)_event\.(?:.*\.)?(' + '|'.join(names) + ').fortscale.scorers=(.+)', l)
        if match is not None:
            name, scorer_name = match.groups()
            if name_to_scorer_names.has_key(name):
                raise Exception('duplicate name: ' + name)
            if scorer_name in name_to_scorer_names.itervalues():
                raise Exception('duplicate scorer: ' + scorer_name)
            name_to_scorer_names[match.group(1)] = scorer_name
    return name_to_scorer_names

def _update_reducer_if_needed(l, name_to_scorer_names, reducers):
    match = re.search('(fortscale\.(aggr|entity)_event\..*\.(' + '|'.join(name_to_scorer_names.itervalues()) +
                      ')\.reduction\.configs=)', l)
    if match is not None:
        prefix, aggr_or_entity, scorer_name = match.groups()
        name = [entry[0] for entry in name_to_scorer_names.iteritems() if entry[1] == scorer_name][0]
        reducer = reducers.pop(name)
        l = prefix + '{"reductionConfigs":[{"reducingFeatureName":"' + \
            ('aggregated_feature_value' if aggr_or_entity == 'aggr' else 'entity_event_value') + \
            '","reducingFactor":' + str(reducer['reducing_factor']) + ',"maxValueForFullyReduce":' + \
            str(reducer['max_value_for_fully_reduce']) + ',"minValueForNotReduce":' + \
            str(reducer['min_value_for_not_reduce']) + '}]}'
    return l

def _transform_to_reducer_if_needed(l, name_to_scorer_names, reducers):
    match = re.search('fortscale\.(aggr|entity)_event\..*\.(' + '|'.join(name_to_scorer_names.itervalues()) + ')\.', l)
    if match is not None:
        aggr_or_entity = match.group(1)
        scorer_name = match.group(2)
        prefix = l[:l.index('.fortscale') + len('.fortscale')]
        suffix = scorer_name.find('_scorer')
        if suffix < 0:
            print 'warning: scorer name is not according to convention - ' + scorer_name
            suffix = len(scorer_name)
        base_scorer_name = scorer_name[:suffix] + '_base_scorer'
        if l.endswith('output.field.name=score'):
            name = [entry[0] for entry in name_to_scorer_names.iteritems() if entry[1] == scorer_name][0]
            reducer = reducers.pop(name)
            l += '\n' + prefix + '.score.' + scorer_name + '.scorer=low-values-score-reducer' + '\n'
            l += prefix + '.score.' + scorer_name + '.base.scorer=' + base_scorer_name + '\n'
            l += prefix + '.score.' + scorer_name + \
                 '.reduction.configs={"reductionConfigs":[{"reducingFeatureName":"' + \
                 ('aggregated_feature_value' if aggr_or_entity == 'aggr' else 'entity_event_value') + \
                 '","reducingFactor":' + str(reducer['reducing_factor']) + ',"maxValueForFullyReduce":' + \
                 str(reducer['max_value_for_fully_reduce']) + ',"minValueForNotReduce":' + \
                 str(reducer['min_value_for_not_reduce']) + '}]}' + '\n'
            l += prefix + '.score.' + base_scorer_name + '.output.field.name=baseScore'
        else:
            l = l.replace(scorer_name, base_scorer_name)
    return l

def update(conf_lines, reducers):
    reducers = copy.deepcopy(reducers)
    name_to_scorer_names = _find_name_to_scorer_names(conf_lines, reducers)
    with_low_values_scorers = {}
    with_non_low_values_scorers = {}
    for l in conf_lines:
        match = re.search('fortscale\.(?:aggr|entity)_event\..*\.(' + '|'.join(name_to_scorer_names.itervalues()) + ')\.scorer=(.+)', l)
        if match is not None:
            scorer_name, scorer_type = match.groups()
            name = [entry[0] for entry in name_to_scorer_names.iteritems() if entry[1] == scorer_name][0]
            if scorer_type == 'low-values-score-reducer':
                with_low_values_scorers[name] = name_to_scorer_names[name]
            else:
                with_non_low_values_scorers[name] = name_to_scorer_names[name]
    res = ''
    for l in conf_lines:
        l = _update_reducer_if_needed(l, with_low_values_scorers, reducers)
        l = _transform_to_reducer_if_needed(l, with_non_low_values_scorers, reducers)
        res += l + '\n'
    if len(reducers) > 0:
        raise Exception("Some reducers weren't found: " + str(reducers))
    return res
