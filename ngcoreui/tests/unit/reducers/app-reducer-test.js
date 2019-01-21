import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'ngcoreui/reducers/index';
import * as ACTION_TYPES from 'ngcoreui/actions/types';
import ReduxDataHelper from '../../helpers/redux-data-helper';

module('Unit | Reducers | App', (hooks) => {

  setupTest(hooks);

  test('APP_CHANGE_ACTIVE_TAB sets the active tab', (assert) => {
    const action = {
      type: ACTION_TYPES.APP_CHANGE_ACTIVE_TAB,
      payload: 'tree'
    };
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .build();

    assert.notOk(state.activeTab);

    const result = reducer(state, action);

    assert.strictEqual(result.activeTab, 'tree');
  });
});
