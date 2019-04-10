import { module, test } from 'qunit';
import { selectedIndicatorId, eventFilter, indicatorEvents, historicalData, areAllEventsReceived, getIncidentData, getIncidentKey, getIndicatorEntity, getIncidentPositionAndNextIncidentId } from 'entity-details/reducers/indicators/selectors';
import userAlerts from '../../../data/presidio/user_alerts';
import indicatorEventsData from '../../../data/presidio/indicator-events';
import indicatorCount from '../../../data/presidio/indicator-count';

module('Unit | Selector | Indicators Selector');

const state = {
  alerts: {
    selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
    alerts: userAlerts.data
  },
  indicators: {
    selectedIndicatorId: '8614aa7f-c8ee-4824-9eaf-e0bb199cd006',
    events: indicatorEventsData.data,
    historicalData: indicatorCount.data,
    totalEvents: 100,
    eventFilter: {
      page: 1,
      size: 100,
      sort_direction: 'DESC'
    }
  }
};

test('test indicator state for indicator ID', function(assert) {
  assert.equal(selectedIndicatorId(state), '8614aa7f-c8ee-4824-9eaf-e0bb199cd006');
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
test('test indicator state for selected incident key', function(assert) {
  assert.equal(getIncidentKey(state), 'abnormal_event_day_time');
});
test('test indicatorEvents for selected incident', function(assert) {
  assert.deepEqual(indicatorEvents(state), indicatorEventsData.data);
});
test('test historicalData for selected incident', function(assert) {
  assert.deepEqual(historicalData(state), indicatorCount.data);
});
test('test allEventsReceived for selected incident to stop scrollbar', function(assert) {
  assert.equal(areAllEventsReceived(state), false);
});
test('test getIndicatorEntity for selected incident', function(assert) {
  assert.equal(getIndicatorEntity(state), 'file');
});
test('test getIncidentPositionAndNextIncidentId for selected incident', function(assert) {
  assert.deepEqual(getIncidentPositionAndNextIncidentId(state), {
    currentPosition: 1,
    indicatorLength: 9,
    nextIndicatorId: '277cdcca-4a55-4d0f-8a8e-750aee9c438f',
    previousIndicatorId: null
  });
});

test('test getIncidentPositionAndNextIncidentId should return current position as 0 if alert is selected', function(assert) {
  const newState = {
    alerts: {
      selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
      alerts: userAlerts.data
    },
    indicators: {
      selectedIndicatorId: null,
      events: indicatorEventsData.data,
      historicalData: indicatorCount.data,
      totalEvents: 100,
      eventFilter: {
        page: 1,
        size: 100,
        sort_direction: 'DESC'
      }
    }
  };
  assert.deepEqual(getIncidentPositionAndNextIncidentId(newState), {
    currentPosition: 0,
    indicatorLength: 9,
    nextIndicatorId: '8614aa7f-c8ee-4824-9eaf-e0bb199cd006',
    previousIndicatorId: null
  });
});

test('test getIncidentPositionAndNextIncidentId should return correct position if some different indicator is selected', function(assert) {
  const newState = {
    alerts: {
      selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
      alerts: userAlerts.data
    },
    indicators: {
      selectedIndicatorId: '277cdcca-4a55-4d0f-8a8e-750aee9c438f',
      events: indicatorEventsData.data,
      historicalData: indicatorCount.data,
      totalEvents: 100,
      eventFilter: {
        page: 1,
        size: 100,
        sort_direction: 'DESC'
      }
    }
  };
  assert.deepEqual(getIncidentPositionAndNextIncidentId(newState), {
    currentPosition: 2,
    indicatorLength: 9,
    nextIndicatorId: 'fb8a98a7-be9a-4cbb-b95d-b4f0c3239225',
    previousIndicatorId: '8614aa7f-c8ee-4824-9eaf-e0bb199cd006'
  });
});
