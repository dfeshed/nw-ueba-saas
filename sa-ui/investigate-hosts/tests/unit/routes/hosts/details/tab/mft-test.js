import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import MftRoute from 'investigate-hosts/routes/hosts/details/tab/mft';
import { computed } from '@ember/object';
import HostDetails from 'investigate-hosts/actions/data-creators/host-details';
import sinon from 'sinon';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let redux;


module('Unit | Route | Hosts | Details | Tab | mft', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'hosts.details.tab'
    }));
    redux = this.owner.lookup('service:redux');
    const PatchedRoute = MftRoute.extend({
      redux: computed(function() {
        return redux;
      }),

      modelFor() {
        return { sid: 1, id: 2 };
      }
    });
    return PatchedRoute.create();
  };

  test('model hook should call getMFTDetails', async function(assert) {
    assert.expect(1);

    const mock = sinon.stub(HostDetails, 'getMFTDetails');

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model({ mftName: '_mft', mftFile: '12132' });

    await settled();

    assert.ok(mock.callCount === 1, 'getProcessDetails method is called');
  });


});
