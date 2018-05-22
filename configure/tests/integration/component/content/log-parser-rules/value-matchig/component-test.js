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

  test('values selector', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.ok(find('.ruleValues'), 'values selector is not showing');
  });

  test('Show type field value', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.equal(this.$('.ruleType input').val(), 'regex', 'Show type field value is not showing or not regex');
  });

  test('Change type field value', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.equal(this.$('.ruleType input').val(), 'ipv4', 'Show type field value is not showing or not ipv4');
  });

  test('Don\'t Select Regex Pattern', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules}}`);
    assert.notOk(find('.ruleRegex'), 'Pattern field is showing');
  });

  test('Select Regex Pattern', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    clickTrigger('.value-matching');
    selectChoose('.value-matching', 'Regex Pattern');
    assert.ok(find('.ruleRegex'), 'Pattern field is not showing');
  });
  test('Show pattern field value', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    clickTrigger('.value-matching');
    selectChoose('.value-matching', 'Regex Pattern');
    assert.equal(this.$('.ruleRegex textarea').val(), '\\s*([\\w_.@-]*)', 'Show ruleRegex field value is not showing or not correct');
  });
  test('Show matching field value', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(0, true).build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.equal(this.$('.ruleMatches input').val(), 'This matches IPV4 addresses', 'Show matching field value is not showing or not correct');
  });
});