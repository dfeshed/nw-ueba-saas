import { findAll } from '@ember/test-helpers';

export const alterSettings = function() {
  const notificationSettingRow = findAll(selectors.notificationSettingRow);
  const [ row0 ] = notificationSettingRow;
  const row0Cells = row0.getElementsByTagName('td');
  const [ , row0Cell1 ] = row0Cells;
  return row0Cell1;
};

export const settings = {
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

const prefix = 'configure.notifications.';
export const t = (context, key) => {
  const i18n = context.owner.lookup('service:i18n');
  return i18n.t(`${prefix}${key}`);
};

export const labels = (context, name) => {
  switch (name) {
    case 'updateFailure': {
      return t(context, `actionMessages.${name}`);
    }
    case 'updateEmailServerFailure': {
      return t(context, `actionMessages.${name}`);
    }
    case 'updateSuccess': {
      return t(context, `actionMessages.${name}`);
    }
    default: {
      return t(context, `${name}`);
    }
  }
};

export const selectors = {
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
