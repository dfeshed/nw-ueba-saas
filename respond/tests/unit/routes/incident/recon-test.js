import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { computed } from '@ember/object';
import { settled, waitUntil } from '@ember/test-helpers';
import ReconRoute from 'respond/routes/incident/recon';
import { patchSocket } from '../../../helpers/patch-socket';

let redux, transition, hasPermission;

const selection = '123';
const endpointId = '555d9a6fe4b0d37c827d402e';
const param = {
  selection,
  endpointId
};
const options = {
  params: {
    'protected.respond.incident': {
      incidentId: 'INC987'
    }
  }
};

module('Unit | Route | incident.recon', function(hooks) {
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
    return PatchedRoute.create();
  };

  test('should set selected incident with event type and id', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    const engineOptions = {
      params: {
        'respond.incident': {
          incidentId: 'INC987'
        }
      }
    };

    await route.model(param, engineOptions);

    await waitUntil(() => {
      const { respond: { incident: { selection } } } = redux.getState();
      const selectionWasSet = selection && selection.type === 'event' && selection.ids[0] === '123';
      if (selectionWasSet) {
        assert.ok(true, 'selection was correctly set during the model hook');
      }
      return selectionWasSet;
    }, { timeout: 10000 });
  });

  test('should set selected incident with event type and id when mounted engine', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.model(param, options);

    await waitUntil(() => {
      const { respond: { incident: { selection } } } = redux.getState();
      const selectionWasSet = selection && selection.type === 'event' && selection.ids[0] === '123';
      if (selectionWasSet) {
        assert.ok(true, 'selection was correctly set during the model hook');
      }
      return selectionWasSet;
    }, { timeout: 10000 });
  });

  test('should fetch languages and aliases then cache them', async function(assert) {
    assert.expect(8);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'query');
      assert.equal(modelName, 'core-meta-key');
      assert.deepEqual(query, {
        filter: [{
          field: 'endpointId',
          value: endpointId
        }]
      });
    });

    await route.model(param, options);

    await waitUntil(() => {
      const { respond: { recon: { aliases, language } } } = redux.getState();
      const aliasesAreSetup = aliases && Object.keys(aliases).length === 9;
      const languagesAreSetup = language && language.length === 94;
      if (aliasesAreSetup && languagesAreSetup) {
        assert.ok(true, 'aliases and language were correctly set during the model hook');
      }
      return aliasesAreSetup && languagesAreSetup;
    }, { timeout: 10000 });

    const { respond: { recon: { aliasesCache, languageCache } } } = redux.getState();
    assert.equal(Object.keys(aliasesCache[endpointId]).length, 9);
    assert.equal(languageCache[endpointId].length, 94);
    assert.equal(aliasesCache[endpointId]['eth.type'][0], '802.3');
    assert.equal(languageCache[endpointId][0].metaName, 'time');
  });

  test('should hydrate languages and aliases from cache when available', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({
      respond: {
        recon: {
          aliasesCache: {
            [endpointId]: {
              'udp.srcport': {
                '7': 'echo'
              },
              'eth.type': {
                '0': '802.3'
              }
            }
          },
          languageCache: {
            [endpointId]: [{
              format: 'Text',
              metaName: 'access.point',
              flags: -2147482621,
              displayName: 'Access Point',
              formattedName: 'access.point (Access Point)'
            }]
          }
        }
      }
    }));

    const route = setupRoute.call(this);

    patchSocket((method, modelName) => {
      if (modelName === 'core-meta-key' || modelName === 'core-meta-alias') {
        assert.ok(false, 'should not have fetched languages or aliases from the api');
      }
    });

    await route.model(param, options);

    await waitUntil(() => {
      const { respond: { recon: { aliases, language } } } = redux.getState();
      const aliasesHydrated = aliases && Object.keys(aliases).length === 2;
      const languagesHydrated = language && language.length === 1;
      if (aliasesHydrated && languagesHydrated) {
        assert.ok(true, 'aliases and language were correctly hydrated from cache during the model hook');
      }
      return aliasesHydrated && languagesHydrated;
    });
  });

  test('should redirect to incident detail when user does not have recon permission', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    hasPermission = false;

    await route.beforeModel();

    await settled();

    assert.equal(transition, 'incident');
  });

  test('should not redirect to incident detail when user has recon permission', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);

    await route.beforeModel();

    await settled();

    assert.equal(transition, null);
  });

});
