import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import IndexRoute from 'investigate-hosts/routes/hosts/index';
import { computed } from '@ember/object';
import HostCreators from 'investigate-hosts/actions/data-creators/host';
import sinon from 'sinon';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let redux;


module('Unit | Route | hosts.index', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'investigate-files'
    }));

    redux = this.owner.lookup('service:redux');

    const PatchedRoute = IndexRoute.extend({
      redux: computed(function() {
        return redux;
      })
    });
    return PatchedRoute.create();
  };

  test('model hook should call bootstrapInvestigateHosts', async function(assert) {
    assert.expect(1);

    const mock = sinon.stub(HostCreators, 'bootstrapInvestigateHosts');

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model({});

    await settled();

    assert.ok(mock.callCount === 1, 'bootstrapInvestigateFiles method is called');
  });


});
