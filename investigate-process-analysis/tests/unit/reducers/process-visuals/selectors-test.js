import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import { isEventsSelected, allAlertCount, riskState } from 'investigate-process-analysis/reducers/process-visuals/selectors';

module('Unit | Selectors | process-visuals', function() {

  test('Returns true if Tab is Events', function(assert) {
    const state1 = Immutable.from({
      processAnalysis: {
        processVisuals: {
          detailsTabSelected: { name: 'events' }
        }
      }
    });
    const state2 = Immutable.from({
      processAnalysis: {
        processVisuals: {
          detailsTabSelected: { name: 'hosts' }
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

  test('Returns all alerts count', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        risk: {
          riskScoreContext: {
            distinctAlertCount: {
              critical: 1,
              medium: 2,
              low: 0,
              high: 0
            }
          }
        }
      }
    });

    const data = allAlertCount(state);
    assert.equal(data, 3);
  });

  test('Returns all alerts count ( 0 )', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        risk: {
          riskScoreContext: null
        }
      }
    });

    const data = allAlertCount(state);
    assert.equal(data, 0);
  });

  test('Returns risk state', function(assert) {
    const state = Immutable.from({
      processAnalysis: {
        risk: {
          riskScoreContext: {
            distinctAlertCount: {
              critical: 1
            }
          }
        }
      }
    });

    const data = riskState(state);
    assert.equal(data.riskScoreContext.distinctAlertCount.critical, 1);
  });
});
