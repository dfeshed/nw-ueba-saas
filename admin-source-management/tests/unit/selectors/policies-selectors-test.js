import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  isPoliciesLoading,
  focusedPolicy,
  selectedEditItem,
  hasSelectedEditItem,
  selectedDeleteItems,
  hasSelectedDeleteItems,
  selectedPublishItems,
  hasSelectedPublishItems
} from 'admin-source-management/reducers/usm/policies-selectors';

module('Unit | Selectors | Policies Selectors');

test('isPoliciesLoading selector', function(assert) {
  const result = isPoliciesLoading(Immutable.from({
    usm: {
      policies: {
        itemsStatus: 'wait'
      }
    }
  }));
  assert.expect(1);
  assert.equal(result, true);
});

test('isPoliciesLoading selector, when complete', function(assert) {
  const result = isPoliciesLoading(Immutable.from({
    usm: {
      policies: {
        itemsStatus: 'complete'
      }
    }
  }));
  assert.expect(1);
  assert.equal(result, false);
});

test('focusedPolicy selector', function(assert) {
  const state = {
    usm: {
      policies: {
        focusedItem: {
          id: 'f1',
          name: 'focusedItemData 1',
          description: 'focusedItemData 1 of state.usm.policies'
        }
      }
    }
  };
  assert.expect(1);
  assert.deepEqual(focusedPolicy(Immutable.from(state)), state.usm.policies.focusedItem, 'The returned value from the focusedPolicy selector is as expected');
});

test('when no items in selection', function(assert) {
  const state = {
    usm: {
      policies: {
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
  assert.expect(6);
  assert.deepEqual(selectedEditItem(Immutable.from(state)), 'none', 'selectedEditItem should have no items');
  assert.equal(hasSelectedEditItem(Immutable.from(state)), false, 'hasSelectedEditItem should return false');
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), [], 'selectedDeleteItems should have no items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), false, 'hasSelectedDeleteItems should return false');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), [], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), false, 'hasSelectedPublishItems should return false');
});

test('when single non-dirty item in selection', function(assert) {
  const state = {
    usm: {
      policies: {
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
  assert.expect(6);
  assert.deepEqual(selectedEditItem(Immutable.from(state)), 'g1', 'selectedEditItem should have one items');
  assert.equal(hasSelectedEditItem(Immutable.from(state)), true, 'hasSelectedEditItem should return true');
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), ['g1'], 'selectedDeleteItems should have one items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), true, 'hasSelectedDeleteItems should return true');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), [], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), false, 'hasSelectedPublishItems should return false');
});

test('when single default policy item in selection', function(assert) {
  const state = {
    usm: {
      policies: {
        items: [
          {
            id: 'g1',
            dirty: false,
            defaultPolicy: true
          },
          {
            id: 'g2',
            dirty: true,
            defaultPolicy: true
          },
          {
            id: 'g3',
            dirty: false,
            defaultPolicy: true
          }
        ],
        itemsSelected: ['g1']
      }
    }
  };
  assert.expect(6);
  assert.deepEqual(selectedEditItem(Immutable.from(state)), 'g1', 'selectedEditItem should have one items');
  assert.equal(hasSelectedEditItem(Immutable.from(state)), true, 'hasSelectedEditItem should return true');
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), [], 'selectedDeleteItems should have no items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), false, 'hasSelectedDeleteItems should return false');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), [], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), false, 'hasSelectedPublishItems should return false');
});


test('when multiple non-dirty items in selection', function(assert) {
  const state = {
    usm: {
      policies: {
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
  assert.expect(6);
  assert.deepEqual(selectedEditItem(Immutable.from(state)), 'none', 'selectedEditItem should have no items');
  assert.equal(hasSelectedEditItem(Immutable.from(state)), false, 'hasSelectedEditItem should return false');
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), ['g1', 'g3'], 'selectedDeleteItems should have two items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), true, 'hasSelectedDeleteItems should return true');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), [], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), false, 'hasSelectedPublishItems should return false');
});

test('when multiple with dirty items in selection', function(assert) {
  const state = {
    usm: {
      policies: {
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
  assert.expect(6);
  assert.deepEqual(selectedEditItem(Immutable.from(state)), 'none', 'selectedEditItem should have no items');
  assert.equal(hasSelectedEditItem(Immutable.from(state)), false, 'hasSelectedEditItem should return false');
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), ['g1', 'g2', 'g3'], 'selectedDeleteItems should have three items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), true, 'hasSelectedDeleteItems should return true');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), ['g2'], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), true, 'hasSelectedPublishItems should return true');
});

test('when multiple items in selection including a default Policy item', function(assert) {
  const state = {
    usm: {
      policies: {
        items: [
          {
            id: 'g1',
            dirty: false,
            defaultPolicy: true
          },
          {
            id: 'g2',
            dirty: true,
            defaultPolicy: false
          },
          {
            id: 'g3',
            dirty: false,
            defaultPolicy: false
          }
        ],
        itemsSelected: ['g1', 'g2']
      }
    }
  };
  assert.expect(6);
  assert.deepEqual(selectedEditItem(Immutable.from(state)), 'none', 'selectedEditItem should have no items');
  assert.equal(hasSelectedEditItem(Immutable.from(state)), false, 'hasSelectedEditItem should return false');
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), ['g2'], 'selectedDeleteItems should not include default Policy items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), true, 'hasSelectedDeleteItems should return true');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), ['g2'], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), true, 'hasSelectedPublishItems should return true');
});
