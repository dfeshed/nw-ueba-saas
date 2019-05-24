import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  getEnabledUsers,
  getPriorityTypes,
  getCategoryTags,
  getAssigneeOptions
} from 'respond-shared/selectors/create-incident/selectors';

module('Unit | Utility | User Selectors');

const enabledUsers = [{ id: 'admin' }];

const createIncident = Immutable.from({
  enabledUsers,
  enabledUsersStatus: 'wait',
  priorityTypes: [],
  categoryTags: []
});

const state = {
  respondShared: {
    createIncident
  }
};

test('Basic Users selectors', function(assert) {
  assert.equal(getEnabledUsers(state), createIncident.enabledUsers, 'The returned value from the getEnabledUsers selector is as expected');
  assert.deepEqual(getAssigneeOptions(state), [{ id: 'UNASSIGNED' }, { id: 'admin' }]);
});

test('Basic dictionary selectors', function(assert) {
  assert.equal(getPriorityTypes(state), createIncident.priorityTypes, 'The returned value from the getPriorityTypes selector is as expected');
  assert.equal(getCategoryTags(state), createIncident.categoryTags, 'The returned value from the getCategoryTags selector is as expected');
});