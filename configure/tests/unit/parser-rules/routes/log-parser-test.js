import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleFor('route:content/log-parser', 'Unit | Route | log parser', {
  needs: ['service:redux'],
  resolver: engineResolverFor('configure')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
