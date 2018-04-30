import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click, settled } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from '../../../../helpers/engine-resolver';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';


let setState;

module('Integration | Component | rules-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('Rules will wait', async function(assert) {
    new ReduxDataHelper(setState).parserRulesData(true).build();
    await render(hbs`{{parser-rules/rules-list}}`);
    assert.ok(find('.rules-list .loading'), 'The rules spinner did not show');
  });

  test('Rules will render', async function(assert) {
    new ReduxDataHelper(setState).parserRulesData(false).build();
    await render(hbs`{{parser-rules/rules-list}}`);
    assert.equal(find('.rules-list .firstItem').textContent.trim(), 'ipv4', 'Rules did render');
  });

  test('Select a rule', async function(assert) {
    new ReduxDataHelper(setState).parserRulesData(false).build();
    await render(hbs`{{parser-rules/rules-list}}`);
    await click('.rules-list .firstItem');
    return settled().then(() => {
      assert.ok(find('.rules-list .active'), 'The rule was not selected');
    });
  });
});
