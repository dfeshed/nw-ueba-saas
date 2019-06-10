import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../helpers/redux-data-helper';
import {
  isGroupsLoading,
  focusedGroup,
  hasSelectedApplyPoliciesItems,
  selectedEditItem,
  hasSelectedEditItem,
  selectedDeleteItems,
  hasSelectedDeleteItems,
  selectedPublishItems,
  hasSelectedPublishItems,
  filterTypesConfig
} from 'admin-source-management/reducers/usm/groups-selectors';

module('Unit | Selectors | Groups Selectors', function() {

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
    assert.expect(7);
    assert.deepEqual(selectedEditItem(Immutable.from(state)), 'none', 'selectedEditItem should have no items');
    assert.equal(hasSelectedEditItem(Immutable.from(state)), false, 'hasSelectedEditItem should return false');
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
    assert.expect(7);
    assert.deepEqual(selectedEditItem(Immutable.from(state)), 'g1', 'selectedEditItem should have one items');
    assert.equal(hasSelectedEditItem(Immutable.from(state)), true, 'hasSelectedEditItem should return true');
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
    assert.expect(7);
    assert.deepEqual(selectedEditItem(Immutable.from(state)), 'none', 'selectedEditItem should have no items');
    assert.equal(hasSelectedEditItem(Immutable.from(state)), false, 'hasSelectedEditItem should return false');
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
    assert.expect(7);
    assert.deepEqual(selectedEditItem(Immutable.from(state)), 'none', 'selectedEditItem should have no items');
    assert.equal(hasSelectedEditItem(Immutable.from(state)), false, 'hasSelectedEditItem should return false');
    assert.deepEqual(selectedDeleteItems(Immutable.from(state)), ['g1', 'g2', 'g3'], 'selectedDeleteItems should have three items');
    assert.equal(hasSelectedDeleteItems(Immutable.from(state)), true, 'hasSelectedDeleteItems should return true');
    assert.deepEqual(selectedPublishItems(Immutable.from(state)), ['g2'], 'selectedPublishItems should have no items');
    assert.equal(hasSelectedPublishItems(Immutable.from(state)), true, 'hasSelectedPublishItems should return true');
    assert.equal(hasSelectedApplyPoliciesItems(Immutable.from(state)), false, 'hasSelectedApplyPoliciesItems should return false');
  });

  const policyListPayload = [
    {
      id: 'policy_001',
      name: 'Policy 001',
      policyType: 'edrPolicy',
      description: 'EMC 001 of policy policy_001',
      lastPublishedOn: 1527489158739,
      dirty: true,
      defaultPolicy: false
    },
    {
      id: 'policy_002',
      name: 'Policy 002',
      policyType: 'edrPolicy',
      description: 'EMC Reston 002 of policy policy_002',
      lastPublishedOn: 0,
      dirty: true,
      defaultPolicy: false
    },
    {
      id: 'policy_003',
      name: 'Policy 003',
      policyType: 'windowsLogPolicy',
      description: 'EMC Reston 003 of policy policy_003',
      lastPublishedOn: 0,
      dirty: true,
      defaultPolicy: false
    },
    {
      id: 'policy_004',
      name: 'Policy 004',
      policyType: 'windowsLogPolicy',
      description: 'EMC Reston 004 of policy policy_004',
      lastPublishedOn: 0,
      dirty: true,
      defaultPolicy: false
    }
  ];

  test('filterTypesConfig selector', function(assert) {
    const expectedConfig = [
      {
        name: 'sourceType',
        label: 'adminUsm.groups.filter.sourceType',
        listOptions: [
          { name: 'edrPolicy', label: 'adminUsm.policyTypes.edrPolicy' },
          { name: 'windowsLogPolicy', label: 'adminUsm.policyTypes.windowsLogPolicy' },
          { name: 'filePolicy', label: 'adminUsm.policyTypes.filePolicy' }
        ],
        type: 'list'
      },
      {
        'name': 'publishStatus',
        'label': 'adminUsm.groups.list.publishStatus',
        'listOptions': [
          { name: 'published', label: 'adminUsm.publishStatus.published' },
          { name: 'unpublished', label: 'adminUsm.publishStatus.unpublished' },
          { name: 'unpublished_edits', label: 'adminUsm.publishStatus.unpublishedEdits' }
        ],
        type: 'list'
      }
    ];
    const fullState = new ReduxDataHelper()
      .groupsPolicyList(policyListPayload)
      .build();
    const config = filterTypesConfig(Immutable.from(fullState));
    assert.equal(config.length, expectedConfig.length, '2 filters as expected');
  });

});
