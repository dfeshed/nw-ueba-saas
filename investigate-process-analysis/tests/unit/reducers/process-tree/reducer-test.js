import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-process-analysis/reducers/process-tree/reducer';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';

const initialState = Immutable.from({
  queryInput: null,
  streaming: false,
  rawData: null,
  error: null,
  selectedProcess: null,
  path: [ '0' ]
});

module('Unit | Reducers | process-tree', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });

  test('INIT_EVENTS_STREAMING reset the values', function(assert) {
    const previous = Immutable.from({
      streaming: false,
      error: 'XYZ'
    });
    const result = reducer(previous, { type: ACTION_TYPES.INIT_EVENTS_STREAMING });
    assert.equal(result.error, null, 'error is set to null');
    assert.equal(result.streaming, true, 'streaming is set to false');
  });

  test('SET_EVENTS_PAGE_ERROR set the error message to state', function(assert) {
    const previous = Immutable.from({
      streaming: false,
      error: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_EVENTS_PAGE_ERROR, payload: { error: 'xyz' } });
    assert.equal(result.error, 'xyz', 'error message is set to state');
    assert.equal(result.streaming, false, 'streaming is set to false');
  });

  test('SET_EVENTS set the events to state', function(assert) {
    const previous = Immutable.from({
      rawData: [],
      error: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_EVENTS, payload: [ {}, {}, {}] });
    assert.equal(result.rawData.length, 3);
  });

  test('SET_PROCESS_ANALYSIS_INPUT query input to state', function(assert) {
    const previous = Immutable.from({
      queryInput: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_PROCESS_ANALYSIS_INPUT, payload: { pn: 'test', aid: 1 } });
    assert.equal(result.queryInput.pn, 'test');
  });

  test('COMPLETED_EVENTS_STREAMING will sets streaming', function(assert) {
    const previous = Immutable.from({
      rawData: [],
      queryInput: {
        pn: 'test',
        aid: 1,
        checkSum: 'xyz'
      },
      streaming: true,
      error: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.COMPLETED_EVENTS_STREAMING });
    assert.equal(result.streaming, false, 'streaming is set to false');
  });

  test('SET_EVENTS_COUNT will sets event count', function(assert) {
    const previous = Immutable.from({
      rawData: [
        {
          processName: 'test',
          childCount: 0,
          processId: 1
        }
      ],
      queryInput: {
        pn: 'test',
        aid: 1,
        checkSum: 'xyz'
      },
      streaming: true,
      error: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_EVENTS_COUNT, payload: { 1: { data: 1 } } });
    assert.equal(result.rawData[0].childCount, 1);
  });

  test('SET_SELECTED_PROCESS sets selected process', function(assert) {
    const previous = Immutable.from({
      selectedProcess: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_PROCESS, payload: '1' });
    assert.equal(result.selectedProcess, '1');
  });

  test('SET_SELECTED_EVENTS will sets eventsData', function(assert) {
    const previous = Immutable.from({
      eventsData: []
    });
    const eventsData = [
      {
        sessionId: 45328,
        time: 1525950159000
      },
      {
        sessionId: 45337,
        time: 1525950159000
      }];
    const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_EVENTS, payload: eventsData });
    assert.equal(result.eventsData.length, 2);
  });
});
