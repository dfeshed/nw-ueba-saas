import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { inMixedMode, extractSource, extractVersion } from 'respond/utils/storypoint/mixed-mode';
import { t } from '../../../integration/components/rsa-incident/container/helper';

module('Unit | Utility | storypoint/mixed-mode', function(hooks) {
  setupTest(hooks);

  test('returns warning when core service in mixed mode', function(assert) {
    const minVersion = '11.2';
    const services = {
      '555d9a6fe4b0d37c827d402d': {
        displayName: 'loki-concentrator',
        host: '10.4.61.33',
        id: '555d9a6fe4b0d37c827d402d',
        name: 'CONCENTRATOR',
        port: 56005,
        version: '11.1.0.0'
      }
    };
    const events = [
      {
        type: 'Network',
        from: '161.253.40.218:2045',
        timestamp: 1526414954000,
        event_source: '10.4.61.33:56005',
        event_source_id: '150',
        indicatorId: '5afb3edc2de080511717841a',
        id: '5afb3edc2de080511717841a:0'
      }
    ];

    const result = inMixedMode(services, events, minVersion);
    const title = t(this, 'investigate.services.coreServiceNotUpdated', { version: '11.1.0.0', minVersion: '11.2' });
    assert.equal(result, title);
  });

  test('returns false when core service NOT in mixed mode', function(assert) {
    const minVersion = '11.2';
    const services = {
      '555d9a6fe4b0d37c827d402d': {
        displayName: 'loki-concentrator',
        host: '10.4.61.33',
        id: '555d9a6fe4b0d37c827d402d',
        name: 'CONCENTRATOR',
        port: 56005,
        version: '11.2.0.0'
      }
    };
    const events = [
      {
        type: 'Network',
        from: '161.253.40.218:2045',
        timestamp: 1526414954000,
        event_source: '10.4.61.33:56005',
        event_source_id: '150',
        indicatorId: '5afb3edc2de080511717841a',
        id: '5afb3edc2de080511717841a:0'
      }
    ];

    const result = inMixedMode(services, events, minVersion);
    assert.equal(result, false);
  });

  test('returns false and will not blow up when events lack proper shape', function(assert) {
    const minVersion = '11.2';
    const services = {
      '555d9a6fe4b0d37c827d402d': {
        displayName: 'loki-concentrator',
        host: '10.4.61.33',
        id: '555d9a6fe4b0d37c827d402d',
        name: 'CONCENTRATOR',
        port: 56005,
        version: '11.2.0.0'
      }
    };

    const events = [
      {
        type: 'Network',
        timestamp: 1526414954000,
        id: '5afb3edc2de080511717841a:0'
      },
      {
        foo: 'bar',
        event_source: null
      }
    ];

    const result = inMixedMode(services, events, minVersion);
    assert.equal(result, false);
  });

  test('returns false and will not blow up when services lack proper shape', function(assert) {
    const minVersion = '11.2';
    const services = {
      '555d9a6fe4b0d37c827d402d': {
        displayName: 'loki-concentrator',
        host: '10.4.61.33',
        id: '555d9a6fe4b0d37c827d402d',
        name: 'CONCENTRATOR',
        port: null,
        version: undefined
      },
      '555d9a6fe4b0d37c827d402e': {
        displayName: 'loki-broker',
        id: '555d9a6fe4b0d37c827d402e',
        name: 'BROKER'
      }
    };

    const events = [
      {
        type: 'Network',
        from: '161.253.40.218:2045',
        timestamp: 1526414954000,
        event_source: '10.4.61.33:56005',
        event_source_id: '150',
        indicatorId: '5afb3edc2de080511717841a',
        id: '5afb3edc2de080511717841a:0'
      }
    ];

    const result = inMixedMode(services, events, minVersion);
    assert.equal(result, false);
  });

  test('extractSource will not return null or undefined', function(assert) {
    const events = [
      {
        type: 'Network',
        event_source: null
      },
      {
        type: 'Network',
        event_source: '10.4.61.33:56005'
      },
      {
        type: 'Network'
      }
    ];
    const result = extractSource(events);
    assert.deepEqual(result, ['10.4.61.33:56005']);
  });

  test('extractVersion will not return null or undefined', function(assert) {
    const services = [
      {
        displayName: 'loki-concentrator',
        host: '10.4.61.33',
        id: '555d9a6fe4b0d37c827d402a',
        name: 'CONCENTRATOR',
        port: 56005
      },
      {
        displayName: 'loki-concentrator',
        host: '10.4.61.33',
        id: '555d9a6fe4b0d37c827d402b',
        name: 'CONCENTRATOR',
        port: 56005,
        version: null
      },
      {
        displayName: 'loki-concentrator',
        host: '10.4.61.33',
        id: '555d9a6fe4b0d37c827d402c',
        name: 'CONCENTRATOR',
        port: 56005,
        version: '11.1.0.0'
      }
    ];
    const result = extractVersion(services);
    assert.equal(result, '11.1.0.0');
  });

  test('extractVersion will return the first version found', function(assert) {
    const services = [
      {
        displayName: 'loki-concentrator',
        host: '10.4.61.33',
        id: '555d9a6fe4b0d37c827d402a',
        name: 'CONCENTRATOR',
        port: 56005,
        version: '11.3.0.0'
      },
      {
        displayName: 'loki-concentrator',
        host: '10.4.61.33',
        id: '555d9a6fe4b0d37c827d402b',
        name: 'CONCENTRATOR',
        port: 56005,
        version: '11.2.0.0'
      },
      {
        displayName: 'loki-concentrator',
        host: '10.4.61.33',
        id: '555d9a6fe4b0d37c827d402c',
        name: 'CONCENTRATOR',
        port: 56005,
        version: '11.1.0.0'
      }
    ];
    const result = extractVersion(services);
    assert.equal(result, '11.3.0.0');
  });

});
