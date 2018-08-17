import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
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

  const sentToArcherControl = '.action-control.send-to-archer';
  const selectors = {
    incidentId: '.incident-inspector-header .id',
    incidentName: '.incident-inspector-header .name',
    sendToArcherButton: `${sentToArcherControl} button`,
    confirmButton: 'footer .modal-footer-buttons .is-primary button',
    sentToArcherIcon: `${sentToArcherControl} .rsa-icon`,
    sentToArcherControl
  };

  test('The rsa-incidents/inspector-header component renders to the DOM', async function(assert) {
    await render(hbs`{{rsa-incidents/inspector-header updateItem=update isSendToArcherAvailable=false}}`);
    assert.equal(findAll('.incident-inspector-header').length, 1, 'The incident inspector header is found in the DOM');
    assert.equal(findAll(selectors.sendToArcherButton).length, 0, 'The escalate button does not appear in the DOM');
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

  test('The Send To Archer Button appears if isSendToArcherAvailable is true', async function(assert) {
    await render(hbs`{{rsa-incidents/inspector-header updateItem=update isSendToArcherAvailable=true}}`);
    assert.equal(findAll(selectors.sendToArcherButton).length, 1, 'The escalate button appears in the DOM');
  });

  test('The Send To Archer button is disabled when the incident has a status of CLOSED', async function(assert) {
    this.set('info', {
      id: 'INC-1234',
      name: 'Something Wicked This Way Comes',
      status: 'CLOSED'
    });
    await render(hbs`{{rsa-incidents/inspector-header info=info isSendToArcherAvailable=true}}`);
    assert.equal(find(selectors.sendToArcherButton).disabled, true);
  });

  test('The Send To Archer button is disabled when the incident has a status of CLOSED_FALSE_POSITIVE', async function(assert) {
    this.set('info', {
      id: 'INC-1234',
      name: 'Something Wicked This Way Comes',
      status: 'CLOSED_FALSE_POSITIVE'
    });
    await render(hbs`{{rsa-incidents/inspector-header info=info isSendToArcherAvailable=true}}`);
    assert.equal(find(selectors.sendToArcherButton).disabled, true);
  });

  test('An incident sent to archer shows an icon/label instead of a button', async function(assert) {
    this.set('info', {
      id: 'INC-1234',
      name: 'Something Wicked This Way Comes',
      sentToArcher: true
    });
    await render(hbs`{{rsa-incidents/inspector-header info=info isSendToArcherAvailable=true}}`);
    assert.equal(findAll(selectors.sendToArcherButton).length, 0);
    assert.equal(findAll(selectors.sentToArcherIcon).length, 1);
  });

  test('Clicking the Send To Archer button calls the send-to-archer endpoint with the incident ID and shows a success flash message', async function(assert) {
    assert.expect(5);
    const done = assert.async();

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'sendToArcher');
      assert.equal(modelName, 'incidents');
      assert.deepEqual(query, {
        incidentId: 'INC-1234'
      });
    });

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      // Note: the archerIncidentId is mocked in the mock server data
      const expectedMessage = translation.t('respond.incidents.actions.actionMessages.sendToArcherSuccess', { incidentId: 'INC-1234', archerIncidentId: '12321349' });
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    this.set('info', {
      id: 'INC-1234',
      name: 'Something Wicked This Way Comes'
    });
    await render(hbs`{{rsa-incidents/inspector-header info=info isSendToArcherAvailable=true}}`);
    await click(selectors.sendToArcherButton);
    await click(selectors.confirmButton);
  });

  const _testEscalationFailureMessages = function(errorCode, i18nKeyLeaf) {
    return async function(assert) {
      assert.expect(2);
      const done = assert.async();
      throwSocket({ message: { code: errorCode } });
      patchFlash((flash) => {
        const translation = this.owner.lookup('service:i18n');
        const expectedMessage = translation.t(`respond.incidents.actions.actionMessages.${i18nKeyLeaf}`, { incidentId: 'INC-1234' });
        assert.equal(flash.type, 'error');
        assert.equal(flash.message.string, expectedMessage);
        done();
      });
      this.set('info', {
        id: 'INC-1234',
        name: 'Something Wicked This Way Comes'
      });
      await render(hbs`{{rsa-incidents/inspector-header info=info isSendToArcherAvailable=true}}`);
      await click(selectors.sendToArcherButton);
      await click(selectors.confirmButton);
    };
  };

  test('If the api call for escalation fails with code 1, the proper error flash message is shown', _testEscalationFailureMessages(1, 'sendToArcherFailed'));
  test('If the api call for escalation fails with code 31, the proper error flash message is shown', _testEscalationFailureMessages(31, 'sendToArcherConnectionFailed'));
  test('If the api call for escalation fails with code 32, the proper error flash message is shown', _testEscalationFailureMessages(32, 'sendToArcherMetadataLoadFailed'));
  test('If the api call for escalation fails with code 33, the proper error flash message is shown', _testEscalationFailureMessages(33, 'sendToArcherValidationFailed'));
});