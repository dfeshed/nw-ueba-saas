import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { computed } from '@ember/object';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import ApplicationRoute from 'admin-source-management/routes/application';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

let transitionToExternal, hasPermission;

module('Unit | Route | application', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    hasPermission = true;
    transitionToExternal = null;
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'application'
    }));
    const accessControl = Service.extend({
      hasAdminViewUnifiedSourcesAccess: computed(function() {
        return hasPermission;
      })
    }).create();
    const i18n = this.owner.lookup('service:i18n');
    const PatchedRoute = ApplicationRoute.extend({
      i18n: computed(function() {
        return i18n;
      }),
      accessControl: computed(function() {
        return accessControl;
      }),
      transitionToExternal(routeName) {
        transitionToExternal = routeName;
      }
    });
    return PatchedRoute.create();
  };

  test('it resolves the proper title token for the route', async function(assert) {
    assert.expect(1);
    const i18n = this.owner.lookup('service:i18n');
    const expectedTitle = `${i18n.t('adminUsm.title')} - ${i18n.t('appTitle')}`;
    const route = setupRoute.call(this);
    assert.equal(route.title(), expectedTitle, `title is ${expectedTitle}`);
  });

  test('it should transitionToExternal "protected" route if the user does not have access', async function(assert) {
    assert.expect(1);
    const route = setupRoute.call(this);
    hasPermission = false;
    await route.beforeModel();
    await settled();
    assert.equal(transitionToExternal, 'protected', 'transitionToExternal was called with protected');
  });

  test('it should NOT transitionToExternal "protected" route if the user has access', async function(assert) {
    assert.expect(1);
    const route = setupRoute.call(this);
    hasPermission = true;
    await route.beforeModel();
    await settled();
    assert.equal(transitionToExternal, null, 'transitionToExternal was NOT called');
  });

});
