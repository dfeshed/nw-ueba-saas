import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, fillIn } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | add-log-parser-rule', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('Add correct rule name', async function(assert) {
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list/add-log-parser-rule}}`);
    assert.ok(find('.add-log-parser-rule input'), 'The input is not showing');
    await fillIn('.add-log-parser-rule input', 'abc');
    await assert.notOk(find('.add-log-parser-rule .input-error'), 'Error is showing');
  });

  test('Add incorrect rule name', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('configure.logsParser.modals.addRule.incorrectRuleName');
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list/add-log-parser-rule}}`);
    await fillIn('.add-log-parser-rule input', '<<<<<<');
    await assert.ok(find('.add-log-parser-rule .input-error'), 'Error is not showing');
    assert.equal(find('.add-log-parser-rule .input-error').textContent.trim(), expectedMessage, 'Incorrect error');
  });

  test('Add to long rule name', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('configure.logsParser.modals.addRule.incorrectRuleName');
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list/add-log-parser-rule}}`);
    await fillIn('.add-log-parser-rule input', 'iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii');
    await assert.ok(find('.add-log-parser-rule .input-error'), 'Error is not showing');
    assert.equal(find('.add-log-parser-rule .input-error').textContent.trim(), expectedMessage, 'Incorrect error');
  });

  test('Add a rule that exists', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('configure.logsParser.modals.addRule.matchesExistingRuleName');
    new ReduxDataHelper(setState).parserRulesFormatData(1, true).build();
    await render(hbs`{{content/log-parser-rules/parser-rules-list/add-log-parser-rule}}`);
    await fillIn('.add-log-parser-rule input', 'ipv4');
    await assert.ok(find('.add-log-parser-rule .input-error'), 'Error is not showing');
    assert.equal(find('.add-log-parser-rule .input-error').textContent.trim(), expectedMessage, 'Incorrect error');
  });
});