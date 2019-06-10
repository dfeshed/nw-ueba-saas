import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import { isEventsSelected } from 'investigate-process-analysis/reducers/process-visuals/selectors';

module('Unit | Selectors | process-visuals', function() {

  test('Returns true if Tab is Events', function(assert) {
    const state1 = Immutable.from({
      processAnalysis: {
        processVisuals: {
          detailsTabSelected: { name: 'Events' }
        }
      }
    });
    const state2 = Immutable.from({
      processAnalysis: {
        processVisuals: {
          detailsTabSelected: { name: 'Hosts' }
        }
      }
    });

    const data1 = isEventsSelected(state1);
    const data2 = isEventsSelected(state2);
    assert.equal(data1, true);
    assert.equal(data2, false);
  });

  test('Returns false if Tab is not Events', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processVisuals: {
          detailsTabSelected: 'Properties'
        }
      }
    });

    const data = isEventsSelected(state);
    assert.equal(data, false);
  });
});