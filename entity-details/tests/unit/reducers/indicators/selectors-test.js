import { module, test } from 'qunit';
import { indicatorId, eventFilter, getIncidentData, getIncidentKey } from 'entity-details/reducers/indicators/selectors';
import userAlerts from '../../../data/presidio/user_alerts';
import indicatorEvents from '../../../data/presidio/indicator-events';
import indicatorCount from '../../../data/presidio/indicator-count';

module('Unit | Selector | Indicators Selector');

const state = {
  alerts: {
    selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
    alerts: userAlerts.data
  },
  indicators: {
    indicatorId: '8614aa7f-c8ee-4824-9eaf-e0bb199cd006',
    events: indicatorEvents.data,
    historicalData: indicatorCount.data,
    eventFilter: {
      page: 1,
      size: 100,
      sort_direction: 'DESC'
    }
  }
};

test('test indicator state for indicator ID', function(assert) {
  assert.equal(indicatorId(state), '8614aa7f-c8ee-4824-9eaf-e0bb199cd006');
});
test('test event filter state for getting events data', function(assert) {
  assert.deepEqual(eventFilter(state), {
    page: 1,
    size: 100,
    sort_direction: 'DESC'
  });
});
test('test indicator state for selected indicator data', function(assert) {
  assert.equal(getIncidentData(state), userAlerts.data[0].evidences[0]);
});
test('test indicator state for seelcted incident key', function(assert) {
  assert.equal(getIncidentKey(state), 'abnormal_event_day_time');
});