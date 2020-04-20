import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../helpers/redux-data-helper';

import {
  isSourcesLoading,
  focusedSource,
  selectedEditItem,
  hasSelectedEditItem,
  selectedDeleteItems,
  hasSelectedDeleteItems,
  selectedPublishItems,
  hasSelectedPublishItems,
  filterTypesConfig
} from 'admin-source-management/reducers/usm/sources-selectors';

module('Unit | Selectors | Sources Selectors');

test('isSourcesLoading selector', function(assert) {
  const result = isSourcesLoading(Immutable.from({
    usm: {
      sources: {
        itemsStatus: 'wait'
      }
    }
  }));
  assert.expect(1);
  assert.equal(result, true);
});

test('isSourcesLoading selector, when complete', function(assert) {
  const result = isSourcesLoading(Immutable.from({
    usm: {
      sources: {
        itemsStatus: 'complete'
      }
    }
  }));
  assert.expect(1);
  assert.equal(result, false);
});

test('focusedSource selector', function(assert) {
  const state = {
    usm: {
      sources: {
        focusedItem: {
          id: 'f1',
          name: 'focusedItemData 1',
          description: 'focusedItemData 1 of state.usm.sources'
        }
      }
    }
  };
  assert.expect(1);
  assert.deepEqual(focusedSource(Immutable.from(state)), state.usm.sources.focusedItem, 'The returned value from the focusedSource selector is as expected');
});

test('when no items in selection', function(assert) {
  const state = {
    usm: {
      sources: {
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
      sources: {
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

test('when single default source item in selection', function(assert) {
  const state = {
    usm: {
      sources: {
        items: [
          {
            id: 'g1',
            dirty: false,
            defaultSource: true
          },
          {
            id: 'g2',
            dirty: true,
            defaultSource: true
          },
          {
            id: 'g3',
            dirty: false,
            defaultSource: true
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

test('when single default edr log source item in selection', function(assert) {
  const state = {
    usm: {
      sources: {
        items: [
          {
            id: '__default_edr_source',
            dirty: false,
            defaultSource: true
          },
          {
            id: 'g2',
            dirty: true,
            defaultSource: true
          },
          {
            id: 'g3',
            dirty: false,
            defaultSource: true
          }
        ],
        itemsSelected: ['__default_edr_source']
      }
    }
  };
  assert.expect(6);
  assert.deepEqual(selectedEditItem(Immutable.from(state)), '__default_edr_source', 'selectedEditItem should have one items');
  assert.equal(hasSelectedEditItem(Immutable.from(state)), true, 'hasSelectedEditItem should return true');
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), [], 'selectedDeleteItems should have no items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), false, 'hasSelectedDeleteItems should return false');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), [], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), false, 'hasSelectedPublishItems should return false');
});

test('when single default windows log source item in selection', function(assert) {
  const state = {
    usm: {
      sources: {
        items: [
          {
            id: '__default_windows_log_source',
            dirty: false,
            defaultSource: true
          },
          {
            id: 'g2',
            dirty: true,
            defaultSource: true
          },
          {
            id: 'g3',
            dirty: false,
            defaultSource: true
          }
        ],
        itemsSelected: ['__default_windows_log_source']
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

test('when multiple non-dirty items in selection', function(assert) {
  const state = {
    usm: {
      sources: {
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
      sources: {
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

test('when multiple items in selection including a default Source item', function(assert) {
  const state = {
    usm: {
      sources: {
        items: [
          {
            id: 'g1',
            dirty: false,
            defaultSource: true
          },
          {
            id: 'g2',
            dirty: true,
            defaultSource: false
          },
          {
            id: 'g3',
            dirty: false,
            defaultSource: false
          }
        ],
        itemsSelected: ['g1', 'g2']
      }
    }
  };
  assert.expect(6);
  assert.deepEqual(selectedEditItem(Immutable.from(state)), 'none', 'selectedEditItem should have no items');
  assert.equal(hasSelectedEditItem(Immutable.from(state)), false, 'hasSelectedEditItem should return false');
  assert.deepEqual(selectedDeleteItems(Immutable.from(state)), ['g2'], 'selectedDeleteItems should not include default Source items');
  assert.equal(hasSelectedDeleteItems(Immutable.from(state)), true, 'hasSelectedDeleteItems should return true');
  assert.deepEqual(selectedPublishItems(Immutable.from(state)), ['g2'], 'selectedPublishItems should have no items');
  assert.equal(hasSelectedPublishItems(Immutable.from(state)), true, 'hasSelectedPublishItems should return true');
});

test('filterTypesConfig selector', function(assert) {
  const expectedConfig = [
    {
      name: 'sourceType',
      label: 'adminUsm.sources.filter.sourceType',
      listOptions: [
        { name: 'edrSource', label: 'adminUsm.sourceTypes.edrSource' },
        { name: 'windowsLogSource', label: 'adminUsm.sourceTypes.windowsLogSource' }
      ],
      type: 'list'
    }
  ];
  const fullState = new ReduxDataHelper()
    .fetchSources()
    .build();
  const config = filterTypesConfig(Immutable.from(fullState));
  assert.equal(config.length, 1, '1 filter as expected');
  assert.deepEqual(config, expectedConfig, 'filter config(s) generated as expected');
});
