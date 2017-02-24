import Ember from 'ember';
import Indicator from 'respond/utils/indicator/indicator';
import { module, test } from 'qunit';

const { get } = Ember;

module('Unit | Utility | indicator/indicator');

const incidentJson = {
  id: 'id1',
  timestamp: Number(new Date()),
  originalHeaders: {
    name: 'name1'
  },
  alert: {
    relationships: [
      [
        'user1',
        'host1',
        'domain1',
        '',
        'destinationIp',
        'file1'
      ],
      [
        'user1',
        '',
        '',
        'sourceIp'
      ]
    ],
    events: [
      {
        time: 1000,
        enrichment: {
          enrichmentKey1: 100
        }
      }
    ]
  }
};

test('it works', function(assert) {
  const result = Indicator.create(incidentJson);
  assert.equal(result.get('name'), incidentJson.originalHeaders.name, 'Expected name to be aliased.');
  assert.equal(result.get('enrichments'), incidentJson.alert.events[0].enrichment, 'Expected enrichments to be found.');

  const evts = result.get('normalizedEvents');
  assert.equal(evts.length, incidentJson.alert.relationships.length, 'Expected to find normalized events for every relationship.');

  const lastEvt = get(evts, 'lastObject');
  assert.equal(lastEvt.time, incidentJson.timestamp, 'Expected manufactured events to inherit indicator timestamp.');
});

