import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | save-reset-rule', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('Save button shows', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/save-reset-rule}}`);
    assert.ok(find('.save-reset-rule .saveRule button'), 'Save button is not showing');
  });

  test('Reset button shows', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/save-reset-rule}}`);
    assert.ok(find('.save-reset-rule .resetRule button'), 'Reset button is not showing');
  });

});