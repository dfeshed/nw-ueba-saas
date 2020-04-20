import EmberObject from '@ember/object';
import HasSelectionsMixin from 'respond/mixins/group-table/has-selections';
import { module, test } from 'qunit';

module('Unit | Mixin | group table/has selections');

const HasSelectionsMixinObject = EmberObject.extend(HasSelectionsMixin);

const value1 = 'foo';
const value2 = 'bar';
const value3 = 'baz';
const selections = { ids: [ value1, value2 ] };

test('it computes selectionsHash correctly', function(assert) {
  const subject = HasSelectionsMixinObject.create({
    selections
  });

  assert.ok(subject);

  let hash = subject.get('selectionsHash');
  assert.ok(hash);
  assert.ok(hash[value1]);
  assert.ok(hash[value2]);
  assert.equal(Object.keys(hash).length, selections.ids.length);

  selections.ids.pushObject(value3);
  hash = subject.get('selectionsHash');

  assert.ok(hash[value3]);
  assert.equal(Object.keys(hash).length, selections.ids.length);

  selections.ids.removeObject(value1);
  hash = subject.get('selectionsHash');

  assert.notOk(hash[value1]);
  assert.equal(Object.keys(hash).length, selections.ids.length);
});
