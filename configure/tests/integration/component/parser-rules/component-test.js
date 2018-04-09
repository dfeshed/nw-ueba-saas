import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import engineResolverFor from '../../../helpers/engine-resolver';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';

let setState;


module('Integration | Component | parser rules', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('it shows the loading spinner when isLoading is true', async function(assert) {
    new ReduxDataHelper(setState).parserRulesWait(true).build();
    await render(hbs`{{parser-rules}}`);
    assert.ok(find('.loading'), 'The spinner did not show');
  });
});

