export const COLUMNS_CONFIG = [
  {
    field: 'device',
    title: 'Device',
    disableSort: true
  },
  {
    field: 'status',
    title: 'Status'
  },
  {
    field: 'session_rate',
    title: 'Session Rate',
    class: 'rsa-form-row-checkbox',
    dataType: 'numeric',
    componentClass: 'rsa-form-checkbox',
    headerComponentClass: 'rsa-form-checkbox'
  },
  {
    field: 'meta_rate',
    title: 'Meta Rate',
    class: 'rsa-form-row-checkbox',
    dataType: 'numeric',
    componentClass: 'rsa-form-checkbox',
    headerComponentClass: 'rsa-form-checkbox'
  },
  {
    field: 'sessions_behind',
    title: 'Sessions Behind',
    class: 'rsa-form-row-checkbox',
    dataType: 'numeric',
    componentClass: 'rsa-form-checkbox',
    headerComponentClass: 'rsa-form-checkbox'
  },
  {
    field: 'time_last',
    title: 'Last Time',
    dataType: 'date'
  },
  {
    field: 'time_network',
    title: 'Latency',
    dataType: 'numeric'
  }
];
