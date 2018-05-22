import { moduleFor, test } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

moduleFor('route:content/log-parser-rules', 'Unit | Route | log parser rules', {
  needs: ['service:redux'],
  resolver: engineResolverFor('configure')
});

test('it exists', function(assert) {
  const route = this.subject();
  assert.ok(route);
});
