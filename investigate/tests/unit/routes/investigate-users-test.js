import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { settled } from '@ember/test-helpers';
import EmberObject, { get, computed } from '@ember/object';
import InvestigateUsersRoute from 'investigate/routes/investigate-users';

let hasPermission, transition;

module('Unit | Route | investigate-users', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    transition = null;
    hasPermission = true;
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'investigate-users'
    }));
  });

  const setupRoute = function() {
    const accessControl = Service.extend({
      hasUEBAAccess: computed(function() {
        return hasPermission;
      })
    }).create();
    const PatchedRoute = InvestigateUsersRoute.extend({
      accessControl: computed(function() {
        return accessControl;
      }),
      transitionToExternal(routeName) {
        transition = routeName;
      }
    });
    return PatchedRoute.create();
  };

  test('without permissions the user will transition to investigate events', async function(assert) {
    assert.expect(1);

    const route = setupRoute.call(this);

    hasPermission = false;

    await route.beforeModel();

    await settled();

    assert.equal(transition, 'investigate.investigate-events');
  });

  test('with permissions the user will not transition to investigate events', async function(assert) {
    assert.expect(1);

    const route = setupRoute.call(this);

    hasPermission = true;

    await route.beforeModel();

    await settled();

    assert.equal(transition, null);
  });

  test('should push ueba queryParam to the controller', async function(assert) {
    assert.expect(1);

    const ueba = 'user/689d0bb1-a5e4-4af0-8d2c-98aa02a8ac9b/alert/fefe1f9e-2cf9-491d-bfc3-c37f61dcc4d9';
    const route = setupRoute.call(this);
    const controller = EmberObject.create();
    const queryParams = {
      ueba
    };

    route.setupController(controller, route.model(queryParams));

    assert.equal(get(controller, 'ueba'), ueba);
  });
});
