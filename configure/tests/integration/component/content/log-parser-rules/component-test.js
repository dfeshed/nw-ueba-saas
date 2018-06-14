import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';

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
    await render(hbs`{{content/log-parser-rules}}`);
    assert.ok(find('.loading'), 'The spinner did not show');
  });

  test('Dont show matchingMapping if formats are missing', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0, false).build();
    await render(hbs`{{content/log-parser-rules}}`);
    assert.notOk(find('.matchingMapping'), 'matchingMapping area is showing');
  });

  test('Show matchingMapping if formats are there', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0, true).metaOptions().build();
    await render(hbs`{{content/log-parser-rules}}`);
    assert.ok(find('.matchingMapping'), 'matchingMapping area is not showing');
  });

});

