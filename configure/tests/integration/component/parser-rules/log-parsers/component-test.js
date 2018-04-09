import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import engineResolverFor from '../../../../helpers/engine-resolver';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | log parsers', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('Log Parsers will render', async function(assert) {
    new ReduxDataHelper(setState).parserRulesWait(false).build();
    await render(hbs`{{parser-rules/log-parsers}}`);
    assert.equal(find('.parserTable tr td').textContent.trim(), 'builtin', 'Log Parsers did render');
  });
});
