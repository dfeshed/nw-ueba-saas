import Immutable from 'seamless-immutable';
import formatOptions from '../data/subscriptions/log-parser-rules/ruleFormats/data';
import metaOptions from '../data/subscriptions/rule-meta/findAll/data';

const _set = (obj, key, val) => {
  if (obj[key]) {
    obj[key] = val;
    return;
  }

  const keys = key.split('.');
  const firstKey = keys.shift();

  if (!obj[firstKey]) {
    obj[firstKey] = {};
  }

  if (keys.length === 0) {
    obj[firstKey] = val;
    return;
  } else {
    _set(obj[firstKey], keys.join('.'), val);
  }
};

const parserRules = [{
  name: 'ipv4',
  outOfBox: false,
  literals: [{ value: 'test' }],
  pattern: {
    captures: [{
      key: 'domain.src',
      index: '0',
      format: 'IPv6'
    }],
    format: 'ipv6',
    regex: ''
  }
}];

export default class DataHelper {
  constructor(setState) {
    this.state = {};
    this.setState = setState;
  }

  _setBaseState() {
    _set(this.state, 'content.logParserRules.logParsers', [{ name: 'builtin' }]);
    _set(this.state, 'content.logParserRules.logParsersStatus', 'completed');
    _set(this.state, 'content.logParserRules.selectedLogParserIndex', 0);
    _set(this.state, 'content.logParserRules.parserRules', parserRules);
    _set(this.state, 'content.logParserRules.parserRulesOriginal', parserRules);
  }

  // Trigger setState, also return the resulting state
  // in case it needs to be used/checked
  build() {
    const state = Immutable.from({
      configure: this.state
    });
    this.setState(state);
    return state.asMutable();
  }

  parserRules(rules, selectedIndex = 0) {
    _set(this.state, 'content.logParserRules.parserRules', rules);
    _set(this.state, 'content.logParserRules.selectedParserRuleIndex', selectedIndex);
    return this;
  }

  formatOptions(options = formatOptions) {
    _set(this.state, 'content.logParserRules.ruleFormats', options);
    return this;
  }

  metaOptions(options = metaOptions) {
    _set(this.state, 'content.logParserRules.metas', options);
    return this;
  }

  parserListState(dirty, deployed) {
    _set(this.state, 'content.logParserRules.logParsers', [{ name: 'ParserListState' }]);
    if (dirty) {
      _set(this.state, 'content.logParserRules.logParsers.dirty', true);
    } else {
      _set(this.state, 'content.logParserRules.logParsers.dirty', false);
    }
    if (deployed) {
      _set(this.state, 'content.logParserRules.logParsers.deployed', true);
    } else {
      _set(this.state, 'content.logParserRules.logParsers.deployed', false);
    }
    _set(this.state, 'content.logParserRules.selectedLogParserIndex', 0);
    return this;
  }

  parserRulesWait(flag) {
    this._setBaseState();
    if (flag) {
      _set(this.state, 'content.logParserRules.logParsersStatus', 'wait');
    } else {
      _set(this.state, 'content.logParserRules.logParsersStatus', 'completed');
    }
    return this;
  }

  parserRulesError() {
    this._setBaseState();
    _set(this.state, 'content.logParserRules.logParsersStatus', 'error');
    return this;
  }

  parserRulesDeleteWait(flag) {
    this._setBaseState();
    if (flag) {
      _set(this.state, 'content.logParserRules.deleteRuleStatus', 'wait');
    } else {
      _set(this.state, 'content.logParserRules.deleteRuleStatus', 'completed');
    }
    return this;
  }

  parserRulesSaveWait(flag) {
    this._setBaseState();
    if (flag) {
      _set(this.state, 'content.logParserRules.saveRuleStatus', 'wait');
    } else {
      _set(this.state, 'content.logParserRules.saveRuleStatus', 'completed');
    }
    return this;
  }

  parserRulesData(flag) {
    this._setBaseState();
    _set(this.state, 'content.logParserRules.parserRules', parserRules);
    _set(this.state, 'content.logParserRules.selectedParserRuleIndex', 0);
    if (flag) {
      _set(this.state, 'content.logParserRules.parserRulesStatus', 'wait');
    } else {
      _set(this.state, 'content.logParserRules.parserRulesStatus', 'completed');
    }
    _set(this.state, 'content.logParserRules.selectedFormat', 'IPV4 Address');
    _set(this.state, 'content.logParserRules.ruleFormats', [{
      name: 'Regex Pattern',
      pattern: '',
      matches: 'This matches Regex',
      type: 'regex'
    },
    {
      name: 'IPV4 Address',
      pattern: '(?:[0-9]{1,3}\\.){3}[0-9]{1,3}',
      matches: 'This matches IPV4 addresses',
      type: 'ipv4'
    }
    ]);
    return this;
  }

  hasChanges() {
    const parserRules = [{
      name: 'new example rule',
      'literals': [
        {
          'value': 'ipv4='
        }
      ],
      'pattern': {
        'captures': [
          {
            'key': 'ipv4',
            'index': '1',
            'format': 'IPV4'
          }
        ],
        'format': 'ipv4'
      },
      'ruleMetas': [],
      'outOfBox': false
    }];
    _set(this.state, 'content.logParserRules.parserRulesOriginal', parserRules);
    return this;
  }

  parserRulesFormatData(index) {
    this._setBaseState();
    if (index === 1) {
      _set(this.state, 'content.logParserRules.logParsers', [{ name: 'builtin', outOfBox: true }]);
    }
    const parserRules = [{
      'name': 'ipv4',
      'literals': [
        {
          'value': 'ipv4='
        }
      ],
      'pattern': {
        'captures': [
          {
            'key': 'ipv4',
            'index': '1',
            'format': 'IPV4'
          }
        ],
        'format': 'ipv4'
      },
      'ruleMetas': [],
      'outOfBox': false
    },
    {
      'name': 'Client Username',
      'literals': [
        {
          'value': 'ipv6='
        }
      ],
      'pattern': {
        'captures': [
          {
            'key': 'ipv6',
            'index': '1',
            'format': 'IPV6'
          }
        ],
        'regex': '\\s*([\\w_.@-]*)'
      },
      'ruleMetas': [],
      'outOfBox': true
    }];
    _set(this.state, 'content.logParserRules.parserRules', parserRules);
    _set(this.state, 'content.logParserRules.parserRulesOriginal', parserRules);
    _set(this.state, 'content.logParserRules.parserRulesStatus', 'completed');
    _set(this.state, 'content.logParserRules.parserRuleTokens', [{
      'value': 'ipv6='
    },
    {
      'value': 'ipv4='
    }]);
    _set(this.state, 'content.logParserRules.selectedParserRuleIndex', index);
    return this;
  }
  isEndpointServerOffline(status) {
    _set(this.state, 'endpoint.server.isSummaryRetrieveError', status);
    return this;
  }
}
