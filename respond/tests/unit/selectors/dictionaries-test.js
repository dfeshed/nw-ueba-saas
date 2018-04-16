import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  getPriorityTypes,
  getStatusTypes,
  getCategoryTags,
  getRemediationStatusTypes,
  getRemediationTypes,
  getAlertTypes,
  getAlertSources,
  getAlertNames,
  getMilestoneTypes,
  getEscalationStatuses
} from 'respond/selectors/dictionaries';

module('Unit | Utility | Dictionary Selectors', function() {

  const dictionaries = Immutable.from({
    priorityTypes: [],
    statusTypes: [],
    categoryTags: [],
    remediationStatusTypes: [],
    remediationTypes: [],
    alertTypes: [],
    alertSources: [],
    alertNames: [],
    milestoneTypes: [],
    escalationStatuses: ['ESCALATED', 'NON_ESCALATED']
  });

  const state = {
    respond: {
      dictionaries
    }
  };

  test('Basic dictionary selectors', function(assert) {
    assert.equal(getPriorityTypes(state), dictionaries.priorityTypes, 'The returned value from the getPriorityTypes selector is as expected');
    assert.equal(getStatusTypes(state), dictionaries.statusTypes, 'The returned value from the getStatusTypes selector is as expected');
    assert.equal(getCategoryTags(state), dictionaries.categoryTags, 'The returned value from the getCategoryTags selector is as expected');
    assert.equal(getRemediationStatusTypes(state), dictionaries.remediationStatusTypes, 'The returned value from the getRemediationStatusTypes selector is as expected');
    assert.equal(getRemediationTypes(state), dictionaries.remediationTypes, 'The returned value from the getRemediationTypes selector is as expected');
    assert.equal(getAlertTypes(state), dictionaries.alertTypes, 'The returned value from the getAlertTypes selector is as expected');
    assert.equal(getAlertSources(state), dictionaries.alertSources, 'The returned value from the getAlertSources selector is as expected');
    assert.equal(getAlertNames(state), dictionaries.alertNames, 'The returned value from the getAlertNames selector is as expected');
    assert.equal(getMilestoneTypes(state), dictionaries.milestoneTypes, 'The returned value from the getMilestoneTypes selector is as expected');
    assert.equal(getEscalationStatuses(state), dictionaries.escalationStatuses, 'The returned value from the getEscalationStatuses selector is as expected');
  });
});

