import { module, test } from 'qunit';
import { highlightedIndex, isListManagerReady, listName } from 'rsa-list-manager/selectors/list-manager/selectors';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | Selectors | list-manager');

const listLocation1 = 'listManager';
const listName1 = 'Some List';

test('highlightedIndex returns highlightedIndex for listLocation', function(assert) {
  const randomIndex = Math.floor(Math.random() * 20) + 1;
  const state = new ReduxDataHelper().highlightedIndex(randomIndex).build();
  const index = highlightedIndex(state, listLocation1);
  assert.equal(index, randomIndex, 'Shall select highlightedIndex based on listLocation');
});

test('listName returns listName for listLocation', function(assert) {
  const state = new ReduxDataHelper().listName(listName1).build();
  const index = listName(state, listLocation1);
  assert.equal(index, listName1, 'Shall select listName based on listLocation');
});

test('isListManagerReady returns true if listLocation exists', function(assert) {
  const state = new ReduxDataHelper().listLocation(listLocation1).build();
  const isReady = isListManagerReady(state, listLocation1);
  assert.ok(isReady, 'isListManagerReady shall be true if listLocation exists');
});

test('isListManagerReady returns false if listLocation does not exist', function(assert) {
  const state = new ReduxDataHelper().build();
  const isReady = isListManagerReady(state, listLocation1);
  assert.notOk(isReady, 'isListManagerReady shall be false if listLocation does not exist');
});
