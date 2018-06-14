import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { clickTrigger, selectChoose } from '../../../../../helpers/ember-power-select';

let setState;

module('Integration | Component | value mapping', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('The meta dropdown shows the correct value', async function(assert) {
    const rules = [{ name: 'Client Domain', pattern: { captures: [{ 'key': 'domain.dst', 'index': '0', 'format': 'Text' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).metaOptions().build();
    await render(hbs`{{content/log-parser-rules/value-mapping}}`);
    assert.equal(findAll('li .ember-power-select-selected-item').length, 1, 'There is only one capture/meta selection dropdown');
    assert.equal(find('li .ember-power-select-selected-item').textContent.trim(), 'Destination Domain', 'The appropriate option is selected in dropdown');
  });

  test('Changing the dropdown selection updates the value in the selected rule state', async function(assert) {
    const rules = [{ name: 'Client Domain', pattern: { captures: [{ 'key': 'domain.dst', 'index': '0', 'format': 'Text' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).metaOptions().build();
    await render(hbs`{{content/log-parser-rules/value-mapping}}`);
    clickTrigger('.value-mapping .firstItem');
    selectChoose('.value-mapping .firstItem', 'Source Domain');
    assert.equal(find('li .ember-power-select-selected-item').textContent.trim(), 'Source Domain', 'The appropriate option is selected in dropdown');
  });

  test('Editing is disabled if the rule is out of the box', async function(assert) {
    assert.expect(1);
    const rules = [{ name: 'Client Domain', outOfBox: true, pattern: { captures: [{ 'key': 'domain.dst', 'index': '0', 'format': 'Text' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).metaOptions().build();
    await render(hbs`{{content/log-parser-rules/value-mapping}}`);
    findAll('li .ember-power-select-trigger').forEach((dropdown) => {
      assert.equal(dropdown.getAttribute('aria-disabled'), 'true');
    });
  });
});