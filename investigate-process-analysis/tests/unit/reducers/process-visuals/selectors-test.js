import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import { isEventsSelected } from 'investigate-process-analysis/reducers/process-visuals/selectors';

module('Unit | Selectors | process-visuals', function() {

  test('Returns true if Tab is Events', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        processVisuals: {
          detailsTabSelected: 'Events'
        }
      }
    });

    const data = isEventsSelected(state);
    assert.equal(data, true);
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