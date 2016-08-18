import heapq
from common import utils


def calc_entity_event_value(e, w):
    return sum([w[a['type']][a['name']] * utils.score.get_indicator_score(a) * (.01 if a['type'] == 'F' else 1)
                for a in e['includedAggrFeatureEvents']])


_group_by_day_cache = {}
def calc_top_entities_given_w(entities, is_daily, w, num_of_entities_per_day):
    if entities.entity_type not in _group_by_day_cache:
        _group_by_day_cache[entities.entity_type] = {
            False: entities.group_by_day(False),
            True: entities.group_by_day(True)
        }

    return [heapq.nlargest(num_of_entities_per_day, entities_group, key=lambda e: calc_entity_event_value(e, w))
            for entities_group in _group_by_day_cache[entities.entity_type][is_daily]]
