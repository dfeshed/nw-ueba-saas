import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { computed } from '@ember/object';
import { settled, waitUntil } from '@ember/test-helpers';
import IncidentsRoute from 'respond/routes/incidents';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';

const timeout = 10000;

let redux, transition, hasPermission;

module('Unit | Route | incidents', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    transition = null;
    hasPermission = true;
    initialize(this.owner);
    patchReducer(this, Immutable.from({}));
    redux = this.owner.lookup('service:redux');
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'incidents'
    }));
    const accessControl = Service.extend({
      hasRespondIncidentsAccess: computed(function() {
        return hasPermission;
      })
    }).create();
    const contextualHelp = this.owner.lookup('service:contextualHelp');
    const i18n = this.owner.lookup('service:i18n');
    const PatchedRoute = IncidentsRoute.extend({
      i18n: computed(function() {
        return i18n;
      }),
      contextualHelp: computed(function() {
        return contextualHelp;
      }),
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

  test('it resolves the proper title token for the route', async function(assert) {
    assert.expect(1);
    const route = setupRoute.call(this);
    assert.equal(route.titleToken(), 'Incidents');
  });

  test('it transitions to "index" if the user does not have access', async function(assert) {
    assert.expect(1);
    const route = setupRoute.call(this);
    hasPermission = false;

    await route.beforeModel();
    await settled();
    assert.equal(transition, 'index');
  });

  test('does not transition to "index" if the user has access', async function(assert) {
    assert.expect(1);
    const route = setupRoute.call(this);
    hasPermission = true;

    await route.beforeModel();
    await settled();
    assert.equal(transition, null);
  });

  test('the contextual-help "topic" are set on activation and unset on deactivation of the route', async function(assert) {
    assert.expect(3);

    const route = setupRoute.call(this);
    assert.equal(route.get('contextualHelp.topic'), null, 'The contextual-help topic is null by default');

    route.activate();
    assert.equal(route.get('contextualHelp.topic'), route.get('contextualHelp.respIncListVw'), 'The contextual-help topic is updated on activation');

    route.deactivate();
    assert.equal(route.get('contextualHelp.topic'), null, 'The contextual-help topic is reverted to null on deactivate');
  });

  test('ensure the expected action creator is called', async function(assert) {
    assert.expect(1);

    const route = setupRoute.call(this);
    await route.model();

    await waitUntil(() => {
      const { respond: { incidents: { isSendToArcherAvailable } } } = redux.getState();
      if (isSendToArcherAvailable === true) {
        assert.ok(true, 'isSendToArcherAvailable was set meaning the action creator was called');
      }
      return isSendToArcherAvailable;
    }, { timeout });

    await settled();
  });
});
