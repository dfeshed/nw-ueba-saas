import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll, fillIn, triggerKeyEvent, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { patchSocket } from '../../../../../helpers/patch-socket';

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
    const rules = [{ name: 'Client Domain', pattern: { format: 'ipv4', captures: [{ index: '0' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.equal(find('.value-matching .ember-power-select-selected-item').textContent.trim(), 'IPV4 Address', 'The dropdown option does not show the correct value');
    assert.equal(find('.ruleType input').value, 'ipv4', 'Show type field value is not showing or not ipv4');
    assert.equal(find('.ruleMatches input').value, 'This matches IPV4 addresses', 'Show matching field value is not showing or not correct');
  });

  test('Select Regex Pattern', async function(assert) {
    const rules = [{ name: 'Client Domain', pattern: { format: 'ipv4', regex: '\\s*([\\w_.@-]*)', captures: [{ index: '1' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    await selectChoose('.value-matching', 'Regex Pattern');
    assert.equal(find('.ruleType input').value, 'regex', 'Show type field value is not showing or not ipv4');
    assert.equal(find('.ruleMatches input').value, 'This matches Regex', 'Show matching field value is not showing or not correct');
    assert.ok(find('.ruleRegex textarea'), 'Pattern field is not showing');
    assert.equal(find('.ruleRegex textarea').value, '\\s*([\\w_.@-]*)', 'Show ruleRegex field value is not showing or not correct');
  });

  test('Editing is disabled if the rule is out of the box', async function(assert) {
    const rules = [{ name: 'Client Domain', outOfBox: true, pattern: { format: null, regex: '\\s*([\\w_.@-]*)', captures: [{ index: '1' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.equal(find('.value-matching .ember-power-select-trigger').getAttribute('aria-disabled'), 'true');
    assert.equal(find('.ruleRegex textarea').disabled, true);
  });

  test('Editing is disabled if the user does not have content-server.logparser.manage permissions', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    const rules = [{ name: 'Regex Pattern', pattern: { format: null, regex: 'test', captures: [{ index: '1' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    accessControl.set('roles', ['content-server.logparser.read']);
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.equal(find('.value-matching .ember-power-select-trigger').getAttribute('aria-disabled'), 'true');
    assert.equal(find('.ruleRegex textarea').disabled, true);
  });

  test('having no regex value shows an error in the text area', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const errorMessage = translation.t('configure.logsParser.invalidRegEx');
    const rules = [{ name: 'Regex Pattern', pattern: { format: null, regex: '   ', captures: [{ index: '1' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'The text area should have an error');
    assert.equal(find('.input-error').textContent.trim(), errorMessage, 'The error message should be displayed');
  });

  test('having regex value with more than 255 chars shows an error in the text area', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const errorMessage = translation.t('configure.logsParser.invalidRegEx');
    const rules = [{ name: 'Regex Pattern', pattern: { format: null, regex: 'iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii256', captures: [{ index: '1' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'The text area should have an error');
    assert.equal(find('.input-error').textContent.trim(), errorMessage, 'The error message should be displayed');
  });

  test('entering an invalid regex shows an error in the text area', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const errorMessage = translation.t('configure.logsParser.invalidRegEx');
    const rules = [{ name: 'Regex Pattern', pattern: { format: null, regex: '\\', captures: [{ index: '1' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'The text area should have an error');
    assert.equal(find('.input-error').textContent.trim(), errorMessage, 'The error message should be displayed');
  });

  test('if the meta capture group count is greater than the existing regex capture groups, the text area shows an error', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const errorMessage = translation.t('configure.logsParser.hasMissingCapturesError');
    const rules = [{ name: 'Regex Pattern', pattern: { format: null, regex: 'test', captures: [{ index: '1' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    await render(hbs`{{content/log-parser-rules/value-matching}}`);
    assert.equal(findAll('.rsa-form-textarea.is-error').length, 1, 'The text area should have an error');
    assert.equal(find('.input-error').textContent.trim(), errorMessage, 'The error message should be displayed');
  });

  test('keyup event trigger a highlighting call by updating the regex', async function(assert) {
    const done = assert.async();
    assert.expect(2);
    const rules = [{ name: 'Regex Pattern', pattern: { format: null, regex: 't', captures: [{ index: '0' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    await render(hbs`{{content/log-parser-rules/value-matching keyUpDelay=100 }}`);
    patchSocket((method, modelName) => {
      assert.equal(method, 'highlight');
      assert.equal(modelName, 'log-parser-rules');
      done();
    });
    await click('textarea');
    await fillIn('textarea', 'test');
    await triggerKeyEvent('textarea', 'keyup', 80);
  });

  test('Arrow keys do not trigger update of regex and highlighting call', async function(assert) {
    setState();
    assert.expect(1);
    const done = assert.async();
    const rules = [{ name: 'Regex Pattern', pattern: { format: null, regex: 't', captures: [{ index: '0' }] } }];
    new ReduxDataHelper(setState).parserRules(rules).formatOptions().build();
    await render(hbs`{{content/log-parser-rules/value-matching keyUpDelay=100 }}`);
    await click('textarea');
    await fillIn('textarea', 'test');
    patchSocket(() => {
      assert.ok(true); // this should only hit once
      done();
    });
    await triggerKeyEvent('textarea', 'keyup', 37);
    await triggerKeyEvent('textarea', 'keyup', 38);
    await triggerKeyEvent('textarea', 'keyup', 39);
    await triggerKeyEvent('textarea', 'keyup', 40);
    setTimeout(async () => {
      await triggerKeyEvent('textarea', 'keyup', 80);
    }, 200);
  });
});