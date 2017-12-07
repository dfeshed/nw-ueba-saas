import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { getCurrentPreferences } from 'investigate-events/reducers/investigate/data-selectors';

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
