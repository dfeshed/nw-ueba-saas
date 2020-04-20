import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { listData, entityType, errorMessage, isError, isDisabled } from 'context/reducers/list/selectors';

module('Unit | Selectors | Add-to-List');

const state = Immutable.from({
  context: {
    list: {
      isListView: true,
      errorMessage: 'listDuplicateName',
      list: [{
        enabled: true,
        name: 'test'
      }],
      entityType: {
        type: 'IP',
        id: '10.10.10.10'
      }
    }
  }
});

test('get list schema', function(assert) {
  const list = listData(state);
  assert.deepEqual(list, state.context.list.list);
});

test('get isError status when errorMessage is present', function(assert) {
  const errorStatus = isError(state);
  assert.equal(errorStatus, true);
});

test('get isError status when errorMessage is not present', function(assert) {
  const state = Immutable.from({
    context: {
      list: {
        entityType: {
          type: 'IP',
          id: '10.10.10.10'
        }
      }
    }
  });
  const errorStatus = isError(state);
  assert.equal(errorStatus, false);
});


test('get errorMessage for list duplicate', function(assert) {
  const message = errorMessage(state);
  assert.equal(message, 'listDuplicateName');
});

test('get entityType', function(assert) {
  const type = entityType(state);
  assert.equal(type, state.context.list.entityType);
});

test('get isDisabled when errorMessage is listDuplicate', function(assert) {
  const disabledStatus = isDisabled(state);
  assert.equal(disabledStatus, true);
});

test('get isDisabled when errorMessage is context.error', function(assert) {
  const state = Immutable.from({
    context: {
      list: {
        errorMessage: 'context.error',
        entityType: {
          type: 'IP',
          id: '10.10.10.10'
        }
      }
    }
  });
  const disabledStatus = isDisabled(state);
  assert.equal(disabledStatus, false);
});
