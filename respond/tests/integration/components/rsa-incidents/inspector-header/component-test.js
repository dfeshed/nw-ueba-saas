import engineResolverFor from '../../../../helpers/engine-resolver';
import { updateEditableField } from '../../../../helpers/editable-field';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, find, findAll, render } from '@ember/test-helpers';
import { patchSocket, throwSocket } from '../../../../helpers/patch-socket';
import { patchFlash } from '../../../../helpers/patch-flash';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Incident Inspector Header', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const escalateControl = '.action-control.escalate-incident';
  const selectors = {
    incidentId: '.incident-inspector-header .id',
    incidentName: '.incident-inspector-header .name',
    escalateButton: `${escalateControl} button`,
    confirmButton: 'footer .modal-footer-buttons .is-primary button',
    escalatedIcon: `${escalateControl} .rsa-icon`,
    escalateControl
  };

  test('The rsa-incidents/inspector-header component renders to the DOM', async function(assert) {
    await render(hbs`{{rsa-incidents/inspector-header updateItem=update isEscalateAvailable=false}}`);
    assert.equal(findAll('.incident-inspector-header').length, 1, 'The incident inspector header is found in the DOM');
    assert.equal(findAll(selectors.escalateButton).length, 0, 'The escalate button does not appear in the DOM');
  });

  test('The rsa-incidents/inspector-header contains the expected data for display', async function(assert) {
    this.set('info', {
      id: 'INC-1234',
      name: 'Something Wicked This Way Comes'
    });
    await render(hbs`{{rsa-incidents/inspector-header info=info}}`);
    assert.equal(find(selectors.incidentId).textContent.trim(), 'INC-1234', 'The ID appears as expected');
    assert.equal(find(`${selectors.incidentName} .editable-field__value`).textContent.trim(), 'Something Wicked This Way Comes', 'The Name appears as expected');
  });

  test('Changing the field calls the update action', async function(assert) {
    this.set('info', {
      id: 'INC-1234',
      name: 'Something Wicked This Way Comes'
    });
    this.set('update', () => {
      assert.ok(true);
    });
    await render(hbs`{{rsa-incidents/inspector-header info=info updateItem=update}}`);
    return updateEditableField('.incident-inspector-header', 'Something Wicked This Way Went');
  });

  test('The Escalate Button appears if isEscalateAvailable is true', async function(assert) {
    await render(hbs`{{rsa-incidents/inspector-header updateItem=update isEscalateAvailable=true}}`);
    assert.equal(findAll(selectors.escalateButton).length, 1, 'The escalate button appears in the DOM');
  });

  test('The Escalate button is disabled when the incident has a status of CLOSED', async function(assert) {
    this.set('info', {
      id: 'INC-1234',
      name: 'Something Wicked This Way Comes',
      status: 'CLOSED'
    });
    await render(hbs`{{rsa-incidents/inspector-header info=info isEscalateAvailable=true}}`);
    assert.equal(find(selectors.escalateButton).disabled, true);
  });

  test('The Escalate button is disabled when the incident has a status of CLOSED_FALSE_POSITIVE', async function(assert) {
    this.set('info', {
      id: 'INC-1234',
      name: 'Something Wicked This Way Comes',
      status: 'CLOSED_FALSE_POSITIVE'
    });
    await render(hbs`{{rsa-incidents/inspector-header info=info isEscalateAvailable=true}}`);
    assert.equal(find(selectors.escalateButton).disabled, true);
  });

  test('An escalated incident shows an escalated icon/label instead of a button', async function(assert) {
    this.set('info', {
      id: 'INC-1234',
      name: 'Something Wicked This Way Comes',
      escalationStatus: 'ESCALATED'
    });
    await render(hbs`{{rsa-incidents/inspector-header info=info isEscalateAvailable=true}}`);
    assert.equal(findAll(selectors.escalateButton).length, 0);
    assert.equal(findAll(selectors.escalatedIcon).length, 1);
  });

  test('Clicking the escalate button calls the escalate endpoint with the incident ID and shows a success flash message', async function(assert) {
    assert.expect(5);
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'escalate');
      assert.equal(modelName, 'incidents');
      assert.deepEqual(query, {
        incidentId: 'INC-1234'
      });
    });
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('respond.incidents.actions.actionMessages.escalationSuccess', { incidentId: 'INC-1234' });
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
    });
    this.set('info', {
      id: 'INC-1234',
      name: 'Something Wicked This Way Comes'
    });
    await render(hbs`{{rsa-incidents/inspector-header info=info isEscalateAvailable=true}}`);
    await click(selectors.escalateButton);
    await click(selectors.confirmButton);
  });

  test('If the api call for escalation fails, an error flash message is shown', async function(assert) {
    assert.expect(2);
    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('respond.incidents.actions.actionMessages.escalationFailure', { incidentId: 'INC-1234' });
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
    });
    this.set('info', {
      id: 'INC-1234',
      name: 'Something Wicked This Way Comes'
    });
    await render(hbs`{{rsa-incidents/inspector-header info=info isEscalateAvailable=true}}`);
    await click(selectors.escalateButton);
    await click(selectors.confirmButton);
  });
});