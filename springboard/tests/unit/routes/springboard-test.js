import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { settled } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchSocket } from '../../helpers/patch-socket';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

const resolver = engineResolverFor('springboard');

module('Unit | Route | springboard', function(hooks) {
  setupTest(hooks, { resolver });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('index route exists', async function(assert) {
    assert.expect(1);

    const route = this.owner.lookup('route:springboard');

    await settled();

    assert.ok(route);
  });

  test('model hook will fetch the data', async function(assert) {
    assert.expect(3);
    const route = this.owner.lookup('route:springboard');

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'all');
      assert.equal(modelName, 'springboard');
      assert.deepEqual(query, {});
    });
    await route.model();
  });
});
