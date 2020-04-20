import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  getStatusTypes,
  getRemediationStatusTypes,
  getAlertTypes,
  getAlertSources,
  getAlertNames,
  getMilestoneTypes
} from 'respond/selectors/dictionaries';

module('Unit | Utility | Dictionary Selectors', function() {

  const dictionaries = Immutable.from({
    statusTypes: [],
    remediationStatusTypes: [],
    alertTypes: [],
    alertSources: [],
    alertNames: [],
    milestoneTypes: []
  });

  const state = {
    respond: {
      dictionaries
    }
  };

  test('Basic dictionary selectors', function(assert) {
    assert.equal(getStatusTypes(state), dictionaries.statusTypes, 'The returned value from the getStatusTypes selector is as expected');
    assert.equal(getRemediationStatusTypes(state), dictionaries.remediationStatusTypes, 'The returned value from the getRemediationStatusTypes selector is as expected');
    assert.equal(getAlertTypes(state), dictionaries.alertTypes, 'The returned value from the getAlertTypes selector is as expected');
    assert.equal(getAlertSources(state), dictionaries.alertSources, 'The returned value from the getAlertSources selector is as expected');
    assert.equal(getAlertNames(state), dictionaries.alertNames, 'The returned value from the getAlertNames selector is as expected');
    assert.equal(getMilestoneTypes(state), dictionaries.milestoneTypes, 'The returned value from the getMilestoneTypes selector is as expected');
  });
});

