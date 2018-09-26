import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { click, fillIn, find, findAll, render } from '@ember/test-helpers';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import notifications from '../../../../data/subscriptions/notification-settings/findAll/data';

const initialState = {
  emailServers: [],
  selectedEmailServer: null,
  notificationsStatus: null, // wait, completed, error
  socManagers: [],
  notificationSettings: [],
  isTransactionUnderway: false
};

const selectors = {
  emailServerSettings: '.email-server-settings',
  emailServerDropdownTrigger: '.ember-power-select-trigger',
  emailServerOptions: '.ember-power-select-option',
  socManagerEmails: '.soc-manager-emails .soc-manager-email',
  deleteSocManagerEmailButton: '.soc-manager-emails li .remove-email button',
  addEmailInput: '.soc-manager-emails .soc-email-controls input',
  addEmailButton: '.soc-manager-emails .soc-email-controls button',
  notificationSettingRow: '.notification-details table tbody tr',
  formWarning: 'footer .form-warning',
  applyButton: '.form-save-controls button'
};

let setState;

module('Integration | Component | Respond Email Notifications', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      const fullState = { configure: { respond: { notifications: state } } };
      patchReducer(this, fullState);
    };
    initialize(this.owner);
  });

  test('The component appears in the DOM', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{respond/email-notifications}}`);
    assert.equal(findAll('.notifications').length, 1, 'The component appears in the DOM');
  });

  test('The email server dropdown is populated from the emailServers state and selecting an option updates the dropdown', async function(assert) {
    setState({
      ...initialState,
      emailServers: notifications.emailServers
    });
    await render(hbs`{{respond/email-notifications}}`);
    assert.equal(find(selectors.emailServerDropdownTrigger).textContent.trim(), '', 'There is no selected email server displayed');
    await clickTrigger(selectors.emailServerSettings);
    assert.equal(findAll(selectors.emailServerOptions).length, 2, 'There are two email server options in the dropdown');
    await selectChoose(selectors.emailServerSettings, 'My Email Server');
    assert.equal(find(selectors.emailServerDropdownTrigger).textContent.trim(), 'My Email Server', 'The selected dropdown option is displayed');
  });

  test('The soc manager emails appear in the component when they exist in state', async function(assert) {
    setState({
      ...initialState,
      socManagers: ['admin@rsa.com', 'soc@rsa.com']
    });
    await render(hbs`{{respond/email-notifications}}`);
    const socManagerEmails = findAll(selectors.socManagerEmails);
    assert.equal(socManagerEmails.length, 2, 'There are two soc manager emails');
    assert.equal(socManagerEmails[0].textContent, 'admin@rsa.com');
    assert.equal(socManagerEmails[1].textContent, 'soc@rsa.com');
  });

  test('The soc manager email is removed when clicking the delete button', async function(assert) {
    setState({
      ...initialState,
      socManagers: ['admin@rsa.com', 'soc@rsa.com']
    });
    await render(hbs`{{respond/email-notifications}}`);
    assert.equal(findAll(selectors.socManagerEmails).length, 2, 'There are two soc manager emails');
    await click(`${selectors.deleteSocManagerEmailButton}:nth-child(1)`);
    const socManagerEmails = findAll(selectors.socManagerEmails);
    assert.equal(socManagerEmails.length, 1, 'There is only soc manager email remaining');
    assert.equal(socManagerEmails[0].textContent, 'soc@rsa.com');
  });

  test('A soc manager email is added when the user clicks the Add button', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{respond/email-notifications}}`);
    assert.equal(findAll(selectors.socManagerEmails).length, 0, 'There are no soc manager emails');
    await fillIn(selectors.addEmailInput, 'admin@rsa.com');
    assert.equal(find(selectors.addEmailInput).value, 'admin@rsa.com');
    await click(selectors.addEmailButton);
    assert.equal(findAll(selectors.socManagerEmails).length, 1, 'There is now  one soc manager email');
  });

  test('The soc manager email add button is disabled when there is no valid email address is in the input', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{respond/email-notifications}}`);
    assert.ok(find(`${selectors.addEmailButton}:disabled`), 'The add email button is disabled');
    await fillIn(selectors.addEmailInput, 'admin@rsa.com');
    assert.notOk(find(`${selectors.addEmailButton}:disabled`), 'The add email button is not disabled when it has a valid address');
    await fillIn(selectors.addEmailInput, 'admin@rsa');
    assert.ok(find(`${selectors.addEmailButton}:disabled`), 'The add email button is disabled when address is invalid');
  });

  test('The notification settings are displayed properly', async function(assert) {
    setState({
      ...initialState,
      socManagers: ['admin@rsa.com'],
      notificationSettings: [
        {
          reason: 'incident-created',
          sendToAssignee: false,
          sendToSocManagers: false
        },
        {
          reason: 'incident-state-changed',
          sendToAssignee: true,
          sendToSocManagers: true
        }
      ]
    });
    await render(hbs`{{respond/email-notifications}}`);
    const notificationSettingRow = findAll(selectors.notificationSettingRow);
    assert.equal(notificationSettingRow.length, 2, 'There are two rows in the table');
    const [ row0, row1 ] = notificationSettingRow;
    const row0Cells = row0.getElementsByTagName('td');
    const [ row0Cell0, row0Cell1, row0Cell2 ] = row0Cells;
    const row1Cells = row1.getElementsByTagName('td');
    const [ row1Cell0, row1Cell1, row1Cell2 ] = row1Cells;
    assert.equal(row0Cell0.textContent, 'Incident Created', 'The label is resolved for incident-created');
    assert.equal(row1Cell0.textContent, 'Incident Updated', 'The label is resolved for incident-state-changed');
    assert.notOk(row0Cell2.getElementsByTagName('input')[0].disabled, 'The send to Soc Managers checkbox is enabled');
    assert.notOk(row1Cell2.getElementsByTagName('input')[0].disabled, 'The send to Soc Managers checkbox is enabled');
    assert.notOk(row0Cell1.getElementsByTagName('input')[0].checked, 'The send to assignee checkbox is NOT checked for incident-created');
    assert.ok(row1Cell1.getElementsByTagName('input')[0].checked, 'The send to assignee checkbox is checked for incident-state-changed');
    assert.notOk(row0Cell2.getElementsByTagName('input')[0].checked, 'The send to soc managers checkbox is NOT checked for incident-created');
    assert.ok(row1Cell2.getElementsByTagName('input')[0].checked, 'The send to soc managers checkbox is checked for incident-state-changed');
    await click(row0Cell1.getElementsByTagName('input')[0]);
    assert.ok(row0Cell1.getElementsByTagName('input')[0].checked, 'The send to assignee checkbox is checked for incident-created');
    await click(row0Cell2.getElementsByTagName('input')[0]);
    assert.ok(row0Cell2.getElementsByTagName('input')[0].checked, 'The send to soc managers checkbox is checked for incident-created');
    await click(row1Cell1.getElementsByTagName('input')[0]);
    assert.notOk(row1Cell1.getElementsByTagName('input')[0].checked, 'The send to assignee checkbox is NOT checked for incident-state-changed');
    await click(row1Cell2.getElementsByTagName('input')[0]);
    assert.notOk(row1Cell2.getElementsByTagName('input')[0].checked, 'The send to soc managers checkbox is NOT checked for incident-state-changed');
  });

  test('The send to soc manager checkboxes are disabled when there are no soc manager email addresses', async function(assert) {
    setState({
      ...initialState,
      socManagers: [],
      notificationSettings: [
        {
          reason: 'incident-created',
          sendToAssignee: true,
          sendToSocManagers: false
        },
        {
          reason: 'incident-state-changed',
          sendToAssignee: false,
          sendToSocManagers: true
        }
      ]
    });
    await render(hbs`{{respond/email-notifications}}`);
    const notificationSettingRow = findAll(selectors.notificationSettingRow);
    assert.equal(notificationSettingRow.length, 2, 'There are two rows in the table');
    const [ row0, row1 ] = notificationSettingRow;
    const row0Cells = row0.getElementsByTagName('td');
    const [ , , row0Cell2 ] = row0Cells;
    const row1Cells = row1.getElementsByTagName('td');
    const [ , , row1Cell2 ] = row1Cells;
    assert.ok(row0Cell2.getElementsByTagName('input')[0].disabled, 'The send to Soc Managers checkbox is disabled when there is no soc manager email address');
    assert.ok(row1Cell2.getElementsByTagName('input')[0].disabled, 'The send to Soc Managers checkbox is disabled when there is no soc manager email address');
  });

  test('Removing all soc manager emails unchecks and disables all send to soc manager checkboxes', async function(assert) {
    setState({
      ...initialState,
      socManagers: ['admin@rsa.com'],
      notificationSettings: [
        {
          reason: 'incident-created',
          sendToAssignee: true,
          sendToSocManagers: true
        },
        {
          reason: 'incident-state-changed',
          sendToAssignee: true,
          sendToSocManagers: true
        }
      ]
    });
    await render(hbs`{{respond/email-notifications}}`);
    let notificationSettingRow = findAll(selectors.notificationSettingRow);
    let [ row0, row1 ] = notificationSettingRow;
    let row0Cells = row0.getElementsByTagName('td');
    let [ , , row0Cell2 ] = row0Cells;
    let row1Cells = row1.getElementsByTagName('td');
    let [ , , row1Cell2 ] = row1Cells;
    assert.ok(row0Cell2.getElementsByTagName('input')[0].checked, 'The send to soc managers checkbox is checked for incident-created');
    assert.ok(row1Cell2.getElementsByTagName('input')[0].checked, 'The send to soc managers checkbox is checked for incident-state-changed');
    await click(find(selectors.deleteSocManagerEmailButton));
    // Grab the element of interest a second time
    notificationSettingRow = findAll(selectors.notificationSettingRow);
    [ row0, row1 ] = notificationSettingRow;
    row0Cells = row0.getElementsByTagName('td');
    [ , , row0Cell2 ] = row0Cells;
    row1Cells = row1.getElementsByTagName('td');
    [ , , row1Cell2 ] = row1Cells;
    assert.ok(row0Cell2.getElementsByTagName('input')[0].disabled, 'The send to soc managers checkbox is disabled for incident-created');
    assert.ok(row1Cell2.getElementsByTagName('input')[0].disabled, 'The send to soc managers checkbox is disabled for incident-state-changed');
    assert.notOk(row0Cell2.getElementsByTagName('input')[0].checked, 'The send to soc managers checkbox is NOT checked for incident-created');
    assert.notOk(row1Cell2.getElementsByTagName('input')[0].checked, 'The send to soc managers checkbox is NOT checked for incident-state-changed');
  });

  test('The apply button is disabled if the email server is not selected', async function(assert) {
    setState({
      ...initialState,
      emailServers: notifications.emailServers,
      selectedEmailServer: null,
      socManagers: ['admin@rsa.com'],
      notificationSettings: [
        {
          reason: 'incident-created',
          sendToAssignee: true,
          sendToSocManagers: true
        },
        {
          reason: 'incident-state-changed',
          sendToAssignee: true,
          sendToSocManagers: true
        }
      ]
    });
    await render(hbs`{{respond/email-notifications}}`);
    assert.ok(find(selectors.applyButton).disabled, 'The Apply button is disabled when an email server is not selected');
    await selectChoose(selectors.emailServerSettings, 'My Email Server');
    assert.notOk(find(selectors.applyButton).disabled, 'The Apply button is enabled after the email server is selected');
  });

  test('The apply button is disabled if there are no changes to the form and becomes enabled with a warning after a change', async function(assert) {
    const settings = {
      selectedEmailServer: 'my-favorite-server',
      socManagers: ['admin@rsa.com'],
      notificationSettings: [
        {
          reason: 'incident-created',
          sendToAssignee: true,
          sendToSocManagers: true
        },
        {
          reason: 'incident-state-changed',
          sendToAssignee: true,
          sendToSocManagers: true
        }
      ]
    };
    setState({
      ...initialState,
      emailServers: notifications.emailServers,
      ...settings,
      originalSettings: {
        ...settings
      }
    });
    await render(hbs`{{respond/email-notifications}}`);
    const notificationSettingRow = findAll(selectors.notificationSettingRow);
    const [ row0 ] = notificationSettingRow;
    const row0Cells = row0.getElementsByTagName('td');
    const [ , row0Cell1 ] = row0Cells;
    assert.ok(find(selectors.applyButton).disabled, 'The Apply button is disabled when there are no changes');
    assert.equal(find(selectors.formWarning).textContent.trim(), '', 'There is no warning displayed to the user about unsaved changes');
    await click(row0Cell1.getElementsByTagName('input')[0]);
    const i18n = this.owner.lookup('service:i18n');
    const warningMessage = i18n.t('configure.notifications.hasUnsavedChanges');
    assert.equal(find(selectors.formWarning).textContent.trim(), warningMessage, 'A warning is displayed to users that they have unsaved changes');
    assert.notOk(find(selectors.applyButton).disabled, 'The Apply button is NOT disabled after the email server is selected');
  });

  test('The apply button is disabled and a warning is displayed if the user does not have the expected permissions', async function(assert) {
    const i18n = this.owner.lookup('service:i18n');
    const warningMessage = i18n.t('configure.notifications.noManagePermissions').toString();
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('roles', []);
    await render(hbs`{{respond/email-notifications}}`);
    assert.ok(find(selectors.applyButton).disabled, 'The Apply button is disabled');
    assert.equal(find(selectors.formWarning).textContent.trim(), warningMessage, 'A warning is displayed to users that they have no permissions to edit');
  });

});