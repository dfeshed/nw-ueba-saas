import { moduleFor, test } from 'ember-qunit';

moduleFor('controller:protected/investigate/query', 'Unit | Controller | protected/investigate/query', {
});

test('it exists', function(assert) {
  const controller = this.subject();
  assert.ok(controller);
  assert.equal(controller.get('metaPanelSize'), 'default', 'Expected query param metaPanelSize to be "default" initially');
});
