import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-process-analysis/reducers/services/reducer';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

const initialState = Immutable.from({
  serviceData: undefined,
  isServicesLoading: undefined,
  isServicesRetrieveError: undefined,
  summaryData: undefined,
  isSummaryRetrieveError: false,
  summaryErrorMessage: undefined,
  isSummaryLoading: false
});

module('Unit | Reducers | Services', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });

  test('SERVICES_RETRIEVE set the selected service', function(assert) {
    const previous = Immutable.from({
      serviceData: undefined,
      isServicesLoading: undefined
    });

    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SERVICES_RETRIEVE });
    const endState = reducer(previous, startAction);

    assert.equal(endState.isServicesLoading, true);

    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SERVICES_RETRIEVE,
      payload: { data: [{ id: 'test', displayName: 'test', name: 'test', version: '11.1.0.0' }] }
    });
    const newEndState = reducer(previous, successAction);
    assert.equal(newEndState.serviceData.length, 1);
    assert.equal(newEndState.isServicesLoading, false);
  });


  test('SERVICES_RETRIEVE sets Error', function(assert) {
    const previous = Immutable.from({
      serviceData: undefined,
      isServicesLoading: undefined,
      isServicesRetrieveError: undefined
    });

    const startAction = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.SERVICES_RETRIEVE });
    const endState = reducer(previous, startAction);

    assert.equal(endState.isServicesRetrieveError, true);
  });


  test('SUMMARY_RETRIEVE set the selected service', function(assert) {
    const previous = Immutable.from({
      summaryData: undefined,
      isSummaryLoading: undefined
    });

    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.SUMMARY_RETRIEVE });
    const endState = reducer(previous, startAction);

    assert.equal(endState.isSummaryLoading, true);

    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SUMMARY_RETRIEVE,
      payload: { data: { startTime: 1 } }
    });
    const newEndState = reducer(previous, successAction);
    assert.equal(newEndState.summaryData.startTime, 1);
    assert.equal(newEndState.isSummaryLoading, false);
  });

  test('SUMMARY_RETRIEVE sets the error', function(assert) {
    const previous = Immutable.from({
      summaryData: undefined,
      isSummaryLoading: undefined,
      isSummaryRetrieveError: undefined
    });

    const startAction = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.SUMMARY_RETRIEVE, payload: { meta: 'test' } });
    const endState = reducer(previous, startAction);

    assert.equal(endState.isSummaryRetrieveError, true);


  });
});
