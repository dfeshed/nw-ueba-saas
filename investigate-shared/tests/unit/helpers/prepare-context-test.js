import { prepareContext } from 'investigate-shared/helpers/prepare-context';
import { module, test } from 'qunit';

const CONTEXT = [{
  Alerts: {
    resultList: [{
      alert: {
        name: 'Autorun Key contains Non-printable characters',
        numEvents: 1,
        risk_score: 70,
        source: 'Event Stream Analysis'
      },
      '_id': {
        '$oid': '5afcffbedb7a8b75269a0040'
      },
      incidentId: 'INC-167'
    }],
    resultMeta: {}
  },
  Incidents: {
    resultList: [{
      alertCount: 1,
      averageAlertRiskScore: 70,
      created: { $date: '2018-06-12T14:30:50.236Z' },
      name: 'ESA BY MD5',
      priority: 'HIGH',
      status: 'NEW',
      _id: 'INC-167'
    }],
    resultMeta: { } }
}];

module('Unit | Helper | prepare context', function() {
  test('check Alert is properly prepared', function(assert) {
    const result = prepareContext([CONTEXT, 'ALERT']);
    assert.equal(Object.keys(result.resultList[0]).length, 6);
  });

  test('check Incident is properly prepared', function(assert) {
    const result = prepareContext([CONTEXT, 'INCIDENT']);
    assert.equal(Object.keys(result.resultList[0]).length, 7);
  });
});