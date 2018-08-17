import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, fillIn, find, findAll, render, settled } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import RSVP from 'rsvp';
import { fetchLogParsers, fetchDeviceTypes, fetchDeviceClasses } from 'configure/actions/creators/content/log-parser-rule-creators';
import Immutable from 'seamless-immutable';
import { patchSocket, throwSocket } from '../../../../../../helpers/patch-socket';
import { patchFlash } from '../../../../../../helpers/patch-flash';
import { selectChoose, clickTrigger } from 'ember-power-select/test-support/helpers';

let init, setState;

module('Integration | Component | Configure - Content - Add Log Parser', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state = { selectedParserRuleIndex: 0 }) => {
      const fullState = {
        configure: {
          content: {
            logParserRules: {
              ...state
            }
          }
        }
      };
      patchReducer(this, Immutable.from(fullState));
      // initialize all of the required data into redux app state
      const redux = this.owner.lookup('service:redux');
      init = RSVP.allSettled([
        redux.dispatch(fetchLogParsers()),
        redux.dispatch(fetchDeviceTypes()),
        redux.dispatch(fetchDeviceClasses())
      ]);
    };
  });

  const selectors = {
    componentClass: '.add-log-parser',
    instructions: '.add-log-parser-instruction',
    selectedLogParserItem: '.log-parser .ember-power-select-selected-item',
    displayNameInput: '.log-parser-display-name input',
    nameInput: '.log-parser-name input',
    deviceClassItem: '.device-class .ember-power-select-selected-item',
    cloneFromItem: '.clone-from .ember-power-select-selected-item',
    addParserButton: '.modal-footer-buttons .rsa-form-button-wrapper'
  };

  async function fillInForm(assert) {
    await fillIn(selectors.displayNameInput, 'Percival');
    await fillIn(selectors.nameInput, 'percival');
    clickTrigger('.device-class');
    selectChoose('.device-class', 'Intrusion');
    clickTrigger('.clone-from');
    selectChoose('.clone-from', 'builtin');
    assert.equal(find(selectors.displayNameInput).value, 'Percival', 'The Display Name field has a value');
    assert.equal(find(selectors.nameInput).value, 'percival', 'The name field has a value');
    assert.equal(find(selectors.deviceClassItem).textContent.trim(), 'Intrusion', 'The device class has a selected value');
    assert.equal(findAll(`${selectors.addParserButton}.is-disabled`).length, 0, 'The Add Parser Button is not disabled');
    assert.equal(find(selectors.cloneFromItem).textContent.trim(), 'builtin', 'The clone from has a selected value');
    assert.equal(findAll(`${selectors.addParserButton}.is-disabled`).length, 0, 'The Add Parser Button is enabled');
  }

  test('The component appears in the DOM', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const expectedInstruction = translation.t('configure.logsParser.addParser.instruction');
    setState();
    await init;
    await render(hbs`{{content/log-parser-rules/log-parsers/add-log-parser}}`);
    assert.equal(findAll(selectors.componentClass).length, 1, 'The component is in the DOM');
    assert.equal(find(selectors.instructions).textContent.trim(), expectedInstruction, 'The instruction text matches that found in i18n mapping');
    assert.equal(find(selectors.selectedLogParserItem).textContent.trim(), 'New', 'The baseline add-log-parser form always shows "New" as selected');
    assert.equal(find(selectors.displayNameInput).value, '', 'There is no display name');
    assert.equal(find(selectors.nameInput).value, '', 'There is no parser name');
    assert.equal(findAll(selectors.deviceClassItem).length, 0, 'There is no selected device class');
    assert.equal(findAll(selectors.cloneFromItem).length, 0, 'There is no selected clone from');
    assert.equal(findAll(`${selectors.addParserButton}.is-disabled`).length, 1, 'The Add Parser Button is disabled since there is missing information required');
  });

  test('Selecting an existing device type/parser updates the form and disables the required fields', async function(assert) {
    setState();
    await init;
    await render(hbs`{{content/log-parser-rules/log-parsers/add-log-parser}}`);
    clickTrigger('.log-parser');
    selectChoose('.log-parser', 'Accurev');
    assert.equal(find(selectors.selectedLogParserItem).textContent.trim(), 'Accurev', 'The power-select shows the newly selected parser');
    assert.equal(find(selectors.displayNameInput).value, 'Accurev', 'The Accurev display name appears in the display name field');
    assert.equal(find(selectors.displayNameInput).disabled, true, 'The Accurev display name input is disabled since this is an existing (not new) device type');
    assert.equal(find(selectors.nameInput).value, 'accurev', 'The accurev parser name appears in the name field');
    assert.equal(find(selectors.nameInput).disabled, true, 'The Accurev parser name input is disabled since this is an existing (not new) device type');
    assert.equal(find(selectors.deviceClassItem).textContent.trim(), 'Content Management System', 'The Accurev Device Class appears selected in the power-select');
    assert.equal(find('.device-class .ember-power-select-trigger').getAttribute('aria-disabled'), 'true', 'The device class dropdown is disabled since this is an existing device type');
    assert.equal(findAll(`${selectors.addParserButton}.is-disabled`).length, 0, 'The Add Parser Button is not disabled');
  });

  test('An error message is shown if the server call for adding a parser fails', async function(assert) {
    assert.expect(8);
    setState();
    await init;
    await render(hbs`{{content/log-parser-rules/log-parsers/add-log-parser}}`);
    await fillInForm(assert);
    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('configure.logsParser.addParser.addParserFailed');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
    });
    await click(`${selectors.addParserButton} button`);
  });

  test('On successfully adding a parser, the socket call is as expected, a success flash message is shown, and the onLogParserAdded action is called', async function(assert) {
    assert.expect(12);
    const done = assert.async();
    this.set('onLogParserAdded', () => {
      assert.ok(true, 'The onLogParserAdded is called on a successful add');
    });
    setState();
    await init;
    await render(hbs`{{content/log-parser-rules/log-parsers/add-log-parser onLogParserAdded=(action onLogParserAdded)}}`);
    await fillInForm(assert);
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'update');
      assert.equal(modelName, 'log-parser-rules');
      assert.deepEqual(query, {
        action: 'ADD_PARSER',
        cloneFrom: 'builtin',
        deviceClass: 'Intrusion',
        displayName: 'Percival',
        logDeviceParserName: 'percival'
      });
    });
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('configure.logsParser.addParser.addParserSuccessful');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    await click(`${selectors.addParserButton} button`);
    await settled();
  });

  test('Changing the selected log parser after filling in the form should update all the fields using the selected parser info and disable them', async function(assert) {
    setState();
    await init;
    await render(hbs`{{content/log-parser-rules/log-parsers/add-log-parser}}`);
    await fillInForm(assert);
    clickTrigger('.log-parser');
    selectChoose('.log-parser', 'Accurev');
    assert.equal(find(selectors.selectedLogParserItem).textContent.trim(), 'Accurev', 'The power-select shows the newly selected parser');
    assert.equal(find(selectors.displayNameInput).value, 'Accurev', 'The Accurev display name appears in the display name field');
    assert.equal(find(selectors.displayNameInput).disabled, true, 'The Accurev display name input is disabled since this is an existing (not new) device type');
    assert.equal(find(selectors.nameInput).value, 'accurev', 'The accurev parser name appears in the name field');
    assert.equal(find(selectors.nameInput).disabled, true, 'The Accurev parser name input is disabled since this is an existing (not new) device type');
    assert.equal(find(selectors.deviceClassItem).textContent.trim(), 'Content Management System', 'The Accurev Device Class appears selected in the power-select');
    assert.equal(find('.device-class .ember-power-select-trigger').getAttribute('aria-disabled'), 'true', 'The device class dropdown is disabled since this is an existing device type');
    assert.equal(findAll(`${selectors.addParserButton}.is-disabled`).length, 0, 'The Add Parser Button is not disabled');
  });

  test('The form shows an error and the button is disabled if the name field has a value that exists in another parser', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    const errorMessage = translation.t('configure.logsParser.addParser.nameExistsError');
    setState();
    await init;
    await render(hbs`{{content/log-parser-rules/log-parsers/add-log-parser}}`);
    await fillInForm(assert);
    await fillIn(selectors.nameInput, 'builtin');
    assert.equal(findAll('.log-parser-name .rsa-form-input.is-error').length, 1, 'The input field has an error class.');
    assert.equal(find('.log-parser-name .rsa-form-input.is-error .input-error').textContent.trim(), errorMessage, 'The input field shows an error message');
    assert.equal(findAll(`${selectors.addParserButton}.is-disabled`).length, 1, 'The Add Parser Button is disabled');
  });
});
