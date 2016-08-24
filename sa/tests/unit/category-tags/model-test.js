import { moduleForModel, test } from 'ember-qunit';

moduleForModel('category-tags', 'Unit | Model | category tags', {
  // Specify the other units that are required for this test.
  needs: []
});

test('it exists', function(assert) {
  let model = this.subject();
  assert.ok(!!model);
});

test('check model values', function(assert) {

  let myModel = {
    parent: 'ParentCategory',
    name: 'ChildCategory',
    id: '562aae59e4b03ae1affcc511'
  };

  let model = this.subject(myModel);

  assert.equal(model.get('parent'), 'ParentCategory', 'Testing to see that a valid parent category value is returned.');
  assert.equal(model.get('name'), 'ChildCategory', 'Testing to see that a valid child category value is returned.');
  assert.equal(model.get('id'), '562aae59e4b03ae1affcc511', 'Testing to see that a valid id value is returned.');
});
