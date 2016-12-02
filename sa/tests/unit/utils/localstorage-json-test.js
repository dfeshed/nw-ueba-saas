import Ember from 'ember';
import { module, test } from 'qunit';
import Storage from 'sa/utils/localstorage-json';

const {
  Error
} = Ember;

module('Unit | Utility | localstorage-json', {
  beforeEach() {
    localStorage.clear();
    this.key = 'testKey';
    this.storage = Storage.create({ key: this.key });
  }
});

test('should create a key with empty object as value on initialization', function(assert) {
  assert.expect(1);
  const restoredState = this.storage.restore(this.key);
  assert.equal(Object.keys(restoredState).length, 0);
});

test('should not have the key the storage when storage is cleared', function(assert) {
  assert.expect(1);
  localStorage.clear();
  assert.notOk(this.storage.exists());
});

test('should be able to "store" a native object as JSON string', function(assert) {
  assert.expect(1);
  const obj = { 'filters': { 'priority': { 'selected': [3, 2] }, 'status': { 'selected': [0, 1, 3] } }, 'fields': { 'assignee': { 'visible': false } } };
  this.storage.store(obj);
  const jsonString = localStorage[this.key];
  assert.equal(jsonString, '{"filters":{"priority":{"selected":[3,2]},"status":{"selected":[0,1,3]}},"fields":{"assignee":{"visible":false}}}');
});

test('should be able to "restore" a native object out of stored json string', function(assert) {
  assert.expect(4);
  const jsonString = '{"filters":{"priority":{"selected":[3,2]},"status":{"selected":[0,1,3]}},"fields":{"assignee":{"visible":false}}}';
  localStorage[this.key] = jsonString;
  const restoredState = this.storage.restore();
  assert.equal(Object.keys(restoredState).length, 2, 'should be restored with fields and filters as top level keys');
  assert.equal(restoredState.fields.assignee.visible, false, 'visibility of assignee should be false');
  assert.equal(restoredState.filters.priority.selected.length, 2, '2 of the priority filter should be selected');
  assert.equal(restoredState.filters.status.selected.length, 3, '3 of status filters should be selected');
});

test('should return an empty object when state cannot be restored and throwExceptionOnRestore is false (by default)', function(assert) {
  assert.expect(1);
  // invalid json
  const jsonString = '{"filters":{"priority":{"selected":[3,2]';
  // store invalid json
  localStorage[this.key] = jsonString;
  const restoredState = this.storage.restore();
  assert.deepEqual(restoredState, {});
});

test('should throw Ember Error when state cannot be restored and throwExceptionOnRestore is true', function(assert) {
  assert.expect(1);
  this.storage.set('throwExceptionOnRestore', true);
  const emberError = new Error('State could not be restored.');
  // invalid json
  const jsonString = '{"filters":{"priority":{"selected":[3,2]';
  // store invalid json
  localStorage[this.key] = jsonString;
  assert.throws(function() {
    this.storage.restore();
  }, emberError, 'should throw Ember.Error');
});
