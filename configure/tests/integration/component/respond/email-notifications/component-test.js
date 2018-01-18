import { moduleForComponent, test } from 'ember-qunit';
import { getOwner } from '@ember/application';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';
import notifications from '../../../../data/subscriptions/notification-settings/findAll/data';
import { clickTrigger, selectChoose } from '../../../../helpers/ember-power-select';
import $ from 'jquery';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const initialState = {
  emailServers: [],
  selectedEmailServer: null,
  notificationsStatus: null, // wait, completed, error
  socManagers: [],
  notificationSettings: [],
  isTransactionUnderway: false
};

let setState;
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

moduleForComponent('respond/email-notifications', 'Integration | Component | Respond Email Notifications', {
  integration: true,
  resolver: engineResolverFor('configure'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      const fullState = { configure: { respond: { notifications: state } } };
      applyPatch(Immutable.from(fullState));
      this.inject.service('redux');
    };
    initialize(this);
  },
  afterEach() {
    revertPatch();
  }
});

test('The component appears in the DOM', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{respond/email-notifications}}`);
  assert.equal(this.$('.notifications').length, 1, 'The component appears in the DOM');
});

test('The email server dropdown is populated from the emailServers state and selecting an option updates the dropdown', function(assert) {
  setState({
    ...initialState,
    emailServers: notifications.emailServers
  });
  this.render(hbs`{{respond/email-notifications}}`);
  assert.equal(this.$(selectors.emailServerDropdownTrigger).text().trim(), '', 'There is no selected email server displayed');
  clickTrigger(selectors.emailServerSettings);
  assert.equal($(selectors.emailServerOptions).length, 2, 'There are two email server options in the dropdown');
  selectChoose(selectors.emailServerSettings, 'My Email Server');
  return wait().then(() => {
    assert.equal(this.$(selectors.emailServerDropdownTrigger).text().trim(), 'My Email Server', 'The selected dropdown option is displayed');
  });
});

test('The soc manager emails appear in the component when they exist in state', function(assert) {
  setState({
    ...initialState,
    socManagers: ['admin@rsa.com', 'soc@rsa.com']
  });
  this.render(hbs`{{respond/email-notifications}}`);
  assert.equal(this.$(selectors.socManagerEmails).length, 2, 'There are two soc manager emails');
  assert.equal(this.$(`${selectors.socManagerEmails}:eq(0)`).text(), 'admin@rsa.com');
  assert.equal(this.$(`${selectors.socManagerEmails}:eq(1)`).text(), 'soc@rsa.com');
});

test('The soc manager email is removed when clicking the delete button', function(assert) {
  setState({
    ...initialState,
    socManagers: ['admin@rsa.com', 'soc@rsa.com']
  });
  this.render(hbs`{{respond/email-notifications}}`);
  assert.equal(this.$(selectors.socManagerEmails).length, 2, 'There are two soc manager emails');
  this.$(`${selectors.deleteSocManagerEmailButton}:eq(0)`).click();
  return wait().then(() => {
    assert.equal(this.$(selectors.socManagerEmails).length, 1, 'There is only soc manager email remaining');
    assert.equal(this.$(`${selectors.socManagerEmails}:eq(0)`).text(), 'soc@rsa.com');
  });
});

test('A soc manager email is added when the user clicks the Add button', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{respond/email-notifications}}`);
  assert.equal(this.$(selectors.socManagerEmails).length, 0, 'There are no soc manager emails');
  this.$(selectors.addEmailInput).val('admin@rsa.com').change();
  assert.equal(this.$(selectors.addEmailInput).val(), 'admin@rsa.com');
  return wait().then(() => {
    this.$(selectors.addEmailButton).click();
    return wait().then(() => {
      assert.equal(this.$(selectors.socManagerEmails).length, 1, 'There is now  one soc manager email');
    });
  });
});

test('The soc manager email add button is disabled when there is no valid email address is in the input', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{respond/email-notifications}}`);
  assert.equal(this.$(selectors.addEmailButton).is(':disabled'), true, 'The add email button is disabled');
  this.$(selectors.addEmailInput).val('admin@rsa.com').change();
  assert.equal(this.$(selectors.addEmailButton).is(':disabled'), false, 'The add email button is not disabled when it has a valid address');
  this.$(selectors.addEmailInput).val('admin@rsa').change();
  assert.equal(this.$(selectors.addEmailButton).is(':disabled'), true, 'The add email button is disabled when address is invalid');
});

test('The notification settings are displayed properly', function(assert) {
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
  this.render(hbs`{{respond/email-notifications}}`);
  assert.equal(this.$(selectors.notificationSettingRow).length, 2, 'There are two rows in the table');
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(0)`).text(), 'Incident Created', 'The label is resolved for incident-created');
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(0)`).text(), 'Incident Updated', 'The label is resolved for incident-state-changed');
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(2) input`).is(':disabled'), false, 'The send to Soc Managers checkbox is enabled');
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(2) input`).is(':disabled'), false, 'The send to Soc Managers checkbox is enabled');
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(1) label.checked`).length, 0, 'The send to assignee checkbox is not checked for incident-created');
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(1) label.checked`).length, 1, 'The send to assignee checkbox is checked for incident-state-changed');
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(2) label.checked`).length, 0, 'The send to soc managers checkbox is not checked for incident-created');
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(2) label.checked`).length, 1, 'The send to soc managers checkbox is checked for incident-state-changed');
  this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(1) input`).click();
  this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(2) input`).click();
  this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(1) input`).click();
  this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(2) input`).click();
  return wait().then(() => {
    assert.equal(this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(1) label.checked`).length, 1, 'The send to assignee checkbox is checked for incident-created');
    assert.equal(this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(1) label.checked`).length, 0, 'The send to assignee checkbox is not checked for incident-state-changed');
    assert.equal(this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(2) label.checked`).length, 1, 'The send to soc managers checkbox is checked for incident-created');
    assert.equal(this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(2) label.checked`).length, 0, 'The send to soc managers checkbox is not checked for incident-state-changed');
  });
});

test('The send to soc manager checkboxes are disabled when there are no soc manager email addresses', function(assert) {
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
  this.render(hbs`{{respond/email-notifications}}`);
  assert.equal(this.$(selectors.notificationSettingRow).length, 2, 'There are two rows in the table');
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(2) input`).is(':disabled'), true, 'The send to Soc Managers checkbox is disabled when there is no soc manager email address');
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(2) input`).is(':disabled'), true, 'The send to Soc Managers checkbox is disabled when there is no soc manager email address');
});

test('Removing all soc manager emails unchecks and disables all send to soc manager checkboxes', function(assert) {
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
  this.render(hbs`{{respond/email-notifications}}`);
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(2) label.checked`).length, 1, 'The send to soc managers checkbox is checked for incident-created');
  assert.equal(this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(2) label.checked`).length, 1, 'The send to soc managers checkbox is checked for incident-state-changed');
  this.$(`${selectors.deleteSocManagerEmailButton}:eq(0)`).click();
  return wait().then(() => {
    assert.equal(this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(2) label.checked`).length, 0, 'The send to soc managers checkbox is not checked for incident-created');
    assert.equal(this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(2) label.checked`).length, 0, 'The send to soc managers checkbox is not checked for incident-state-changed');
    assert.equal(this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(2) input`).is(':disabled'), true, 'The send to Soc Managers checkbox is enabled for incident-created');
    assert.equal(this.$(`${selectors.notificationSettingRow}:eq(1) td:eq(2) input`).is(':disabled'), true, 'The send to Soc Managers checkbox is enabled for incident-state-changed');
  });
});

test('The apply button is disabled if the email server is not selected', function(assert) {
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
  this.render(hbs`{{respond/email-notifications}}`);
  assert.equal(this.$(selectors.applyButton).is(':disabled'), true, 'The Apply button is disabled when an email server is not selected');
  clickTrigger(selectors.emailServerSettings);
  selectChoose(selectors.emailServerSettings, 'My Email Server');
  return wait().then(() => {
    assert.equal(this.$(selectors.applyButton).is(':disabled'), false, 'The Apply button is enabled after the email server is selected');
  });
});

test('The apply button is disabled if there are changes to the form', function(assert) {
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
  this.render(hbs`{{respond/email-notifications}}`);
  assert.equal(this.$(selectors.applyButton).is(':disabled'), true, 'The Apply button is disabled when there are no changes');
  assert.equal(this.$(selectors.formWarning).text().trim(), '', 'There is no warning displayed to the user about unsaved changes');
  this.$(`${selectors.notificationSettingRow}:eq(0) td:eq(1) input`).click();
  return wait().then(() => {
    const translation = getOwner(this).lookup('service:i18n');
    const warningMessage = translation.t('configure.notifications.hasUnsavedChanges');
    assert.equal(this.$(selectors.formWarning).text().trim(), warningMessage, 'A warning is displayed to users that they have unsaved changes');
    assert.equal(this.$(selectors.applyButton).is(':disabled'), false, 'The Apply button is enabled after the email server is selected');
  });
});