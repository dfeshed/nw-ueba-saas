import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  areSomeScanning,
  hostExportLink,
  processedHostList,
  isAllHostSelected,
  noHostsSelected,
  serviceList,
  allAreEcatAgents,
  areAnyEcatAgents } from 'investigate-hosts/reducers/hosts/selectors';

module('Unit | selectors | hosts');
const STATE = Immutable.from({
  endpoint: {
    machines: {
      hostList: [
        {
          id: 1,
          agentStatus: {
            scanStatus: 'scaning'
          },
          machine: {
            agentVersion: '4.3.0.0'
          }
        },
        {
          id: 2,
          agentStatus: {
            scanStatus: 'idle'
          },
          machine: {
            agentVersion: '4.4.0.0'
          }
        }
      ],
      hostExportLinkId: 123,
      selectedHostList: [{
        id: 1,
        version: '4.3.0.0'
      }],
      listOfServices: [
        {
          name: 'broker'
        },
        {
          name: 'endpoint'
        }
      ]
    }
  }
});
test('areSomeScanning', function(assert) {
  const result = areSomeScanning(STATE);
  assert.equal(result, true, 'should return true as some are scanning');
});

test('hostExportLink', function(assert) {
  const result = hostExportLink(STATE);
  assert.equal(result, `${location.origin}/rsa/endpoint/machine/property/download?id=123`, 'should return the export link');
});

test('isAllHostSelected', function(assert) {
  const result = isAllHostSelected(STATE);
  assert.equal(result, false);
});

test('noHostsSelected', function(assert) {
  const result = noHostsSelected(STATE);
  assert.equal(result, false);
});

test('processedHostList', function(assert) {
  const result = processedHostList(STATE);
  assert.equal(result.length, 2);
  assert.equal(result[0].canStartScan, false);
  assert.equal(result[1].canStartScan, true);
  assert.equal(result[0].selected, true);
  assert.equal(result[1].selected, false);
});

test('serviceList', function(assert) {
  const result = serviceList(STATE);

  assert.equal(result.length, 1);
});

test('allAreEcatAgents, check all are 4.4 agents', function(assert) {
  const result = allAreEcatAgents(Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [
          {
            id: '1',
            version: '4.4.0.0'
          },
          {
            id: '2',
            version: '4.4.0.1'
          }
        ]
      }
    }
  }));
  assert.equal(result, true);
});

test('areAnyEcatAgents, check some are 4.4 agents', function(assert) {
  const result = areAnyEcatAgents(Immutable.from({
    endpoint: {
      machines: {
        selectedHostList: [
          {
            id: '1',
            version: '4.4.0.0'
          },
          {
            id: '2',
            version: '4.2.0.1'
          }
        ]
      }
    }
  }));
  assert.equal(result, true);
});

