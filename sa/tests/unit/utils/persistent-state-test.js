import { module, test } from 'qunit';
import Ember from 'ember';
import State from 'sa/utils/persistent-state';

const {
  Object: EmberObject
} = Ember;

module('Unit | Utility | persistent-state', {
  beforeEach() {
    this.state = State.create();
  }
});

test('empty object should not be "replayable"', function(assert) {
  assert.expect(1);
  this.state.set('value', {});
  assert.notOk(this.state.get('isReplayable'), 'after init the stored empty object should not be replayable');
});

test('should throw Ember Error when storage property is not configured before invoking persist', function(assert) {
  assert.expect(1);
  const emberError = new Error('Attempted to persist without configuring storage. Cannot persist.');
  assert.throws(function() {
    this.state.persist('columns.assignee.visible', true);
  }, emberError, 'should throw Ember.Error');
});

test('should add new node having boolean value', function(assert) {
  assert.expect(1);
  const StorageStub = EmberObject.extend({
    restore() {
      return {};
    },
    store(restoredState) {
      this.assert.equal(restoredState.columns.assignee.visible, false, 'visibility of assignee column should be false');
    }
  });
  const storage = StorageStub.create({ assert });
  this.state.set('storage', storage);
  this.state.persist('columns.assignee.visible', false);
});

test('should add new node having array as value', function(assert) {
  assert.expect(1);
  const StorageStub = EmberObject.extend({
    restore() {
      return {};
    },
    store(restoredState) {
      this.assert.deepEqual(restoredState.filters.priority, [3, 2], 'should add priority filter with ids 3, 2');
    }
  });
  const storage = StorageStub.create({ assert });
  this.state.set('storage', storage);
  this.state.persist('filters.priority', [3, 2]);
});

test('should update already persisted node', function(assert) {
  // assert.expect(2);
  const restoredState = {
    columns: {
      id: {
        visible: true
      },
      assigneeFirstLastName: {
        visible: false
      }
    },
    filters: {
      priority: [10, 11, 12],
      status: [3],
      assigneeId: ['-1', '3']
    }
  };
  const StorageStub = EmberObject.extend({
    restore() {
      this.assert.deepEqual(restoredState.filters.priority, [10, 11, 12], 'before update, priority filter with ids 10, 11, 12 were persisted');
      return restoredState;
    },
    store(restoredState) {
      this.assert.deepEqual(restoredState.filters.priority, [3, 2], 'after update, priority filter with ids 3, 2 will be persisted');
    }
  });
  const storage = StorageStub.create({ assert });
  this.state.set('storage', storage);
  this.state.persist('filters.priority', [3, 2]);
});
