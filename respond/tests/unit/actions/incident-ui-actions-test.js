import Ember from 'ember';
import { module, test } from 'qunit';
import incidentReducer from 'respond/reducers/respond/incident';
import ACTION_TYPES from 'respond/actions/types';

const { copy } = Ember;

const initialState = {
  viewMode: 'overview',
  isJournalPanelOpen: false
};

module('Unit | Utility | Incident UI Actions - Reducers');

test('The TOGGLE_JOURNAL_PANEL action toggles the app state property', function(assert) {
  const initState = copy(initialState);
  const isJournalPanelOpen = true;
  const expectedEndState = {
    ...initState,
    isJournalPanelOpen
  };

  const endState = incidentReducer(initState, {
    type: ACTION_TYPES.TOGGLE_JOURNAL_PANEL
  });

  assert.deepEqual(endState, expectedEndState);
});

test('The SET_VIEW_MODE action properly modifies the viewMode app state property', function(assert) {
  const initState = copy(initialState);
  const viewMode = 'storyline';
  const expectedEndState = {
    ...initState,
    viewMode
  };

  const endState = incidentReducer(initState, {
    type: ACTION_TYPES.SET_VIEW_MODE,
    payload: viewMode
  });

  assert.deepEqual(endState, expectedEndState);
});