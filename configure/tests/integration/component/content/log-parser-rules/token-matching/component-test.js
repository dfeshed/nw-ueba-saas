import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll, click, fillIn } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import $ from 'jquery';
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
    firstToken: '.firstItem',
    tokenInputs: 'li.token input'
  };

  test('The tokens are displayed in an input', async function(assert) {
    const rules = [{ name: 'Client Domain', literals: [{ value: 'ipv4=' }], pattern: { format: null, regex: '' } }];
    new ReduxDataHelper(setState).parserRules(rules).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    assert.equal(find('.token-matching .firstItem input').value, 'ipv4=', 'Token matching value is not showing or not ipv4=');
  });

  test('Add a new rule token', async function(assert) {
    const rules = [{ name: 'Client Domain', literals: [{ value: 'ipv4=' }], pattern: { format: null, regex: '' } }];
    new ReduxDataHelper(setState).parserRules(rules).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    assert.equal(find(selectors.addTokenButton).disabled, true, 'The add button should be disabled when there is no value in the input');
    await fillIn(selectors.addTokenInput, '123');
    assert.equal(find(selectors.addTokenButton).disabled, false, 'The add button should be enabled when there is no value in the input');
    await fillIn(selectors.addTokenInput, 'ipv4=');
    assert.equal(find(selectors.addTokenButton).disabled, true, 'The add button should be disabled when another token exists with the same name');
    await fillIn(selectors.addTokenInput, '   ');
    assert.equal(find(selectors.addTokenButton).disabled, true, 'The add button should be disabled if it is only whitepsace');
    await fillIn(selectors.addTokenInput, '123');
    await click(selectors.addTokenButton);
    assert.equal(find('.token-matching .firstItem input').value, '123', 'Token 123 was not added');
  });

  test('Delete a rule token', async function(assert) {
    const rules = [{ name: 'Client Domain', literals: [{ value: 'ipv4=' }, { value: 'ipv6=' }], pattern: { format: null, regex: '' } }];
    new ReduxDataHelper(setState).parserRules(rules).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    await click('.firstItem button');
    assert.equal(find('.firstItem input').value, 'ipv6=', 'First token was not deleted');
  });

  test('Edit a rule token', async function(assert) {
    const rules = [{ name: 'Client Domain', literals: [{ value: 'ipv4=' }, { value: 'ipv6=' }], pattern: { format: null, regex: '' } }];
    new ReduxDataHelper(setState).parserRules(rules).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    $('.firstItem input').val('123').focusout();
    assert.equal(find(`${selectors.firstToken} input`).value, '123', 'the token was not edited as expected');
  });

  test('Edit rule token to have same value as an existing token', async function(assert) {
    const rules = [{ name: 'Client Domain', literals: [{ value: 'ipv4=' }, { value: 'ipv6=' }], pattern: { format: null, regex: '' } }];
    new ReduxDataHelper(setState).parserRules(rules).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    $('.firstItem input').val('ipv6=').focusout();
    assert.equal(find(`${selectors.firstToken} input`).value, 'ipv4=', 'the token was edited when it was not suppost to be edited');
  });

  test('Edit rule token to have only white space', async function(assert) {
    const rules = [{ name: 'Client Domain', literals: [{ value: 'ipv4=' }, { value: 'ipv6=' }], pattern: { format: null, regex: '' } }];
    new ReduxDataHelper(setState).parserRules(rules).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    $('.firstItem input').val('         ').focusout();
    assert.equal(find(`${selectors.firstToken} input`).value, 'ipv4=', 'the token was edited when it was not suppost to be edited');
  });

  test('Out of box rule is not editable', async function(assert) {
    const rules = [{ name: 'Client Domain', outOfBox: true, literals: [{ value: 'ipv4=' }, { value: 'ipv6=' }], pattern: { format: null, regex: '' } }];
    new ReduxDataHelper(setState).parserRules(rules).build();
    await render(hbs`{{content/log-parser-rules/token-matching}}`);
    assert.equal(find(selectors.addTokenButton).disabled, true, 'The add button should be disabled');
    assert.equal(find(selectors.addTokenInput).disabled, true, 'The add token input should be disabled');
    assert.equal(findAll(selectors.tokenInputs).length, 2, 'There are two tokens listed');
    findAll(selectors.tokenInputs).forEach((input) => {
      assert.equal(input.disabled, true, 'The token input should be disabled');
    });
  });
});
