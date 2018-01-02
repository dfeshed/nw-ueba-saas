/* eslint-env node */
export default {
  'configName': 'logconfig',
  'primaryDestination': '10.10.10.10',
  'secondaryDestination': '10.10.10.12',
  'channels': [
    { 'filter': 'include', eventId: '1,2,3', channel: 'System' }
  ],
  'protocol': 'UDP',
  'testLogOnLoad': true,
  'enabled': true
};
