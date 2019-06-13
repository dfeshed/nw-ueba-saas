import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import reducer from 'recon/reducers/header/reducer';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'recon/actions/types';

module('Unit | Reducer | header', function(hooks) {
  setupTest(hooks);

  test('SUMMARY_RETRIEVE start', function(assert) {
    const action = makePackAction(LIFECYCLE.START, {
      type: ACTION_TYPES.SUMMARY_RETRIEVE
    });

    const result = reducer(Immutable.from({
      headerError: null,
      headerErrorCode: null,
      headerItems: null,
      headerLoading: null
    }), action);

    assert.equal(result.headerLoading, false); // via always
    assert.equal(result.headerError, true); // via always
    assert.equal(result.headerItems, null);
    assert.equal(result.headerErrorCode, null);
  });

  test('SUMMARY_RETRIEVE always when headerLoading is true', function(assert) {
    const action = makePackAction('always', {
      type: ACTION_TYPES.SUMMARY_RETRIEVE
    });

    const result = reducer(Immutable.from({
      headerError: null,
      headerErrorCode: null,
      headerItems: null,
      headerLoading: true
    }), action);

    assert.equal(result.headerLoading, false);
    assert.equal(result.headerErrorCode, null);
    assert.equal(result.headerItems, null);
    assert.equal(result.headerError, true);
  });

  test('SUMMARY_RETRIEVE always when headerLoading is false', function(assert) {
    const action = makePackAction('always', {
      type: ACTION_TYPES.SUMMARY_RETRIEVE
    });

    const result = reducer(Immutable.from({
      headerError: null,
      headerErrorCode: null,
      headerItems: null,
      headerLoading: false
    }), action);

    assert.equal(result.headerLoading, false);
    assert.equal(result.headerErrorCode, null);
    assert.equal(result.headerItems, null);
    assert.equal(result.headerError, null);
  });

  test('SUMMARY_RETRIEVE failure', function(assert) {
    const action = makePackAction(LIFECYCLE.FAILURE, {
      type: ACTION_TYPES.SUMMARY_RETRIEVE,
      payload: {
        code: 110
      }
    });

    const result = reducer(Immutable.from({
      headerError: null,
      headerErrorCode: null,
      headerItems: null,
      headerLoading: true
    }), action);

    assert.equal(result.headerLoading, false);
    assert.equal(result.headerErrorCode, 110);
    assert.equal(result.headerItems, null);
    assert.equal(result.headerError, true);
  });

  test('SUMMARY_RETRIEVE success', function(assert) {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SUMMARY_RETRIEVE,
      payload: {
        headerItems: [{ name: 'foo' }]
      }
    });

    const result = reducer(Immutable.from({
      headerError: null,
      headerErrorCode: null,
      headerItems: null,
      headerLoading: true
    }), action);

    assert.equal(result.headerLoading, false);
    assert.equal(result.headerErrorCode, null);
    assert.equal(result.headerItems[0].name, 'foo');
    assert.equal(result.headerError, false);
  });
});
