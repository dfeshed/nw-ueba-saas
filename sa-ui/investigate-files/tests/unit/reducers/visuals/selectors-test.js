import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import { selectedTabComponent, getFileDetailTabs, getDataSourceTab, displayCloseRightPanel, riskState } from 'investigate-files/reducers/visuals/selectors';

module('Unit | selectors | visuals');

test('getFileDetailTabs', function(assert) {
  const state = Immutable.from({
    files: {
      visuals: {
        activeFileDetailTab: 'OVERVIEW'
      }
    }
  });
  const result = getFileDetailTabs(state).findBy('name', 'OVERVIEW');
  assert.equal(result.selected, true, 'OVERVIEW tab should be selected');
});

test('riskState', function(assert) {
  const state = Immutable.from({
    files: {
      risk: {
        isRiskScoreReset: true,
        activeRiskSeverityTab: 'critical',
        currentEntityId: 'ec436aeee41857eee5875efdb7166fe043349db5f58f3ee9fc4ff7f50005767f',
        riskScoreContext: {
          id: 'ec436aeee41857eee5875efdb7166fe043349db5f58f3ee9fc4ff7f50005767f',
          distinctAlertCount: {
            critical: 2,
            high: 1,
            medium: 9,
            low: 0
          },
          categorizedAlerts: {
            Critical: {
              'Enables Login Bypass': {
                alertCount: 1,
                eventCount: 1
              },
              'Possible Mimikatz Activity': {
                alertCount: 3,
                eventCount: 3
              }
            }
          }
        }
      }
    }
  });
  const result = riskState(state);
  assert.deepEqual(result.activeRiskSeverityTab, 'critical', 'active severity tab is set');
});

test('riskState default', function(assert) {
  const state = Immutable.from({
    files: {
      risk: null
    }
  });
  const result = riskState(state);
  assert.deepEqual(result, {}, 'Default risk state is set');
});

test('selectedTabComponent for default tab', function(assert) {
  const state = Immutable.from({
    files: { visuals: { } }
  });
  const result = selectedTabComponent(state);
  assert.equal(result, 'file-details/overview', 'returns the default tab component class');
});

test('selectedTabComponent for different tab', function(assert) {
  const state = Immutable.from({
    files: {
      visuals: {
        activeFileDetailTab: 'ANALYSIS'
      }
    }
  });
  const result = selectedTabComponent(state);
  assert.equal(result, 'file-details/file-analysis-wrapper', 'returns the selected tab component class for ANALYSIS');
});

test('getDataSourceTab', function(assert) {
  const state = Immutable.from({
    files: {
      visuals: {
        activeDataSourceTab: 'RISK_PROPERTIES'
      }
    }
  });
  const result = getDataSourceTab(state).findBy('name', 'RISK_PROPERTIES');
  assert.equal(result.selected, true, 'Incidents Tab should be selected');
});

test('getDataSourceTab default', function(assert) {
  const state = Immutable.from({
    files: {
      visuals: {
        activeDataSourceTab: null
      }
    }
  });
  const result = getDataSourceTab(state).findBy('name', 'FILE_DETAILS');
  assert.equal(result.selected, true, 'Incidents Tab should be selected');
});


test('displayCloseRightPanel returns true or false depending on the tab selected and config', function(assert) {
  const stateOverview = Immutable.from({
    files: {
      visuals: {
        activeFileDetailTab: 'OVERVIEW'
      }
    }
  });
  const resultOverView = displayCloseRightPanel(stateOverview);
  assert.equal(resultOverView, true, 'displayCloseRightPanel will return true');

  const stateAnalysis = Immutable.from({
    files: {
      visuals: {
        activeFileDetailTab: 'ANALYSIS'
      }
    }
  });
  const resultAnalysis = displayCloseRightPanel(stateAnalysis);
  assert.equal(resultAnalysis, false, 'displayCloseRightPanel will return false');
});