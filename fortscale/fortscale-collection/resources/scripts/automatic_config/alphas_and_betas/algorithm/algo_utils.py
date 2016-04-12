import heapq
from common import algo_utils as common_algo_utils


def calc_entity_event_value(e, w):
    return sum([w[a['type']][a['name']] * common_algo_utils.get_indicator_score(a) * (.01 if a['type'] == 'F' else 1)
                for a in e['includedAggrFeatureEvents']])


def calc_top_entities_given_w(entities, is_daily, w, num_of_entities_per_day):
    def inner(entities, is_daily, w, num_of_entities_per_day):
        return [heapq.nlargest(num_of_entities_per_day, entities_group, key=lambda e: calc_entity_event_value(e, w))
                for entities_group in group_by_day_cache[is_daily]]

    group_by_day_cache = {
        False: entities.group_by_day(False),
        True: entities.group_by_day(True)
    }

    global calc_top_entities_given_w
    calc_top_entities_given_w = inner
    return inner(entities, is_daily, w, num_of_entities_per_day)
