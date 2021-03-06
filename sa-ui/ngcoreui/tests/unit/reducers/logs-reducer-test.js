import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'ngcoreui/reducers/shared-reducers';
import * as ACTION_TYPES from 'ngcoreui/actions/types';
import ReduxDataHelper from '../../helpers/redux-data-helper';

module('Unit | Reducers | Logs', (hooks) => {

  setupTest(hooks);

  test('LOGS_LOAD_START sets logsLoading to true and resets logs to an empty array', (assert) => {
    const action = {
      type: ACTION_TYPES.LOGS_LOAD_START
    };
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .logs(['foo', 'bar'])
      .build().shared;

    const result = reducer(state, action);

    assert.deepEqual(result.logs, []);
    assert.strictEqual(result.logsLoading, true);
  });

  test('LOGS_LOAD_FINISH sets logsLoading to false', (assert) => {
    const action = {
      type: ACTION_TYPES.LOGS_LOAD_FINISH
    };
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .logsLoading(true)
      .build().shared;

    const result = reducer(state, action);

    assert.strictEqual(result.logsLoading, false);
  });

  test('LOGS_FILTER_CHANGE sets logsFilterChangePending to true', (assert) => {
    const action = {
      type: ACTION_TYPES.LOGS_FILTER_CHANGE
    };
    const state = new ReduxDataHelper()
      .connected()
      .build().shared;

    assert.strictEqual(state.logsFilterChangePending, false);

    const result = reducer(state, action);

    assert.strictEqual(result.logsFilterChangePending, true);
  });

  test('LOGS_FILTER_CHANGE sets logsFilterChangePending to true and doesn\'t error when already true', (assert) => {
    const action = {
      type: ACTION_TYPES.LOGS_FILTER_CHANGE
    };
    const state = new ReduxDataHelper()
      .connected()
      .logsFilterChangePending(true)
      .build().shared;

    assert.strictEqual(state.logsFilterChangePending, true);

    const result = reducer(state, action);

    assert.strictEqual(result.logsFilterChangePending, true);
  });

  test('LOGS_FILTER_CHANGE_DONE sets logsFilterChangePending to false', (assert) => {
    const action = {
      type: ACTION_TYPES.LOGS_FILTER_CHANGE_DONE
    };
    const state = new ReduxDataHelper()
      .connected()
      .logsFilterChangePending(true)
      .build().shared;

    assert.strictEqual(state.logsFilterChangePending, true);

    const result = reducer(state, action);

    assert.strictEqual(result.logsFilterChangePending, false);
  });

  test('LOGS_ADD_NEW adds logs to state and removes overlap', (assert) => {
    const action1 = {
      type: ACTION_TYPES.LOGS_ADD_NEW,
      payload: [
        { id: 1, msg: 'foo' },
        { id: 2, msg: 'bar' }
      ]
    };
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .logs([])
      .build().shared;

    const result1 = reducer(state, action1);

    assert.deepEqual(result1.logs, [
      { id: 1, msg: 'foo' },
      { id: 2, msg: 'bar' }
    ]);

    const action2 = {
      type: ACTION_TYPES.LOGS_ADD_NEW,
      payload: [
        { id: 2, msg: 'bar' },
        { id: 3, msg: 'baz' }
      ]
    };

    const result2 = reducer(result1, action2);

    assert.deepEqual(result2.logs, [
      { id: 1, msg: 'foo' },
      { id: 2, msg: 'bar' },
      { id: 3, msg: 'baz' }
    ]);
  });

  test('LOGS_UPDATE adds logs but allows a finite amount in state', (assert) => {
    const action1 = {
      type: ACTION_TYPES.LOGS_UPDATE,
      payload: {
        logs: [
          { id: 1, msg: 'foo' },
          { id: 2, msg: 'bar' }
        ],
        count: 3
      }
    };
    const state = new ReduxDataHelper()
      .connected()
      .treePathContentsStandard()
      .logs([])
      .build().shared;

    const result1 = reducer(state, action1);

    assert.deepEqual(result1.logs, [
      { id: 1, msg: 'foo' },
      { id: 2, msg: 'bar' }
    ]);

    const action2 = {
      type: ACTION_TYPES.LOGS_UPDATE,
      payload: {
        logs: [
          { id: 3, msg: 'baz' },
          { id: 4, msg: 'fred' }
        ],
        count: 3
      }
    };

    const result2 = reducer(result1, action2);

    assert.deepEqual(result2.logs, [
      { id: 2, msg: 'bar' },
      { id: 3, msg: 'baz' },
      { id: 4, msg: 'fred' }
    ]);
  });
});
