import { module, test } from 'qunit';
import Service from '@ember/service';
import { run, later } from '@ember/runloop';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { computed } from '@ember/object';
import { settled } from '@ember/test-helpers';
import sinon from 'sinon';

import { patchReducer } from '../../helpers/vnext-patch';
import InvestigateHosts from 'investigate-hosts/routes/hosts';
import endpointServerCreators from 'investigate-shared/actions/data-creators/endpoint-server-creators';

let redux;

module('Unit | Route | investigate-hosts.hosts', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'investigate-hosts'
    }));
    redux = this.owner.lookup('service:redux');
    const PatchedRoute = InvestigateHosts.extend({
      redux: computed(function() {
        return redux;
      })
    });
    return PatchedRoute.create();
  };

  test('should call setSelectedEndpointServer', async function(assert) {
    run(() => {
      const endpointServerCreatorsMock = sinon.stub(endpointServerCreators, 'setSelectedEndpointServer');

      // setup reducer and route
      patchReducer(this, Immutable.from({}));
      const route = setupRoute.call(this);

      const params = {
        sid: '7723dc',
        machineId: '123',
        tabName: 'OVERVIEW'
      };

      // execute model hook
      route.model(params);

      settled();
      later(() => {
        assert.equal(endpointServerCreatorsMock.callCount, 1, 'should call setSelectedEndpointServer');
        assert.deepEqual(endpointServerCreatorsMock.args[0][0], params.sid, 'sid should be set');
      }, 10000);
    });
  });
});
