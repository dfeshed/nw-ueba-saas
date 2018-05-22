import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { computed } from '@ember/object';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import ReconRoute from 'respond/routes/incident/recon';

module('Unit | Route | incident.recon', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    patchReducer(this, Immutable.from({}));
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'incident'
    }));
  });

  test('should set selected incident with event type and id', async function(assert) {
    assert.expect(1);

    const redux = this.owner.lookup('service:redux');
    const PatchedRoute = ReconRoute.extend({
      redux: computed(function() {
        return redux;
      })
    });
    const route = PatchedRoute.create();

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

});
