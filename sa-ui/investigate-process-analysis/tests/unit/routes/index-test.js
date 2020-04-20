import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled, waitUntil } from '@ember/test-helpers';
import IndexRoute from 'investigate-process-analysis/routes/index';
import { computed } from '@ember/object';
import { patchReducer } from '../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let transition, redux;


module('Unit | Route | index', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    transition = null;
    initialize(this.owner);
  });

  const setupRoute = function() {

    redux = this.owner.lookup('service:redux');

    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'index'
    }));

    const PatchedRoute = IndexRoute.extend({
      redux: computed(function() {
        return redux;
      }),
      transitionTo(routeName) {
        transition = routeName;
      }
    });
    return PatchedRoute.create();
  };

  test('activate route will adds the class to dom', async function(assert) {
    assert.expect(2);
    patchReducer(this, Immutable.from({
      processAnalysis: {
        processTree: {
          selectedServerId: '1234567'
        }
      }
    }));
    const route = setupRoute.call(this);

    assert.equal(document.querySelectorAll('.process-analysis').length, 0);

    await route.activate();

    await settled();

    assert.equal(document.querySelectorAll('.process-analysis').length, 1);
  });

  test('deactivate route will removes the class from dom', async function(assert) {
    assert.expect(2);

    patchReducer(this, Immutable.from({
      processAnalysis: {
        processTree: {
          selectedServerId: '1234567'
        }
      }
    }));
    const route = setupRoute.call(this);

    await route.activate();

    await settled();

    assert.equal(document.querySelectorAll('.process-analysis').length, 1);

    await route.deactivate();

    await settled();

    assert.equal(document.querySelectorAll('.process-analysis').length, 0);
  });


  test('route model hook will set the process analysis input an retrieves the service list', async function(assert) {
    assert.expect(2);
    patchReducer(this, Immutable.from({
      processAnalysis: {
        processTree: {
          selectedServerId: '1234567'
        }
      }
    }));
    const route = setupRoute.call(this);

    await route.model({ sid: '345789' });

    await waitUntil(() => !redux.getState().processAnalysis.services.isServicesLoading && !redux.getState().processAnalysis.services.isSummaryLoading, { timeout: Infinity });

    await settled();

    assert.equal(redux.getState().processAnalysis.services.serviceData.length, 4);
    assert.equal(redux.getState().processAnalysis.query.serviceId, '345789');

  });


  test('executeQuery action updates the url', async function(assert) {
    assert.expect(2);
    patchReducer(this, Immutable.from({
      processAnalysis: {
        processTree: {
          selectedServerId: '1234567'
        },

        query: {
          serviceId: '123456',
          startTime: '1',
          endTime: '2'
        }
      }
    }));
    const route = setupRoute.call(this);

    route.send('executeQuery');

    await settled();

    assert.equal(transition.queryParams.sid, '123456');
    assert.equal(transition.queryParams.st, '1');
  });

});
