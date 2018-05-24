import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { computed } from '@ember/object';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { settled } from '@ember/test-helpers';
import ReconRoute from 'respond/routes/incident/recon';

let route, redux, transition, hasPermission;

module('Unit | Route | incident.recon', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    transition = null;
    hasPermission = true;
    initialize(this.owner);
    patchReducer(this, Immutable.from({}));
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'incident'
    }));
    const accessControl = Service.extend({
      hasReconAccess: computed(function() {
        return hasPermission;
      })
    }).create();
    redux = this.owner.lookup('service:redux');
    const PatchedRoute = ReconRoute.extend({
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
    route = PatchedRoute.create();
  });

  test('should set selected incident with event type and id', async function(assert) {
    assert.expect(1);

    const param = {
      selection: '123'
    };
    const options = {
      params: {
        'respond.incident': {
          incident_id: 'INC987'
        }
      }
    };

    await route.model(param, options);

    return waitFor(() => {
      const { respond: { incident: { selection } } } = redux.getState();
      const selectionWasSet = selection && selection.type === 'event' && selection.ids[0] === '123';
      if (selectionWasSet) {
        assert.ok(true, 'selection was correctly set during the model hook');
      }
      return selectionWasSet;
    });
  });

  test('should set selected incident with event type and id when mounted engine', async function(assert) {
    assert.expect(1);

    const param = {
      selection: '123'
    };
    const options = {
      params: {
        'protected.respond.incident': {
          incident_id: 'INC987'
        }
      }
    };

    await route.model(param, options);

    return waitFor(() => {
      const { respond: { incident: { selection } } } = redux.getState();
      const selectionWasSet = selection && selection.type === 'event' && selection.ids[0] === '123';
      if (selectionWasSet) {
        assert.ok(true, 'selection was correctly set during the model hook');
      }
      return selectionWasSet;
    });
  });

  test('should redirect to incident detail when user does not have recon permission', async function(assert) {
    assert.expect(1);

    hasPermission = false;

    await route.beforeModel();

    await settled();

    assert.equal(transition, 'incident');
  });

  test('should not redirect to incident detail when user has recon permission', async function(assert) {
    assert.expect(1);

    await route.beforeModel();

    await settled();

    assert.equal(transition, null);
  });

});
