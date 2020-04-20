import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { computed } from '@ember/object';
import { settled } from '@ember/test-helpers';
import InvestigateHosts from 'investigate-hosts/routes/hosts';

let hasPermission, transition;

module('Unit | Route | investigate-hosts.hosts', function(hooks) {

  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:contextualHelp', Service.extend({}));
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'investigate-hosts'
    }));

    const accessControl = Service.extend({
      hasInvestigateHostsAccess: computed(function() {
        return hasPermission;
      })
    }).create();

    const contextualHelp = this.owner.lookup('service:contextualHelp');
    const PatchedRoute = InvestigateHosts.extend({
      accessControl: computed(function() {
        return accessControl;
      }),
      contextualHelp: computed(function() {
        return contextualHelp;
      }),
      transitionTo(routeName) {
        transition = routeName;
      }
    });
    return PatchedRoute.create();
  };

  test('it transitions to "permission-denied" if the user does not have access', async function(assert) {
    assert.expect(1);
    const route = setupRoute.call(this);
    hasPermission = false;

    await route.beforeModel();
    await settled();
    assert.equal(transition, 'permission-denied');
  });

});
