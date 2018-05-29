import Immutable from 'seamless-immutable';

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

export default class DataHelper {
  constructor(setState) {
    this.state = {};
    this.setState = setState;
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

  parserRulesWait(flag) {
    _set(this.state, 'content.logParserRules.logParsers', [{ name: 'builtin' }]);
    if (flag) {
      _set(this.state, 'content.logParserRules.logParsersStatus', 'wait');
    } else {
      _set(this.state, 'content.logParserRules.logParsersStatus', 'completed');
    }
    return this;
  }

  parserRulesDeleteWait(flag) {
    _set(this.state, 'content.logParserRules.logParsers', [{ name: 'builtin' }]);
    if (flag) {
      _set(this.state, 'content.logParserRules.deleteRuleStatus', 'wait');
    } else {
      _set(this.state, 'content.logParserRules.deleteRuleStatus', 'completed');
    }
    return this;
  }

  parserRulesData(flag) {
    _set(this.state, 'content.logParserRules.parserRules', [{ name: 'ipv4' }]);
    if (flag) {
      _set(this.state, 'content.logParserRules.parserRulesStatus', 'wait');
    } else {
      _set(this.state, 'content.logParserRules.parserRulesStatus', 'completed');
    }
    return this;
  }

  parserRulesFormatData(index, formats) {
    _set(this.state, 'content.logParserRules.parserRules', [{
      'name': 'ipv4',
      'literals': [
        {
          'value': 'ipv4= '
        }
      ],
      'pattern': {
        'captures': [
          {
            'key': 'ipv4',
            'index': '1'
          }
        ],
        'format': 'ipv4'
      },
      'ruleMetas': []
    },
    {
      'name': 'Client Username',
      'literals': [
        {
          'value': 'ipv6= '
        }
      ],
      'pattern': {
        'captures': [
          {
            'key': 'ipv6',
            'index': '1'
          }
        ],
        'regex': '\\s*([\\w_.@-]*)'
      },
      'ruleMetas': []
    }]);
    _set(this.state, 'content.logParserRules.selectedParserRuleIndex', 0);
    _set(this.state, 'content.logParserRules.parserRulesStatus', 'completed');
    if (formats) {
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
      },
      {
        name: 'IPV6 Address',
        pattern: '((([0-9A-Fa-f]{1,4}:){1,6}:)|(([0-9A-Fa-f]{1,4}:){7}))([0-9A-Fa-f]{1,4})|::1|::0',
        matches: 'This matches IPV6 addresses',
        type: 'ipv6'
      }]);
    }
    _set(this.state, 'content.logParserRules.selectedParserRuleIndex', index);
    return this;
  }
}