import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';
import reducer from 'recon/reducers/meta/reducer';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'recon/actions/types';

module('Unit | Reducer | meta', function(hooks) {
  setupTest(hooks);

  test('META_RETRIEVE start', function(assert) {
    const action = makePackAction(LIFECYCLE.START, {
      type: ACTION_TYPES.META_RETRIEVE
    });

    const result = reducer(Immutable.from({
      metaLoading: false
    }), action);

    assert.equal(result.meta, null);
    assert.equal(result.metaError, null);
    assert.equal(result.metaLoading, true);
  });

  test('META_RETRIEVE success', function(assert) {
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.META_RETRIEVE,
      payload: 'foo'
    });

    const result = reducer(Immutable.from({
      meta: null,
      metaError: null,
      metaLoading: true
    }), action);

    assert.equal(result.meta, 'foo');
    assert.equal(result.metaError, null);
    assert.equal(result.metaLoading, false);
  });

  test('META_RETRIEVE failure', function(assert) {
    const action = makePackAction(LIFECYCLE.FAILURE, {
      type: ACTION_TYPES.META_RETRIEVE,
      payload: 'foo'
    });

    const result = reducer(Immutable.from({
      meta: null,
      metaError: null,
      metaLoading: true
    }), action);

    assert.equal(result.meta, null);
    assert.equal(result.metaError, 'foo');
    assert.equal(result.metaLoading, false);
  });
});
