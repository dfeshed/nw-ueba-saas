import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { clickTrigger, selectChoose } from '../../../../../helpers/ember-power-select';

let setState;

module('Integration | Component | value matching', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('The rule type and matches fields appear as expected', async function(assert) {
    const rules = [{ name: 'Client Domain', pattern: { format: 'ipv4' } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.equal(find('.value-matching .ember-power-select-selected-item').textContent.trim(), 'IPV4 Address', 'The dropdown option does not show the correct value');
    assert.equal(find('.ruleType input').value, 'ipv4', 'Show type field value is not showing or not ipv4');
    assert.equal(find('.ruleMatches input').value, 'This matches IPV4 addresses', 'Show matching field value is not showing or not correct');
  });

  test('Select Regex Pattern', async function(assert) {
    const rules = [{ name: 'Client Domain', pattern: { format: 'ipv4', regex: '\\s*([\\w_.@-]*)' } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    clickTrigger('.value-matching');
    selectChoose('.value-matching', 'Regex Pattern');
    assert.equal(find('.ruleType input').value, 'regex', 'Show type field value is not showing or not ipv4');
    assert.equal(find('.ruleMatches input').value, 'This matches Regex', 'Show matching field value is not showing or not correct');
    assert.ok(find('.ruleRegex textarea'), 'Pattern field is not showing');
    assert.equal(find('.ruleRegex textarea').value, '\\s*([\\w_.@-]*)', 'Show ruleRegex field value is not showing or not correct');
  });

});