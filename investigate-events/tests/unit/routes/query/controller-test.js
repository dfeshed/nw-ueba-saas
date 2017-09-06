import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleFor('controller:query', 'Unit | Controller | query', {
  resolver: engineResolverFor('investigate-events')
});

test('it exists', function(assert) {
  const controller = this.subject();
  assert.ok(controller);
  assert.equal(controller.get('metaPanelSize'), 'default', 'Expected query param metaPanelSize to be "default" initially');
});
