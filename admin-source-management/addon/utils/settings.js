export const RADIO_BUTTONS_CONFIG = {
  name: 'recurrence',
  label: 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.title',
  type: 'radioGroup',
  items: [
    {
      name: 'DAYS',
      label: 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.options.daily'
    },
    {
      name: 'WEEKS',
      label: 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.options.weekly'
    }
  ]
};

export const SCAN_SCHEDULE_CONFIG = {
  name: 'scan-schedule',
  label: 'adminUsm.policy.scheduleConfiguration.scanType.title',
  type: 'radioGroup',
  items: [
    {
      name: 'MANUAL',
      label: 'adminUsm.policy.scheduleConfiguration.scanType.options.manual'
    },
    {
      name: 'SCHEDULED',
      label: 'adminUsm.policy.scheduleConfiguration.scanType.options.scheduled'
    }
  ]
};

const ALL_SETTINGS = [
  { id: 'scanType', label: 'Scheduled or Manual Scan' },
  { id: 'scanStartDate', label: 'Effective Date' }
];

const getById = (id) => ALL_SETTINGS.find((d) => d.id === id);

export const getLabelById = (id) => {
  const obj = getById(id);
  return obj ? obj.label : null;
};