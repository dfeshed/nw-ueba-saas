import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'rsa-list-manager/actions/types';
import reducer from 'rsa-list-manager/reducers/list-manager/reducer';

module('Unit | Reducers | list-manager');

const listLocation1 = 'listManager';
const listName1 = 'Some List';

test('ACTION_TYPES.INITIALIZE_LIST_MANAGER updates values', function(assert) {
  const prevState = Immutable.from({
    listLocation: undefined,
    listName: undefined,
    highlightedIndex: -1
  });

  const action = {
    type: ACTION_TYPES.INITIALIZE_LIST_MANAGER,
    payload: { listLocation: listLocation1, listName: listName1 }
  };
  const result = reducer(prevState, action);
  assert.equal(result.listLocation, listLocation1, 'listLocation shall be set');
  assert.equal(result.listName, listName1, 'listName shall be set');

});

test('ACTION_TYPES.SET_HIGHLIGHTED_INDEX sets highlightedIndex', function(assert) {
  const prevState = Immutable.from({
    listLocation: undefined,
    highlightedIndex: -1
  });

  const randomIndex = Math.floor(Math.random() * 20) + 1;
  const action = {
    type: ACTION_TYPES.SET_HIGHLIGHTED_INDEX,
    payload: randomIndex
  };

  const result = reducer(prevState, action);
  assert.equal(result.highlightedIndex, randomIndex, 'highlightedIndex shall be set correctly');
});
