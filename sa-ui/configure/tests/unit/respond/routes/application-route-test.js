import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { computed } from '@ember/object';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import ApplicationRoute from 'configure/routes/application';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

let transitionToExternal, primary;

module('Unit | Route | application', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(function() {
    primary = true;
    transitionToExternal = null;
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'application'
    }));
    const session = Service.extend({
      isNwUIPrimary: computed(function() {
        return primary;
      })
    }).create();
    const PatchedRoute = ApplicationRoute.extend({
      session: computed(function() {
        return session;
      }),
      transitionToExternal(routeName) {
        transitionToExternal = routeName;
      }
    });
    return PatchedRoute.create();
  };

  test('it should transitionToExternal "protected" route if the the session.isNwUIPrimary flag is false', async function(assert) {
    assert.expect(1);
    const route = setupRoute.call(this);
    primary = false;
    await route.beforeModel();
    await settled();
    assert.equal(transitionToExternal, 'protected', 'transitionToExternal was called with protected');
  });
  //
  test('it should NOT transitionToExternal "protected" route if isNwUIPrimary flag is true', async function(assert) {
    assert.expect(1);
    const route = setupRoute.call(this);
    primary = true;
    await route.beforeModel();
    await settled();
    assert.equal(transitionToExternal, null, 'transitionToExternal was NOT called');
  });
});
