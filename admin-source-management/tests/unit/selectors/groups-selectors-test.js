import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  isGroupsLoading,
  focusedGroup,
  hasSelectedApplyPoliciesItems,
  selectedDeleteItems,
  hasSelectedDeleteItems,
  selectedPublishItems,
  hasSelectedPublishItems
} from 'admin-source-management/reducers/usm/groups-selectors';

module('Unit | Selectors | Groups Selectors');

test('isGroupsLoading selector, when wait', function(assert) {
  const result = isGroupsLoading(Immutable.from({
    usm: {
      groups: {
        itemsStatus: 'wait'
      }
    }
  }));
  assert.expect(1);
  assert.equal(result, true);
});

test('isGroupsLoading selector, when complete', function(assert) {
  const result = isGroupsLoading(Immutable.from({
    usm: {
      groups: {
        itemsStatus: 'complete'
      }
    }
  }));
  assert.expect(1);
  assert.equal(result, false);
});

test('focusedGroup selector', function(assert) {
  const state = {
    usm: {
      groups: {
        focusedItem: {
          id: 'f1',
          name: 'focusedItemData 1',
          description: 'focusedItemData 1 of state.usm.groups'
        }
      }
    }
  };
  assert.expect(1);
  assert.deepEqual(focusedGroup(Immutable.from(state)), state.usm.groups.focusedItem, 'The returned value from the focusedGroup selector is as expected');
});

test('when no items in selection', function(assert) {
  const state = {
    usm: {
      groups: {
        items: [
          {
            id: 'g1',
            dirty: false
          },
          {
            id: 'g2',
            dirty: true
          },
          {
            id: 'g3',
            dirty: false
          }
        ],
        itemsSelected: []
      }
    }
  };
  assert.expect(5);
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), [], 'selectedDeleteItems should have no items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), false, 'hasSelectedDeleteItems should return false');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), [], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), false, 'hasSelectedPublishItems should return false');
  assert.equal(hasSelectedApplyPoliciesItems(Immutable.from(state)), false, 'hasSelectedApplyPoliciesItems should return false');
});

test('when single non-dirty item in selection', function(assert) {
  const state = {
    usm: {
      groups: {
        items: [
          {
            id: 'g1',
            dirty: false
          },
          {
            id: 'g2',
            dirty: true
          },
          {
            id: 'g3',
            dirty: false
          }
        ],
        itemsSelected: ['g1']
      }
    }
  };
  assert.expect(5);
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), ['g1'], 'selectedDeleteItems should have one items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), true, 'hasSelectedDeleteItems should return true');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), [], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), false, 'hasSelectedPublishItems should return false');
  assert.equal(hasSelectedApplyPoliciesItems(Immutable.from(state)), false, 'hasSelectedApplyPoliciesItems should return false');
});

test('when multiple non-dirty items in selection', function(assert) {
  const state = {
    usm: {
      groups: {
        items: [
          {
            id: 'g1',
            dirty: false
          },
          {
            id: 'g2',
            dirty: true
          },
          {
            id: 'g3',
            dirty: false
          }
        ],
        itemsSelected: ['g1', 'g3']
      }
    }
  };
  assert.expect(5);
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), ['g1', 'g3'], 'selectedDeleteItems should have two items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), true, 'hasSelectedDeleteItems should return true');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), [], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), false, 'hasSelectedPublishItems should return false');
  assert.equal(hasSelectedApplyPoliciesItems(Immutable.from(state)), false, 'hasSelectedApplyPoliciesItems should return false');
});

test('when multiple with dirty items in selection', function(assert) {
  const state = {
    usm: {
      groups: {
        items: [
          {
            id: 'g1',
            dirty: false
          },
          {
            id: 'g2',
            dirty: true
          },
          {
            id: 'g3',
            dirty: false
          }
        ],
        itemsSelected: ['g1', 'g2', 'g3']
      }
    }
  };
  assert.expect(5);
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), ['g1', 'g2', 'g3'], 'selectedDeleteItems should have three items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), true, 'hasSelectedDeleteItems should return true');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), ['g2'], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), true, 'hasSelectedPublishItems should return true');
  assert.equal(hasSelectedApplyPoliciesItems(Immutable.from(state)), false, 'hasSelectedApplyPoliciesItems should return false');
});
