import copy
import json
import re


def _find_name_to_scorer_names(lines):
    name_to_scorer_names = {}
    for l in lines:
        match = re.search('fortscale\.(?:aggr|entity)_event\.(?:.*\.)?(.*).fortscale.scorers=(.+)', l)
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

def _create_noop_reducer():
    return {
        'min_value_for_not_reduce': 0,
        'max_value_for_fully_reduce': 0,
        'reducing_factor': 1
    }

def update(conf_lines, reducers):
    reducers = copy.deepcopy(reducers)
    name_to_scorer_names = _find_name_to_scorer_names(conf_lines)
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
                if reducers.has_key(name):
                    with_non_low_values_scorers[name] = name_to_scorer_names[name]
    for name in name_to_scorer_names.iterkeys():
        if not reducers.has_key(name) and with_low_values_scorers.has_key(name):
            # any reducer in the config file which is not specified in the results file should be deactivated
            reducers[name] = _create_noop_reducer()
    res = ''
    for l in conf_lines:
        l = _update_reducer_if_needed(l, with_low_values_scorers, reducers)
        l = _transform_to_reducer_if_needed(l, with_non_low_values_scorers, reducers)
        res += l + '\n'
    return res

def _apply_reducer(scorer_conf, reducer):
    if scorer_conf['type'] == 'low-values-score-reducer':
        reduction_configs = scorer_conf['reduction-configs']
        if len(reduction_configs) != 1:
            raise Exception('low-values-score-reducers with multiple reduction-configs are not supported')
        reduction_config = reduction_configs[0]
        reduction_config['maxValueForFullyReduce'] = reducer['max_value_for_fully_reduce']
        reduction_config['reducingFactor'] = reducer['reducing_factor']
        reduction_config['minValueForNotReduce'] = reducer['min_value_for_not_reduce']
    else:
        raise Exception('aggregation scorers without low-values-score-reducers already used are not supported')

def update27(conf_lines, reducers):
    conf = json.loads('\n'.join(conf_lines))
    for scorers_conf in conf['data-source-scorers']:
        if len(scorers_conf['scorers']) != 1:
            raise Exception('aggregations with multiple scorers are not supported')
        scorer_conf = scorers_conf['scorers'][0]
        data_source = scorers_conf['data-source']
        f_name = data_source[data_source.rindex('.') + 1:]
        for reducer_name, reducer in reducers.iteritems():
            if reducer_name == f_name:
                _apply_reducer(scorer_conf, reducer)
                reducers.pop(reducer_name)
                break
        else:
            if scorer_conf['type'] == 'low-values-score-reducer':
                # any reducer in the config file which is not specified in the results file should be deactivated
                _apply_reducer(scorer_conf, _create_noop_reducer())
    return json.dumps(conf, indent=4)
