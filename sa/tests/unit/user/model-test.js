import { moduleForModel, test } from 'ember-qunit';

moduleForModel('user', 'Unit | Model | user', {
  // Specify the other units that are required for this test.
  needs: []
});

test('it exists', function(assert) {
  let model = this.subject();
  // let store = this.store();
  assert.ok(!!model);
});

test('check model values', function(assert) {
  let myModel = {
    email: 'tony@rsa.com',
    firstName: 'John',
    lastName: 'Doe'
  };

  let model = this.subject(myModel);

  assert.equal(model.get('email'), 'tony@rsa.com', 'Invalid user email');
  assert.equal(model.get('firstName'), 'John', 'Invalid user firstName');
  assert.equal(model.get('lastName'), 'Doe', 'Invalid user lastName');
});