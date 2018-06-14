import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, click, fillIn, triggerEvent } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | token matching', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const selectors = {
    addTokenInput: '.add-token input',
    addTokenButton: '.add-token button',
    firstToken: '.firstItem'
  };

  test('The tokens are displayed in an input', async function(assert) {
    const rules = [{ name: 'Client Domain', literals: [{ value: 'ipv4=' }] }];
    new ReduxDataHelper(setState).parserRules(rules).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    assert.equal(find('.token-matching .firstItem input').value, 'ipv4=', 'Token matching value is not showing or not ipv4=');
  });

  test('Add a new rule token', async function(assert) {
    const rules = [{ name: 'Client Domain', literals: [{ value: 'ipv4=' }] }];
    new ReduxDataHelper(setState).parserRules(rules).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    assert.equal(find(selectors.addTokenButton).disabled, true, 'The add button should be disabled when there is no value in the input');
    await fillIn(selectors.addTokenInput, '123');
    assert.equal(find(selectors.addTokenButton).disabled, false, 'The add button should be enabled when there is no value in the input');
    await fillIn(selectors.addTokenInput, 'ipv4=');
    assert.equal(find(selectors.addTokenButton).disabled, true, 'The add button should be disabled when another token exists with the same name');
    await fillIn(selectors.addTokenInput, '123');
    await click(selectors.addTokenButton);
    assert.equal(find('.token-matching .firstItem input').value, '123', 'Token 123 was not added');
  });

  test('Delete a rule token', async function(assert) {
    const rules = [{ name: 'Client Domain', literals: [{ value: 'ipv4=' }, { value: 'ipv6=' }] }];
    new ReduxDataHelper(setState).parserRules(rules).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    await click('.firstItem button');
    assert.equal(find('.firstItem input').value, 'ipv6=', 'First token was not deleted');
  });

  test('Edit a rule token', async function(assert) {
    const rules = [{ name: 'Client Domain', literals: [{ value: 'ipv4=' }, { value: 'ipv6=' }] }];
    new ReduxDataHelper(setState).parserRules(rules).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    await fillIn(`${selectors.firstToken} input`, '123');
    await triggerEvent('.firstItem input', 'focusOut');
    assert.equal(find(`${selectors.firstToken} input`).value, '123', 'the token was not edited as expected');
  });
});