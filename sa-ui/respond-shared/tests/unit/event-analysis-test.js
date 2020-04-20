import { lookupCoreDevice, createProcessAnalysisLink, createEventAnalysisLink, getEventType } from 'respond-shared/utils/event-analysis';
import { module, test } from 'qunit';

module('Unit | Utility | Event Analysis');

const services = [
  { 'id': '555d9a6fe4b0d37c827d402d', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR', 'version': '11.2.0.0', 'host': '10.40.15.182', 'port': 50002 },
  { 'id': '555d9a6fe4b0d37c827d4021', 'displayName': 'loki-broker', 'name': 'BROKER', 'version': '11.1.0.0', 'host': '10.4.61.28', 'port': 56003 }
];

const event = {
  'agent_id': 'C73CD5FF-5962-4A5F-2E9D-1CFFF4DFED2D',
  'process_vid': '9217136421101689658',
  'event_source': '10.40.15.182:50002',
  'device_type': 'nwendpoint',
  'operating_system': 'windows',
  'source': {
    'filename': 'POWERSHELL.EXE',
    'hash': '6bd1f5ab9250206ab3836529299055e272ecaa35a72cbd0230cb20ff1cc30902'
  }
};

test('Calling lookupCoreDevice() returns serviceId of core device based on valid event source', function(assert) {
  const result = lookupCoreDevice(services, '10.40.15.182:50002');

  assert.equal(result, '555d9a6fe4b0d37c827d402d', 'Expected an valid serviceId.');
});

test('Calling lookupCoreDevice() returns false when event source detail is incorrect', function(assert) {
  const result = lookupCoreDevice(services, '10.40.4.182:50002');

  assert.equal(result, false);
});

test('Calling createProcessAnalysisLink() with all necessary event metas returned process analysis link', function(assert) {

  const result = createProcessAnalysisLink(event, services);

  assert.ok(result, 'Expected valid process analysis link');
});

test('Calling createProcessAnalysisLink() when process_vid is missing returns undefined', function(assert) {

  const eventItem = {
    'agent_id': 'C73CD5FF-5962-4A5F-2E9D-1CFFF4DFED2D',
    'event_source': '10.40.15.182:50002',
    'device_type': 'nwendpoint',
    'operating_system': 'windows',
    'source': {
      'filename': 'POWERSHELL.EXE',
      'hash': '6bd1f5ab9250206ab3836529299055e272ecaa35a72cbd0230cb20ff1cc30902'
    }
  };

  const result = createProcessAnalysisLink(eventItem, services);

  assert.notOk(result, 'Expected valid process analysis link');
});

test('Calling createProcessAnalysisLink() when os type is linux returns undefined', function(assert) {

  const eventItem = {
    'agent_id': 'C73CD5FF-5962-4A5F-2E9D-1CFFF4DFED2D',
    'event_source': '10.40.15.182:50002',
    'device_type': 'nwendpoint',
    'operating_system': 'linux',
    'process_vid': '9217136421101689658',
    'source': {
      'filename': 'POWERSHELL.EXE',
      'hash': '6bd1f5ab9250206ab3836529299055e272ecaa35a72cbd0230cb20ff1cc30902'
    }
  };

  const result = createProcessAnalysisLink(eventItem, services);

  assert.notOk(result, 'Expected valid process analysis link');
});

test('Calling createEventAnalysisLink() with all necessary event metas returned process analysis link', function(assert) {

  const eventItem = {
    'event_source': '10.40.15.182:50002',
    'event_source_id': '150'
  };

  const result = createEventAnalysisLink(eventItem, services);

  assert.ok(result, 'Expected valid event analysis link');
});

test('Calling createEventAnalysisLink() when event source id missing returns undefined', function(assert) {

  const eventItem = {
    'event_source': '10.40.15.182:50002'
  };

  const result = createEventAnalysisLink(eventItem, services);

  assert.notOk(result, 'Expected valid event analysis link');
});

test('Calling getEventType() when device type is nwendpoint returns event type of endpoint', function(assert) {
  const result = getEventType('', 'nwendpoint');

  assert.equal(result, 'ENDPOINT', 'Expected event type of ENDPOINT');
});

test('Calling getEventType() when type of event is network returns event type of network', function(assert) {

  const result = getEventType('Network', '');

  assert.equal(result, 'NETWORK', 'Expected event type of NETWORK');
});

test('Calling getEventType() when event type is other than network and device type is other than nwendpoint returns event type of log', function(assert) {

  const result = getEventType('', '');

  assert.equal(result, 'LOG', 'Expected event type of LOG');
});
