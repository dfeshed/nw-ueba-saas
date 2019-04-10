import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import groupWizardCreators from 'admin-source-management/actions/creators/group-wizard-creators';
import ACTION_TYPES from 'admin-source-management/actions/types';


const getState = () => {
  return {
    usm: {
      groupWizard: {
        groupRanking: [1, 2, 3, 4]
      }
    }
  };
};

module('Unit | Actions | group wizard creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('reorderRanking action creator returns proper type and payload and action on key arrowDown', function(assert) {
    const payloadResult = {
      'groupRankingNew': [1, 3, 2, 4]
    };
    const dispatch = (action) => {
      assert.equal(action.type, ACTION_TYPES.REORDER_GROUP_RANKING, 'action has the correct type');
      assert.deepEqual(action.payload, payloadResult, 'payload has the correct vals');
    };
    const thunk = groupWizardCreators.reorderRanking('arrowDown', 1);
    thunk(dispatch, getState);
  });

  test('reorderRanking action on key arrowUp', function(assert) {
    const payloadResult = {
      'groupRankingNew': [2, 1, 3, 4]
    };
    const dispatch = (action) => {
      assert.deepEqual(action.payload, payloadResult, 'payload has the correct vals');
    };
    const thunk = groupWizardCreators.reorderRanking('arrowUp', 1);
    thunk(dispatch, getState);
  });

  test('reorderRanking action on key arrowUp last record selected', function(assert) {
    const payloadResult = {
      'groupRankingNew': [1, 2, 4, 3]
    };
    const dispatch = (action) => {
      assert.deepEqual(action.payload, payloadResult, 'payload has the correct vals');
    };
    const thunk = groupWizardCreators.reorderRanking('arrowUp', 3);
    thunk(dispatch, getState);
  });

  test('reorderRanking action on reorder group ranking by dragging', function(assert) {
    const payloadResult = {
      'groupRankingNew': [1, 2, 4, 3]
    };
    const dispatch = (action) => {
      assert.deepEqual(action.payload, payloadResult, 'payload has the correct vals');
    };
    const thunk = groupWizardCreators.reorderRanking([1, 2, 4, 3]);
    thunk(dispatch, getState);
  });

  const newSetState = () => {
    return {
      usm: {
        groupWizard: {
          groupRanking: [{ id: 1, isChecked: true }, { id: 2 }, { id: 3 }],
          focusedItem: { foo: 123 },
          selectedSourceType: 'edrPolicy'
        }
      }
    };
  };

  test('reorderRanking action on group ranking with simulation', function(assert) {
    const payloadResult = {
      'groupRankingNew': [{ id: 1, isChecked: true }, { id: 2 }, { id: 3 }]
    };
    const dispatch = (action) => {
      assert.deepEqual(action.payload, payloadResult, 'payload has the correct vals');
    };
    const thunk = groupWizardCreators.reorderRanking([{ id: 1, isChecked: true }, { id: 2 }, { id: 3 }]);
    thunk(dispatch, newSetState);
  });

});