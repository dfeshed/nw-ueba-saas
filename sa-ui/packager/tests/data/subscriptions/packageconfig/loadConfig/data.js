export default {
  'configName': 'logconfig',
  'primaryDestination': '10.10.10.10',
  'secondaryDestination': '10.10.10.12',
  'channels': [
    { 'filter': 'include', 'eventId': '1,2,3', 'channel': 'System' },
    { 'filter': 'exclude', 'eventId': 1, 'channel': 'Application' },
    { 'filter': 'include', 'eventId': '', 'channel': 'Security' }
  ],
  'protocol': 'UDP',
  'testLogOnLoad': true,
  'enabled': true,
  'hasErrors': true,
  'errorMessage': 'EVENT_ID_INVALID'
};
