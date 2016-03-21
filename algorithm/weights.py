import copy
import math

import algo_utils
import config
from utils import print_verbose, print_json

if config.show_graphs:
    import matplotlib.pyplot as plt
    import seaborn as sns

def calc_contributions(entities, w):
    res = {'F': {}, 'P': {}}
    values_sum = sum([algo_utils.calc_entity_event_value(e, w) for e in entities])
    for e in entities:
        scores_sum = sum([algo_utils.get_indicator_score(a) for a in e['includedAggrFeatureEvents']])
        if scores_sum > 0:
            value = algo_utils.calc_entity_event_value(e, w)
            for a in e['includedAggrFeatureEvents']:
                res[a['type']][a['name']] = res[a['type']].get(a['name'], 0) + \
                                            (100. * algo_utils.get_indicator_score(a) / scores_sum) * (1. * value / values_sum)
    return res

def create_w(initial_w_estimation = {}, overrides = {}):
    if initial_w_estimation != None:
        w = copy.deepcopy(initial_w_estimation)
    else:
        w = {
            'F': {
                "distinct_number_of_src_machines_kerberos_logins_daily": config.BASE_ALPHA,
                "distinct_number_of_dst_machines_kerberos_logins_daily": config.BASE_ALPHA,
                "number_of_failed_kerberos_logins_daily": config.BASE_ALPHA,
                "number_of_successful_kerberos_logins_daily": config.BASE_ALPHA,
                "distinct_number_of_src_machines_ssh_daily": config.BASE_ALPHA,
                "distinct_number_of_dst_machines_ssh_daily": config.BASE_ALPHA,
                "number_of_successful_ssh_daily": config.BASE_ALPHA,
                "number_of_failed_ssh_daily": config.BASE_ALPHA,
                "number_of_failed_vpn_daily": config.BASE_ALPHA,
                "distinct_number_of_src_machines_vpn_daily": config.BASE_ALPHA,
                "distinct_number_of_countries_vpn_daily": config.BASE_ALPHA,
                "number_of_successful_crmsf_events_daily": config.BASE_ALPHA,
                "number_of_failed_crmsf_daily": config.BASE_ALPHA,
                "distinct_number_of_countries_crmsf_daily": config.BASE_ALPHA,
                "number_of_events_wame_daily":config.BASE_ALPHA,
                "number_of_successful_ntlm_events_daily": config.BASE_ALPHA,
                "number_of_failed_ntlm_daily": config.BASE_ALPHA,
                "distinct_number_of_src_machines_ntlm_daily": config.BASE_ALPHA,
                "number_of_events_gwame_daily": config.BASE_ALPHA,
                "distinct_number_of_src_machines_prnlog_daily": config.BASE_ALPHA,
                "distinct_number_of_events_prnlog_daily": config.BASE_ALPHA,
                "distinct_number_of_total_pages_prnlog_daily": config.BASE_ALPHA,
                "distinct_number_of_file_size_bytes_prnlog_daily": config.BASE_ALPHA,
                "number_of_successful_oracle_events_daily": config.BASE_ALPHA,
                "number_of_failed_oracle_daily": config.BASE_ALPHA,
                "distinct_number_of_db_usernames_oracle_daily": config.BASE_ALPHA,
                "distinct_number_of_db_objects_oracle_daily": config.BASE_ALPHA,
                "distinct_number_of_source_machines_oracle_daily": config.BASE_ALPHA,
                "distinct_number_of_destination_machines_oracle_daily": config.BASE_ALPHA,
                "number_of_successful_kerberos_tgt_events_daily": config.BASE_ALPHA,
                "number_of_failed_kerberos_tgt_daily": config.BASE_ALPHA,
                "distinct_number_of_src_machines_kerberos_tgt_daily": config.BASE_ALPHA,
            },
            'P': {
                "sum_of_highest_scores_over_src_machines_kerberos_logins_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_dst_machines_kerberos_logins_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_failure_code_kerberos_logins_daily": config.BASE_BETA,
                "highest_score_over_date_time_events_kerberos_logins_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_src_machines_ssh_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_dst_machines_ssh_daily": config.BASE_BETA,
                "highest_score_over_date_time_events_ssh_daily": config.BASE_BETA,
                "highest_score_over_auth_method_events_ssh_daily": config.BASE_BETA,
                "highest_score_over_date_time_events_vpn_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_src_machines_vpn_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_country_vpn_daily": config.BASE_BETA,
                "sum_of_scores_rate_vpn_session_daily": config.BASE_BETA,
                "highest_score_over_date_time_events_crmsf_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_country_crmsf_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_status_crmsf_daily": config.BASE_BETA,
                "highest_date_time_score_wame_daily": config.BASE_BETA,
                "sum_of_highest_scores_per_action_type_wame_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_src_machines_ntlm_daily": config.BASE_BETA,
                "highest_scores_over_failure_code_ntlm_daily": config.BASE_BETA,
                "highest_event_time_score_ntlm_daily": config.BASE_BETA,
                "highest_score_gwame_daily": config.BASE_BETA,
                "sum_of_pages_prnlog_daily": config.BASE_BETA,
                "sum_of_file_size_prnlog_daily": config.BASE_BETA,
                "highest_score_over_date_time_events_prnlog_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_dst_machines_prnlog_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_src_machines_prnlog_daily": config.BASE_BETA,
                "highest_score_over_date_time_events_oracle_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_db_username_oracle_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_db_object_oracle_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_action_type_oracle_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_return_code_oracle_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_src_machines_oracle_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_dest_machines_oracle_daily": config.BASE_BETA,
                "sum_of_highest_scores_over_src_machines_kerberos_tgt_daily": config.BASE_BETA,
                "highest_scores_over_failure_code_kerberos_tgt_daily": config.BASE_BETA,
                "highest_event_time_score_kerberos_tgt_daily": config.BASE_BETA,
            }
        }

    for pf_type in ['F', 'P']:
        for name, value in overrides.get(pf_type, {}).iteritems():
            if not w[pf_type].has_key(name):
                raise Exception('bad name: ' + name)
            w[pf_type][name] = value
    return w

def plot_contributions(c):
    if not config.show_graphs:
        return
    c = list(c['F'].iteritems()) + list(c['P'].iteritems())
    values = [contribution[1] for contribution in c]
    names = [contribution[0] for contribution in c]
    fig, ax = plt.subplots()
    sns.barplot(x = values,
                y = names,
                order = sorted(names),
                palette = 'Greens').axes.set_xlim(0, 25)

def iterate_weights(entities, is_daily, initial_w_estimation = None, max_allowed_contribution = None):
    if max_allowed_contribution is None:
        max_allowed_contribution = 10
    overrides = {'F': {}, 'P': {}}
    updated = True
    tries = 0
    while updated:
        if (tries == 50):
            print 'failed - exceeded maximum allowed tries -', tries
            break
        tries += 1
        w = create_w(initial_w_estimation = initial_w_estimation, overrides = overrides)
        num_of_weights = len(sum([list(a.itervalues()) for a in w.itervalues()], []))
        print_verbose('min entity event value:', min([algo_utils.calc_entity_event_value(e, w) for e in entities.iterate(is_daily)]))
        top_entities = sum(algo_utils.calc_top_entities_given_w(entities, is_daily, w, max(10, config.NUM_OF_ALERTS_PER_DAY)), [])
        print_verbose('inspecting', len(top_entities), 'entities. The smallest one has value of',
                      min([algo_utils.calc_entity_event_value(e, w) for e in top_entities]))
        c = calc_contributions(top_entities, w)
        plot_contributions(c)
        for pf_type in ['F', 'P']:
            for name in list(c[pf_type].iterkeys()):
                if (config.FIXED_W_DAILY if is_daily else config.FIXED_W_HOURLY)[pf_type].has_key(name):
                    del c[pf_type][name]
        updated = False
        max_contribution = max(sum([list(a.itervalues()) for a in c.itervalues()], []))
        if max_contribution > max(100. / num_of_weights, max_allowed_contribution):
            for pf_type in ['F', 'P']:
                for name, contribution in c[pf_type].iteritems():
                    if not updated and contribution == max_contribution:
                        overrides[pf_type][name] = overrides[pf_type].get(name, w[pf_type][name]) * .8
                        print_verbose('updating', pf_type, name, 'to', overrides[pf_type][name], ': it had contribution', contribution)
                        updated = True

    if not updated:
        print 'finished successfully (maximal contribution is small enough - %f)!' % max_contribution
    print_verbose()
    print_verbose('result overrides:')
    print_json(overrides, False)
    print_verbose()
    print_verbose('result contributions:')
    print_json(c, False)

    return overrides

def penalty_calculator(is_daily, hist_transformer = None):
    def penaltize_bar(score, count):
        score *= .01
        if score < 1:
            score = score ** 3
        return score * count

    def penalty_calculator_wrapper(entities, fs_and_ps, hist):
        if hist_transformer != None:
            hist = hist_transformer(copy.deepcopy(hist))
        median = fs_and_ps.calc_median_hist(is_daily)
        penalty = sum([penaltize_bar(score, max(0, count - median[score])) for score, count in hist.iteritems()])
        return penalty

    return penalty_calculator_wrapper

def calc_w_based_on_penalties(entities, fs_and_ps, is_daily, penalty_calculator):
    w = {'F': {}, 'P': {}}
    max_allowed_w = 0.1
    for pf_type, name, hist in fs_and_ps.iterate(is_daily):
        penalty = penalty_calculator(entities, fs_and_ps, hist)
        #if penalty > 0:
        #    print_verbose(name, '->', penalty)

        #penalty = max(1, penalty)
        #penalty = -math.log(1 - (penalty - 1.) / penalty) / 100 - .05
        penalty = max(0, penalty)
        log_base = 5
        penalty = (1 - 1. / math.log(log_base + penalty / 10.0, log_base)) * 0.5 * max_allowed_w

        penalty = max(0, penalty)
        w[pf_type][name] = (config.BASE_ALPHA - penalty) * (1. * config.BASE_BETA / config.BASE_ALPHA if pf_type == 'P' else 1)

    return w

def give_penalty_and_then_iterate(entities,
                                  fs_and_ps,
                                  is_daily,
                                  penalty_calculator,
                                  max_allowed_contribution = None):
    initial_w_estimation_based_on_penalty = calc_w_based_on_penalties(entities, fs_and_ps, is_daily, penalty_calculator)
    print_verbose('initial w after penalties:')
    print_json(initial_w_estimation_based_on_penalty, False)

    initial_w_estimation_based_on_penalty = create_w(initial_w_estimation = initial_w_estimation_based_on_penalty,
                                                     overrides = (config.FIXED_W_DAILY if is_daily else config.FIXED_W_HOURLY))
    print_verbose('w after fixing:')
    print_json(initial_w_estimation_based_on_penalty, False)

    overrides_based_on_penalty = iterate_weights(entities,
                                                 is_daily = is_daily,
                                                 initial_w_estimation = initial_w_estimation_based_on_penalty,
                                                 max_allowed_contribution = max_allowed_contribution)
    print_verbose()
    print 'final w:'
    w = create_w(initial_w_estimation = initial_w_estimation_based_on_penalty, overrides = overrides_based_on_penalty)
    print_json(w)
    return w

def allow_one_high_score_per_user_per_year(penalty_calculator, is_daily):
    average_daily_fraction_of_active_workers = 0.7

    def penalty_calculator_wrapper(entities, fs_and_ps, hist):
        penalty = penalty_calculator(entities, fs_and_ps, hist)
        penalty -= average_daily_fraction_of_active_workers * len(list(entities.iterate(is_daily = is_daily))) / (365 * (1 if is_daily else 24))
        return penalty

    return penalty_calculator_wrapper