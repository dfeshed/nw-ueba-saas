import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { clickTrigger, selectChoose } from '../../../../helpers/ember-power-select';


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
    new ReduxDataHelper(setState).parserRulesFormatData('ipv4').build();
    await render(hbs`{{parser-rules/value-matching}}`);
    assert.ok(find('.ruleValues'), 'values selector is not showing');
  });

  test('Show type field value', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData('ipv4').build();
    await render(hbs`{{parser-rules/value-matching}}`);
    assert.equal(this.$('.ruleType input').val(), 'ipv4', 'Show type field value is not showing or not ipv4');
  });

  test('Change type field value', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData('ipv6').build();
    await render(hbs`{{parser-rules/value-matching}}`);
    assert.equal(this.$('.ruleType input').val(), 'ipv6', 'Show type field value is not showing or not ipv6');
  });

  test('values selector regex', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData('ipv4').build();
    await render(hbs`{{parser-rules}}`);
    assert.ok(find('.ruleRegex .is-read-only'), 'Pattern field is not read only');
  });

  test('values selector regex', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData('ipv6').build();
    await render(hbs`{{parser-rules/value-matching}}`);
    clickTrigger('.value-matching');
    selectChoose('.value-matching', 'regex');
    assert.notOk(find('.ruleRegex .is-read-only'), 'Pattern field is read only, not editeble');
  });

  test('Show pattern field value', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData('ipv4').build();
    await render(hbs`{{parser-rules/value-matching}}`);
    assert.equal(this.$('.ruleRegex input').val(), '(?:[0-9]{1,3}\\.){3}[0-9]{1,3}', 'Show ruleRegex field value is not showing or not correct');
  });
  test('Show matching field value', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData('ipv6').build();
    await render(hbs`{{parser-rules/value-matching}}`);
    assert.equal(this.$('.ruleMatches input').val(), 'This matches IPV6 addresses', 'Show matching field value is not showing or not correct');
  });
});