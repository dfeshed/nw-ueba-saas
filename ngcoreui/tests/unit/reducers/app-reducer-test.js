import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'ngcoreui/reducers/index';
import * as ACTION_TYPES from 'ngcoreui/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';
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

  test('APP_GET_USER stores the username', (assert) => {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.APP_GET_USER,
      payload: {
        params: {
          authType: 'netwitness',
          username: 'admin',
          permissions: 'admin.owner,aggregate,concentrator.manage,connections.manage,database.manage,everyone,index.manage,logs.manage,rules.manage,sdk.content,sdk.manage,sdk.meta,sdk.packets,services.manage,storedproc.execute,storedproc.manage,sys.manage,users.manage',
          groups: 'Administrators',
          queryPrefix: '',
          queryTimeout: '60',
          threshold: '0'
        }
      }
    });
    const state = new ReduxDataHelper()
      .connected()
      .build();

    assert.notOk(state.username);

    const result = reducer(state, action);

    assert.strictEqual(result.username, 'admin');
  });
});
