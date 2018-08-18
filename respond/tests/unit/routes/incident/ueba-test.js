import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import EmberObject, { get, computed } from '@ember/object';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { settled } from '@ember/test-helpers';
import UebaRoute from 'respond/routes/incident/ueba';

let redux, transition, hasPermission;

const selection = '123';
const ueba = '/user/123/alert/456';
const param = {
  ueba,
  selection
};
const options = {
  params: {
    'protected.respond.incident': {
      incident_id: 'INC987'
    }
  }
};

module('Unit | Route | incident.ueba', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    transition = null;
    hasPermission = true;
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'incident'
    }));
    const accessControl = Service.extend({
      hasUEBAAccess: computed(function() {
        return hasPermission;
      })
    }).create();
    redux = this.owner.lookup('service:redux');
    const PatchedRoute = UebaRoute.extend({
      accessControl: computed(function() {
        return accessControl;
      }),
      redux: computed(function() {
        return redux;
      }),
      transitionTo(routeName) {
        transition = routeName;
      }
    });
    return PatchedRoute.create();
  };

  test('should set selected incident with storyPoint type and id', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    const engineOptions = {
      params: {
        'respond.incident': {
          incident_id: 'INC987'
        }
      }
    };

    await route.model(param, engineOptions);

    return waitFor(() => {
      const { respond: { incident: { selection } } } = redux.getState();
      const selectionWasSet = selection && selection.type === 'storyPoint' && selection.ids[0] === '123';
      if (selectionWasSet) {
        assert.ok(true, 'selection was correctly set during the model hook');
      }
      return selectionWasSet;
    });
  });

  test('should set selected incident with storyPoint type and id when mounted engine', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model(param, options);

    return waitFor(() => {
      const { respond: { incident: { selection } } } = redux.getState();
      const selectionWasSet = selection && selection.type === 'storyPoint' && selection.ids[0] === '123';
      if (selectionWasSet) {
        assert.ok(true, 'selection was correctly set during the model hook');
      }
      return selectionWasSet;
    });
  });

  test('should push ueba queryParam to the controller', async function(assert) {
    assert.expect(1);

    const route = setupRoute.call(this);
    const controller = EmberObject.create();

    route.setupController(controller, route.model(param, options));

    assert.equal(get(controller, 'ueba'), ueba);
  });

  test('should redirect to incident detail when user does not have ueba permission', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    hasPermission = false;

    await route.beforeModel();

    await settled();

    assert.equal(transition, 'incident');
  });

  test('should not redirect to incident detail when user has ueba permission', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.beforeModel();

    await settled();

    assert.equal(transition, null);
  });

});
