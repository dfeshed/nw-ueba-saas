import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { getCurrentPreferences, isDataEmpty, shouldShowStatus } from 'investigate-events/reducers/investigate/data-selectors';

module('Unit | Selectors | data-selectors');

test('get the current preferences to save', function(assert) {
  const response = {
    eventAnalysisPreferences: {
      isReconExpanded: true
    }
  };
  const state = Immutable.from({
    investigate: {
      data: {
        reconSize: 'max'
      }
    },
    recon: {
      visuals: {
        currentReconView: 'TEXT',
        defaultLogFormat: 'LOG',
        defaultPacketFormat: 'PCAP',
        isReconExpanded: false
      }
    }
  });
  const preferences = getCurrentPreferences(state);
  assert.equal(preferences.eventAnalysisPreferences.isReconExpanded, true);
  assert.deepEqual(preferences, response);
});

test('data is not empty', function(assert) {
  const state = {
    investigate: {
      eventTimeline: {
        data: new Date(),
        status: 'resolved'
      }
    }
  };
  const dataEmpty = isDataEmpty(state);

  assert.equal(dataEmpty, false);
});

test('data is empty and status is resolved', function(assert) {
  const state = {
    investigate: {
      eventTimeline: {
        data: null,
        status: 'resolved'
      }
    }
  };
  const dataEmpty = isDataEmpty(state);

  assert.equal(dataEmpty, true);
});


test('should show status', function(assert) {
  const state = {
    investigate: {
      eventTimeline: {
        data: 1,
        status: 'wait'
      }
    }
  };
  const showStatus = shouldShowStatus(state);

  assert.equal(showStatus, true);
});

test('no need to show status', function(assert) {
  const state = {
    investigate: {
      eventTimeline: {
        data: new Date(),
        status: 'resolved'
      }
    }
  };
  const showStatus = shouldShowStatus(state);

  assert.equal(showStatus, false);
});

