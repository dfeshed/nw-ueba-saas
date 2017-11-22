import { module, test } from 'qunit';
import { _buildInvestigateUrl } from 'investigate-events/helpers/build-context-menu';

module('Unit | Utils | Build Context Menu');

test('it builds the correct investigation drill URL', function(assert) {
  const selection = {
    metaName: 'ip.src',
    metaValue: '1.1.1.1'
  };
  const contextDetails = {
    endpointId: 'service-1',
    startTime: 1510768478,
    endTime: 1510768488,
    queryConditions: [
      {
        meta: 'ip.dst',
        value: '2.2.2.2',
        operator: '='
      },
      {
        meta: 'device.type',
        value: 'cisco',
        operator: '='
      }
    ],
    language: [{ metaName: 'device.type', format: 'Text' }]
  };
  const investigateUrl = _buildInvestigateUrl(selection, '=', contextDetails);
  const expected = '/investigation/endpointid/service-1/navigate/query/(ip.dst%20%3D%202.2.2.2%20%26%26%20device.type%20%3D%20\'cisco\')%20%26%26%20ip.src%20%3D%201.1.1.1/date/2017-11-15T17:54:38Z/2017-11-15T17:54:48Z';
  assert.equal(investigateUrl, expected, 'Investigate URL should be compiled properly');
});

test('it builds the correct investigation refocus URL', function(assert) {
  const selection = {
    metaName: 'ip.src',
    metaValue: '1.1.1.1'
  };
  const contextDetails = {
    endpointId: 'service-1',
    startTime: 1510768478,
    endTime: 1510768488,
    queryConditions: [
      {
        meta: 'ip.dst',
        value: '2.2.2.2',
        operator: '='
      },
      {
        meta: 'device.type',
        value: 'cisco',
        operator: '='
      }]
  };
  const investigateUrl = _buildInvestigateUrl(selection, '=', contextDetails, true);
  const expectedUrl = '/investigation/endpointid/service-1/navigate/query/ip.src%20%3D%201.1.1.1/date/2017-11-15T17:54:38Z/2017-11-15T17:54:48Z';
  assert.equal(investigateUrl, expectedUrl, 'Investigate URL should be compiled properly');
});