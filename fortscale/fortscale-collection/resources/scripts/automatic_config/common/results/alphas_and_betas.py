import json
from common import config
from json import encoder

encoder.FLOAT_REPR = lambda o: format(o, '.8f')


def update(asl_conf_lines, w):
    j = json.loads(''.join(asl_conf_lines))
    for definition in j['EntityEventDefinitions']:
        cluster_name_to_f_name = {}
        for cluster_name, f_names in definition['entityEventFunction']['clusters'].iteritems():
            if len(f_names) != 1:
                raise Exception('clusters with multiple Fs are not supported yet')
            f_name = f_names[0]
            f_name = f_name[f_name.index('.') + 1:]
            cluster_name_to_f_name[cluster_name] = f_name

        betas = definition['entityEventFunction']['betas']
        weights = w[definition['name']]['P']
        for p_name in betas.iterkeys():
            short_name = p_name[p_name.index('.') + 1:]
            if not weights.has_key(short_name):
                print 'warning:', short_name, 'is not specified in the configuration. using default of', config.BASE_BETA
            betas[p_name] = weights.pop(short_name, config.BASE_BETA)

        alphas = definition['entityEventFunction']['alphas']
        weights = w[definition['name']]['F']
        for cluster_name in alphas.iterkeys():
            f_name = cluster_name_to_f_name[cluster_name]
            if not weights.has_key(f_name):
                print 'warning:', f_name, 'is not specified in the configuration. using default of', config.BASE_ALPHA
            alphas[cluster_name] = weights.pop(f_name, config.BASE_ALPHA)

    for weights in w.itervalues():
        if sum([len(v) for v in weights.itervalues()]) > 0:
            raise Exception('W has illegal weight name')

    return json.dumps(j, indent = 2, sort_keys = True)
