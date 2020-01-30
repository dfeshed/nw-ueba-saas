import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { settled } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

const resolver = engineResolverFor('springboard');

module('Unit | Route | springboard | show', function(hooks) {
  setupTest(hooks, { resolver });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('index route exists', async function(assert) {
    assert.expect(1);

    const route = this.owner.lookup('route:springboard.show');

    await settled();

    assert.ok(route);
  });

  test('model hook will set the active springboard', async function(assert) {
    assert.expect(1);

    const route = this.owner.lookup('route:springboard.show');
    const redux = this.owner.lookup('service:redux');

    await route.model({ id: '1' });
    await settled();

    assert.ok(redux.getState().springboard.activeSpringboardId, 1, 'Correct id is set to state');
  });
});
