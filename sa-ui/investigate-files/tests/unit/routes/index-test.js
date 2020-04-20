import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import IndexRoute from 'investigate-files/routes/index';

let transition;


module('Unit | Route | index', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    transition = null;
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'index'
    }));
    const PatchedRoute = IndexRoute.extend({

      replaceWith(routeName) {
        transition = routeName;
      },
      transitionTo(routeName) {
        transition = routeName;
      }
    });
    return PatchedRoute.create();
  };

  test('should redirect to files route', async function(assert) {
    assert.expect(1);

    const route = setupRoute.call(this);

    await route.beforeModel();

    await settled();

    assert.equal(transition, 'files');
  });


});
