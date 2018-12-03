import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import {
  riskScoringServerError,
  isRiskScoreContextEmpty,
  currentSeverityContext,
  riskType
} from 'investigate-shared/selectors/risk/selectors';

module('Unit | Selectors | risk', function(hooks) {

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
            medium: 0,
            low: 0
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
            medium: 0,
            low: 0
          }
        }
      }
    });
    data = isRiskScoreContextEmpty(state);
    assert.equal(data, true);
  });

  test('currentSeverityContext', function(assert) {
    const state = Immutable.from({
      risk: {
        riskScoreContext: {
          hash: 'f49c0dde83a6c8e2a5a9436ce2484cb8be2fc36a50528abb2d3ee5ddee2744c8',
          distinctAlertCount: {
            critical: 1,
            high: 1,
            medium: 0,
            low: 0
          },
          categorizedAlerts: {
            Critical: {
              'Enables Login Bypass': {
                alertCount: 1,
                eventContexts: [
                  {
                    id: '5bd84e048e090168ea067125',
                    sourceId: '6667a7b8-ad3a-4ce6-b2c2-f96e2f44c7d0',
                    source: 'Respond'
                  }
                ]
              }
            }
          }
        },
        activeRiskSeverityTab: 'critical'
      }
    });

    const [data] = currentSeverityContext(state);
    assert.equal(data.alertCount, 1, 'Alert count is correct');
    assert.equal(data.alertName, 'Enables Login Bypass', 'Alert name is correct');
    assert.equal(data.context.length, 1, 'All alerts are present');
    assert.equal(data.eventCount, 1, 'Event count is correct');
    assert.equal(data.filesCount, 0, 'Files count is not yet there');
    assert.equal(data.usersCount, 0, 'Events count is not yet there');
  });
  test('Returns FILE riskType', function(assert) {
    const riskState = Immutable.from({
      riskType: 'FILE'
    });
    const fileState = Immutable.from({
      files: {}
    });
    const data = riskType(fileState, riskState);
    assert.equal(data, 'FILE', 'Risk Type will be FILE');
  });

  test('Returns HOST riskType', function(assert) {
    const riskState = Immutable.from({
      riskType: 'HOST'
    });
    const fileState = Immutable.from({});
    const data = riskType(fileState, riskState);
    assert.equal(data, 'HOST', 'Risk Type will be HOST');
  });

});
