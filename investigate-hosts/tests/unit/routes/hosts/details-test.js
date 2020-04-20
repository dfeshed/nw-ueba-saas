import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { computed } from '@ember/object';
import sinon from 'sinon';
import { settled } from '@ember/test-helpers';

import { patchReducer } from '../../../helpers/vnext-patch';
import InvestigateHosts from 'investigate-hosts/routes/hosts/details';
import endpointServerCreators from 'investigate-shared/actions/data-creators/endpoint-server-creators';

let redux;

module('Unit | Route | hosts.details', function(hooks) {

  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:contextualHelp', Service.extend({}));
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'hosts.details'
    }));

    redux = this.owner.lookup('service:redux');
    const contextualHelp = this.owner.lookup('service:contextualHelp');
    const PatchedRoute = InvestigateHosts.extend({
      redux: computed(function() {
        return redux;
      }),
      contextualHelp: computed(function() {
        return contextualHelp;
      })
    });
    return PatchedRoute.create();
  };

  test('Should call changeEndpointServer', async function(assert) {
    const endpointServerCreatorsMock = sinon.stub(endpointServerCreators, 'changeEndpointServer');

    // setup reducer and route
    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);
    const params = {
      id: 'abcd1234',
      sid: '7723dc',
      machineId: '123',
      tabName: 'OVERVIEW'
    };

    // execute model hook
    route.model(params);
    await settled();

    assert.ok(endpointServerCreatorsMock.callCount === 1, 'bootstrapInvestigateFiles method is called');
    assert.deepEqual(endpointServerCreatorsMock.args[0][0], { 'id': '7723dc' }, 'sid should be set');
  });

});
