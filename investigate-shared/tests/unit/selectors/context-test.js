import { module, test } from 'qunit';
import {
  getAlertsCount,
  getIncidentsCount
} from 'investigate-shared/selectors/context';
import Immutable from 'seamless-immutable';


module('Unit | Selectors | context');
const CONTEXT = [{
  Alerts: { resultList: [{}, {}, {}] },
  Incidents: { resultList: [{}, {}] }
}];

test('Check Alerts and Incidents count', function(assert) {
  // If agentId is present, activeHostPropertyTab will be used
  const state = Immutable.from({
    endpoint: {
      detailsInput: { agentId: 'abc' },
      visuals: {
        lookupData: CONTEXT,
        activeHostPropertyTab: 'INCIDENT',
        activeDataSourceTab: 'ALERT'
      }
    }
  });
  assert.equal(getIncidentsCount(state), 2, 'Incident count is correct');
  assert.equal(getAlertsCount(state), 3, 'Alerts count is correct');
});