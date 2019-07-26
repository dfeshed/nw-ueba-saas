import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import InfoRoute from 'investigate-hosts/routes/hosts/details/tab/info';
import { computed } from '@ember/object';
import Process from 'investigate-hosts/actions/data-creators/process';
import sinon from 'sinon';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let redux;


module('Unit | Route | Hosts | Details | Tab | info', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'hosts.details.tab'
    }));
    redux = this.owner.lookup('service:redux');
    const PatchedRoute = InfoRoute.extend({
      redux: computed(function() {
        return redux;
      }),

      modelFor() {
        return { sid: 1, rowId: 2 };
      }
    });
    return PatchedRoute.create();
  };

  test('model hook should call getProcessDetails', async function(assert) {
    assert.expect(1);

    const mock = sinon.stub(Process, 'getProcessDetails');

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model({ rowId: '1234' });

    await settled();

    assert.ok(mock.callCount === 1, 'getProcessDetails method is called');
  });


});
