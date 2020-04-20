import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { computed } from '@ember/object';
import { settled, waitUntil } from '@ember/test-helpers';
import AlertsRoute from 'respond/routes/alerts';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';

let redux, transition, hasPermission;

module('Unit | Route | alerts', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    transition = null;
    hasPermission = true;
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'alerts'
    }));
    const riac = Service.extend({
      hasAlertsAccess: computed(function() {
        return hasPermission;
      })
    }).create();
    const contextualHelp = this.owner.lookup('service:contextualHelp');
    const i18n = this.owner.lookup('service:i18n');
    redux = this.owner.lookup('service:redux');
    const PatchedRoute = AlertsRoute.extend({
      i18n: computed(function() {
        return i18n;
      }),
      contextualHelp: computed(function() {
        return contextualHelp;
      }),
      riac: computed(function() {
        return riac;
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
    assert.equal(route.titleToken(), 'Alerts');
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
    assert.equal(route.get('contextualHelp.topic'), route.get('contextualHelp.respAlrtListVw'), 'The contextual-help topic is updated on activation');

    route.deactivate();
    assert.equal(route.get('contextualHelp.topic'), null, 'The contextual-help topic is reverted to null on deactivate');
  });

  test('ensure the expected action creator is called', async function(assert) {
    assert.expect(1);

    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);
    await route.model();

    await waitUntil(() => {
      const { respond: { dictionaries: { alertNames } } } = redux.getState();
      const alertNamesHydrated = alertNames && alertNames.length > 0;
      if (alertNamesHydrated) {
        assert.ok(true, 'alertNames are set meaning the action creator was called');
      }
      return alertNamesHydrated;
    });

    await settled();
  });

});