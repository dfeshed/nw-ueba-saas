import EmberObject from '@ember/object';
import CanThrottleAttr from 'respond/mixins/can-throttle-attr';
import { module, test } from 'qunit';
import wait from 'ember-test-helpers/wait';

module('Unit | Mixin | can throttle attr');

const MockClass = EmberObject.extend(CanThrottleAttr);

const throttleInterval = 1000;

const subject = MockClass.create({
  throttleFromAttr: 'foo',
  throttleToAttr: 'bar',
  throttleInterval,
  foo: 'initialValue'
});

subject.didInsertElement();

test('it initializes the to attr without any delay', function(assert) {
  assert.equal(subject.get('foo'), subject.get('bar'));
});

test('it copies values from one attr to another with throttle', function(assert) {
  assert.expect(2);
  subject.set('foo', 'value2');
  assert.notEqual(subject.get('bar'), subject.get('foo'), 'Expected synchronization to wait for a delay');
  subject.set('foo', 'value3');
  subject.set('foo', 'value4');
  return wait()
    .then(() => {
      assert.equal(subject.get('foo'), subject.get('bar'), 'Expected synchronization to occur after delay is over');
    });
});

const subject2 = MockClass.create({
  throttleFromAttr: 'foo',
  throttleToAttr: 'bar',
  throttleInterval: 0,
  foo: 'initialValue'
});

subject2.didInsertElement();

test('when throttleInterval is zero, it copies values immediately', function(assert) {
  subject2.set('foo', 'value2');
  assert.equal(subject.get('foo'), subject.get('bar'), 'Expected synchronization to occur immediately');
  subject2.set('foo', 'value3');
  subject2.set('foo', 'value4');
  assert.equal(subject.get('foo'), subject.get('bar'), 'Expected synchronization to occur immediately');
});

const shouldNotChange = 'should not change';

const subject3 = MockClass.create({
  throttleFromAttr: '',
  throttleToAttr: 'bar',
  throttleInterval: 0,
  foo: 'initialValue',
  bar: shouldNotChange
});

subject3.didInsertElement();

test('when from attr is empty, to attr is not modified', function(assert) {
  assert.expect(2);
  subject3.set('foo', 'value2');
  assert.equal(subject3.get('bar'), shouldNotChange);
  return wait()
    .then(() => {
      assert.equal(subject3.get('bar'), shouldNotChange);
    });
});

