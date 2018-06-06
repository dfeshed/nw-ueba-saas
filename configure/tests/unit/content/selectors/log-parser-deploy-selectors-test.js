import { module, test } from 'qunit';
import {
  hasDeployableRules
 } from 'configure/reducers/content/log-parser-rules/selectors';

module('Unit | Selectors | log-parser-deploy');

const stateHasDeployableRules = {
  configure: {
    content: {
      logParserRules: {
        deployLogParserStatus: 'wait',
        parserRules: [
          {
            name: 'foo',
            pattern: {
              format: 'Foo'
            },
            outOfBox: true
          },
          {
            name: 'foo2',
            pattern: {
              format: 'Foo2'
            },
            outOfBox: false
          }
        ]
      }
    }
  }
};

const stateNoDeployableRules = {
  configure: {
    content: {
      logParserRules: {
        deployLogParserStatus: 'wait',
        parserRules: [
          {
            name: 'foo',
            pattern: {
              format: 'Foo'
            },
            outOfBox: true
          },
          {
            name: 'foo2',
            pattern: {
              format: 'Foo2'
            },
            outOfBox: true
          }
        ]
      }
    }
  }
};

test('hasDeployableRules', function(assert) {
  assert.equal(hasDeployableRules(stateHasDeployableRules), true, 'has Deployable Rules');
});

test('noDeployableRules', function(assert) {
  assert.equal(hasDeployableRules(stateNoDeployableRules), false, 'no Deployable Rules');
});
