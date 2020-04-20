import Size from 'respond/utils/css/size';
import { module, test } from 'qunit';

module('Unit | Utility | css/size');

test('it parses a numeric value correctly', function(assert) {

  const subject = Size.create({
    value: 100
  });

  assert.equal(subject.get('number'), 100);
  assert.equal(subject.get('units'), 'px');
  assert.notOk(subject.get('auto'));
  assert.equal(subject.get('string'), '100px');
});

test('it parses a percentage correctly', function(assert) {

  const subject = Size.create({
    value: '50%'
  });

  assert.equal(subject.get('number'), 50);
  assert.equal(subject.get('units'), '%');
  assert.notOk(subject.get('auto'));
  assert.equal(subject.get('string'), '50%');
});

test('it parses an empty value correctly', function(assert) {

  const subject = Size.create({
    value: ''
  });

  assert.notOk(subject.get('number'));
  assert.notOk(subject.get('units'));
  assert.ok(subject.get('auto'));
  assert.equal(subject.get('string'), '');
});

test('it parses an null value correctly', function(assert) {

  const subject = Size.create({
    value: null
  });

  assert.notOk(subject.get('number'));
  assert.notOk(subject.get('units'));
  assert.ok(subject.get('auto'), false);
  assert.equal(subject.get('string'), '');
});

test('it parses an "auto" value correctly', function(assert) {

  const subject = Size.create({
    value: 'auto'
  });

  assert.notOk(subject.get('number'));
  assert.notOk(subject.get('units'));
  assert.ok(subject.get('auto'), false);
  assert.equal(subject.get('string'), '');
});

test('it can add values of the same units', function(assert) {

  const subject1 = Size.create({
    value: '50%'
  });

  const subject2 = Size.create({
    value: '25%'
  });

  const subject3 = subject1.add(subject2);
  assert.equal(subject3.get('number'), 75);
  assert.equal(subject3.get('units'), '%');
  assert.notOk(subject3.get('auto'));
  assert.equal(subject3.get('string'), '75%');
});

test('it yields auto when adding values of mixed units', function(assert) {

  const subject1 = Size.create({
    value: '50%'
  });

  const subject2 = Size.create({
    value: '25px'
  });

  const subject3 = subject1.add(subject2);
  assert.ok(subject3.get('auto'));
});

