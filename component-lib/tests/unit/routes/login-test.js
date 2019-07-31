import { Promise } from 'rsvp';
import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Service from '@ember/service';
import { computed } from '@ember/object';
import LoginRoute from 'component-lib/routes/login';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import sinon from 'sinon';
import { windowProxy } from 'component-lib/utils/window-proxy';
import { patchFetch } from '../../helpers/patch-fetch';

let transitionTo, isAuthenticated, isSsoEnabled;

module('Unit | Route | login', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    isAuthenticated = false;
    isSsoEnabled = true;
    initialize(this.owner);
    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return isSsoEnabled;
          }
        });
      });
    });
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'login'
    }));
    const session = Service.extend({
      isAuthenticated: computed(function() {
        return isAuthenticated;
      })
    }).create();
    const PatchedRoute = LoginRoute.extend({
      session: computed(function() {
        return session;
      }),
      transitionTo(routeName) {
        transitionTo = routeName;
      }
    });
    return PatchedRoute.create();
  };

  test('it exists', function(assert) {
    const route = this.owner.lookup('route:login');
    assert.ok(route);
  });

  test('should not transitionTo protected route if session is authenticated', async function(assert) {
    assert.expect(1);

    const route = setupRoute.call(this);

    await route.beforeModel();

    await settled();

    assert.equal(transitionTo, null, 'transitionTo was NOT called');
  });

  test('should transitionTo protected route if session is authenticated', async function(assert) {
    assert.expect(1);

    const route = setupRoute.call(this);

    isAuthenticated = true;

    await route.beforeModel();

    await settled();

    assert.equal(transitionTo, 'protected', 'transitionTo was called with protected');
  });

  test('should redirect to saml login url if single sign on is enabled', async function(assert) {
    assert.expect(2);

    let currentUrl = null;
    let newTab = true;

    const locationStub = sinon.stub(windowProxy, 'openInCurrentTab').callsFake((urlPassed) => {
      currentUrl = urlPassed;
      newTab = false;
    });
    const route = setupRoute.call(this);

    await route.model();

    await settled();

    assert.equal(currentUrl, '/saml/login', 'redirect to saml login url if sso is enabled');
    assert.notOk(newTab);
    locationStub.restore();
  });

  test('should not redirect to saml login url if single sign on is disabled', async function(assert) {
    assert.expect(1);

    const locationSpy = sinon.spy(windowProxy, 'openInCurrentTab');

    const route = setupRoute.call(this);

    isSsoEnabled = false;

    await route.model();

    await settled();

    assert.ok(locationSpy.notCalled);
  });
});
