import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import {
  riskScoringServerError,
  isRiskScoreContextEmpty
} from 'investigate-shared/selectors/risk/selectors';

module('Unit | Selectors | endpoint filters', function(hooks) {

  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('check risk scoring server is returning error or not', function(assert) {
    let state = Immutable.from({
      risk: {
        riskScoreContextError: {
          error: 'mongo.connection.failed'
        }
      }
    });
    let data = riskScoringServerError(state);
    assert.notEqual(data.length, 0);

    state = Immutable.from({
      risk: {
        riskScoreContextError: null
      }
    });
    data = riskScoringServerError(state);
    assert.equal(data, null);
  });

  test('check risk score context is empty or not', function(assert) {
    let state = Immutable.from({
      risk: {
        riskScoreContext: {
          distinctAlertCount: {
            critical: 1,
            high: 0,
            medium: 0
          }
        }
      }
    });
    let data = isRiskScoreContextEmpty(state);
    assert.equal(data, false);

    state = Immutable.from({
      risk: {
        riskScoreContext: {
          distinctAlertCount: {
            critical: 0,
            high: 0,
            medium: 0
          }
        }
      }
    });
    data = isRiskScoreContextEmpty(state);
    assert.equal(data, true);
  });


});
