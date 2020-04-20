import { module, test } from 'qunit';
import {
  hasDeployableRules
} from 'configure/reducers/content/log-parser-rules/selectors';

module('Unit | Selectors | log-parser-deploy');

const outOfBoxRule = {
  name: 'foo',
  outOfBox: true,
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
};

const notOutOfBoxRule = {
  name: 'foo2',
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
};


test('hasDeployableRules-NotAllOutOfBox-Deployed-Dirty', function(assert) {
  const state = {
    configure: {
      content: {
        logParserRules: {
          logParsers: [
            {
              name: 'ciscopix',
              dirty: true,
              deployed: true
            }
          ],
          selectedLogParserIndex: 0,
          deployLogParserStatus: 'wait',
          parserRules: [outOfBoxRule, notOutOfBoxRule],
          parserRulesOriginal: [outOfBoxRule, notOutOfBoxRule]
        }
      }
    }
  };
  assert.equal(hasDeployableRules(state), true, 'has Deployable Rules - NotAllOutOfBox-Deployed-Dirty');
});

test('hasDeployableRules-AllOutOfBox-Deployed-Dirty', function(assert) {
  const state = {
    configure: {
      content: {
        logParserRules: {
          logParsers: [
            {
              name: 'ciscopix',
              dirty: true,
              deployed: true
            }
          ],
          selectedLogParserIndex: 0,
          deployLogParserStatus: 'wait',
          parserRules: [outOfBoxRule],
          parserRulesOriginal: [outOfBoxRule]
        }
      }
    }
  };
  assert.equal(hasDeployableRules(state), true, 'has Deployable Rules - AllOutOfBox-Deployed-Dirty');
});

test('noDeployableRules-AllOutOfBox-NotDeployed-NotDirty', function(assert) {
  const state = {
    configure: {
      content: {
        logParserRules: {
          logParsers: [
            {
              name: 'ciscopix',
              dirty: false,
              deployed: false
            }
          ],
          selectedLogParserIndex: 0,
          deployLogParserStatus: 'wait',
          parserRules: [outOfBoxRule],
          parserRulesOriginal: [outOfBoxRule]
        }
      }
    }
  };
  assert.equal(hasDeployableRules(state), false, 'no Deployable Rules - AllOutOfBox-NotDeployed-NotDirty');
});

test('noDeployableRules-AllOutOfBox-Deployed-NotDirty', function(assert) {
  const state = {
    configure: {
      content: {
        logParserRules: {
          logParsers: [
            {
              name: 'ciscopix',
              dirty: false,
              deployed: true
            }
          ],
          selectedLogParserIndex: 0,
          deployLogParserStatus: 'wait',
          parserRules: [outOfBoxRule],
          parserRulesOriginal: [outOfBoxRule]
        }
      }
    }
  };
  assert.equal(hasDeployableRules(state), false, 'no Deployable Rules - AllOutOfBox-Deployed-NotDirty');
});
