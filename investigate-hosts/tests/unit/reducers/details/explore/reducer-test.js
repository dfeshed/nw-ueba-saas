import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/explore/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';
import { exploreData } from '../../../state/state';

module('Unit | Reducers | explore');

const initialState = Immutable.from({
  fileSearchResults: [],
  searchValue: null,
  searchStatus: null,
  selectedTab: null,
  showSearchResults: false,
  componentName: 'host-detail/header/titlebar/explore/search-field'
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});

test('The START_FILE_SEARCH will reset the previous search result state', function(assert) {
  const previous = Immutable.from({
    showSearchResults: false,
    searchStatus: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.START_FILE_SEARCH });
  assert.equal(result.showSearchResults, true);
  assert.equal(result.searchStatus, 'wait');
});

test('The FILE_SEARCH_END will reset the previous search result state', function(assert) {
  const previous = Immutable.from({
    searchStatus: 'wait'
  });
  const result = reducer(previous, { type: ACTION_TYPES.FILE_SEARCH_END });
  assert.equal(result.searchStatus, 'complete');
});

test('The SELECTED_TAB_DATA will reset the previous search result state', function(assert) {
  const previous = Immutable.from({
    selectedTab: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.SELECTED_TAB_DATA, payload: 'process' });
  assert.equal(result.selectedTab, 'process');
});

test('The RESET_INPUT_DATA will reset the state', function(assert) {
  const previous = Immutable.from({
    searchStatus: 'complete',
    fileSearchResults: [{ name: 'test' }],
    componentName: 'aaa'
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_INPUT_DATA });
  assert.deepEqual(result, initialState);
});

test('The RESET_EXPLORED_RESULTS will sets the selected file hash the state', function(assert) {
  const previous = Immutable.from({
    fileSearchResults: [ { test: '' }],
    searchValue: 'test',
    searchStatus: 'complete'
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_EXPLORED_RESULTS });
  assert.deepEqual(result, initialState);
});

test('The FILE_SEARCH_PAGE sets normalized server response to state', function(assert) {
  const previous = Immutable.from({
    searchStatus: 'wait',
    fileSearchResults: [],
    searchValue: null,
    componentName: null,
    showSearchResults: false
  });
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FILE_SEARCH_PAGE,
    payload: { data: { scanStartTime: '1/1/1', files: exploreData }, meta: { complete: 'complete' }, request: { filter: [ { value: 'test' } ] } }
  });
  const endState = reducer(previous, action);
  assert.equal(endState.fileSearchResults.length, 1);
  assert.equal(endState.searchStatus, 'complete');

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FILE_SEARCH_PAGE,
    payload: { data: { scanStartTime: undefined }, meta: { complete: 'complete' }, request: { filter: [ { value: 'test' } ] } }
  });
  const newEndState = reducer(previous, newAction);
  assert.equal(newEndState.componentName, 'host-detail/header/titlebar/explore/search-label');
});

test('The TOGGLE_EXPLORE_SEARCH_RESULTS will toggles the flag in state', function(assert) {
  const previous = Immutable.from({
    showSearchResults: true
  });
  const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_EXPLORE_SEARCH_RESULTS, payload: { flag: false } });
  assert.equal(result.showSearchResults, false);
});
