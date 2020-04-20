import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';

module('Unit | Service | riac', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  let redux;

  hooks.beforeEach(function() {
    initialize(this.owner);
    redux = this.owner.lookup('service:redux');
  });

  const makePayload = function(bool, admins) {
    return {
      data: {
        enabled: bool,
        adminRoles: admins
      }
    };
  };

  test('hasAlertsAccess is set when required roles are included', function(assert) {
    const accessControl = this.owner.lookup('service:access-control');
    const riac = this.owner.lookup('service:riac');

    redux.dispatch(makePackAction(LIFECYCLE.SUCCESS, {
      type: 'RESPOND::GET_RIAC_SETTINGS',
      payload: makePayload(false, ['Respond_Adminstrators'])
    }));

    // access ALLOWED under rbac.
    assert.equal(riac.get('hasAlertsAccess'), true);


    redux.dispatch(makePackAction(LIFECYCLE.SUCCESS, {
      type: 'RESPOND::GET_RIAC_SETTINGS',
      payload: makePayload(true, ['Respond_Adminstrators'])
    }));

    // admin should have access
    accessControl.set('authorities', ['Respond_Adminstrators']);
    assert.equal(riac.get('hasAlertsAccess'), true);

    // some roles don't have access
    accessControl.set('authorities', ['Analysts']);
    assert.equal(riac.get('hasAlertsAccess'), false);
  });

  test('hasTasksAccess is set when required roles are included', function(assert) {
    const accessControl = this.owner.lookup('service:access-control');
    const riac = this.owner.lookup('service:riac');

    redux.dispatch(makePackAction(LIFECYCLE.SUCCESS, {
      type: 'RESPOND::GET_RIAC_SETTINGS',
      payload: makePayload(false, ['Respond_Adminstrators'])
    }));

    // access ALLOWED under rbac.
    assert.equal(riac.get('hasTasksAccess'), true);

    redux.dispatch(makePackAction(LIFECYCLE.SUCCESS, {
      type: 'RESPOND::GET_RIAC_SETTINGS',
      payload: makePayload(true, ['Respond_Adminstrators'])
    }));

    // admins should have access
    accessControl.set('authorities', ['Respond_Adminstrators']);
    assert.equal(riac.get('hasTasksAccess'), true);

    // some groups don't have access
    accessControl.set('authorities', ['Analysts']);
    assert.equal(riac.get('hasTasksAccess'), false);
  });

  test('canChangeAssignee is set when required roles are included', async function(assert) {
    const accessControl = this.owner.lookup('service:access-control');
    const riac = this.owner.lookup('service:riac');

    redux.dispatch(makePackAction(LIFECYCLE.SUCCESS, {
      type: 'RESPOND::GET_RIAC_SETTINGS',
      payload: makePayload(false, ['Administrators', 'Respond_Adminstrators'])
    }));

    // canChangeAssignee ALLOWED under rbac.
    assert.equal(riac.get('canChangeAssignee'), true);


    redux.dispatch(makePackAction(LIFECYCLE.SUCCESS, {
      type: 'RESPOND::GET_RIAC_SETTINGS',
      payload: makePayload(true, ['Administrators', 'Respond_Adminstrators'])
    }));

    // admin should have access
    accessControl.set('authorities', ['Respond_Adminstrators']);
    assert.equal(riac.get('hasAlertsAccess'), true);

    // some roles don't have access
    accessControl.set('authorities', ['Analysts']);
    assert.equal(riac.get('hasAlertsAccess'), false);
  });
});
