import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import TabRoute from 'investigate-hosts/routes/hosts/details/tab';
import { computed } from '@ember/object';
import ExploreCreators from 'investigate-hosts/actions/data-creators/explore';
import HostDetails from 'investigate-hosts/actions/data-creators/host-details';
import sinon from 'sinon';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let redux;


module('Unit | Route | Hosts | Details | tab', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'hosts.details.tab'
    }));
    redux = this.owner.lookup('service:redux');
    const contextualHelp = this.owner.lookup('service:contextualHelp');
    const PatchedRoute = TabRoute.extend({
      redux: computed(function() {
        return redux;
      }),
      contextualHelp: computed(function() {
        return contextualHelp;
      }),
      modelFor() {
        return { sid: 1, id: 2 };
      }
    });
    return PatchedRoute.create();
  };

  test('model hook should call setSelectedTabData if scanTime and checksum', async function(assert) {
    assert.expect(1);

    const mock = sinon.stub(ExploreCreators, 'setSelectedTabData');

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model({ tabName: 'AUTORUNS', scanTime: '123345555', checksum: '123131231313fsdfsdfa' });

    await settled();

    assert.ok(mock.callCount === 1, 'setSelectedTabData method is called');
  });

  test('model hook should call HostDetails', async function(assert) {
    assert.expect(1);

    const mock = sinon.stub(HostDetails, 'setDataForHostTab');

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model({ tabName: 'AUTORUNS', scanTime: null, checksum: null });

    await settled();

    assert.ok(mock.callCount === 1, 'setSelectedTabData method is called');
  });

});
