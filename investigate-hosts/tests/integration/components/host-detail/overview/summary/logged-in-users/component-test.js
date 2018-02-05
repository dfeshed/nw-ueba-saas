import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { linux } from '../../../../state/overview.hostdetails';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';

import engineResolverFor from '../../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../../helpers/patch-reducer';

let setState;
moduleForComponent('host-detail/overview/summary/logged-in-users', 'Integration | Component | endpoint host detail/overview/summary/logged-in-users', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    setState = (machine) => {
      const { overview } = machine;
      const state = Immutable.from({ endpoint: { overview } });
      applyPatch(state);
      this.inject.service('redux');
    };
  },

  afterEach() {
    revertPatch();
  }
});

test('it renders multiple logged-in-users', function(assert) {
  setState(linux);
  this.render(hbs`
    {{host-detail/overview/summary/logged-in-users}}
  `);

  return wait().then(() => {
    const loggedInUsers = this.$('.host-content__user-details').length;
    assert.equal(loggedInUsers, 2, 'number of logged-in-user');
  });
});

test('it renders when machine is undefined', function(assert) {
  const agent = {
    overview: {
      hostDetails: {}
    }
  };

  setState(agent);
  this.render(hbs`
    {{host-detail/overview/summary/logged-in-users}}
  `);

  return wait().then(() => {
    const loggedInUsers = this.$('.host-content__user-details').length;
    assert.equal(loggedInUsers, 0, 'number of logged-in users when machine is undefined');
  });
});

test('it renders when users is undefined', function(assert) {
  const agent = {
    overview: {
      hostDetails: {
        machine: {}
      }
    }
  };

  setState(agent);
  this.render(hbs`
    {{host-detail/overview/summary/logged-in-users}}
  `);

  return wait().then(() => {
    const loggedInUsers = this.$('.host-content__user-details').length;
    assert.equal(loggedInUsers, 0, 'number of logged-in users when users is undefined');
  });
});

test('it renders isAdmin user', function(assert) {
  setState(linux);
  this.render(hbs`
    {{host-detail/overview/summary/logged-in-users}}
  `);

  return wait().then(() => {
    const adminUser = this.$('.host-content__user-details__administrator')[0].innerText;
    assert.deepEqual(adminUser.trim(), 'Administrator', 'is admin user');
  });
});

test('it renders when user is not Admin', function(assert) {
  setState(linux);
  this.render(hbs`
    {{host-detail/overview/summary/logged-in-users}}
  `);

  return wait().then(() => {
    const adminUser = this.$('.host-content__user-details__administrator')[1].innerText;
    assert.deepEqual(adminUser.trim(), '', 'is not admin user');
  });
});

test('count of account-circle icons', function(assert) {
  setState(linux);
  this.render(hbs`
    {{host-detail/overview/summary/logged-in-users}}
  `);

  return wait().then(() => {
    const iconLength = this.$('.host-content__user-details__icon > i.rsa-icon-account-circle-1-lined').length;
    assert.deepEqual(iconLength, 2, 'icon class name is present');
  });
});

test('it renders loggedin users name', function(assert) {
  setState(linux);
  this.render(hbs`
    {{host-detail/overview/summary/logged-in-users}}
  `);

  return wait().then(() => {
    const name1 = this.$('.host-content__user-details__name')[0].innerText;
    const name2 = this.$('.host-content__user-details__name')[1].innerText;
    assert.deepEqual(name1.trim(), 'Sharms74', 'first user name');
    assert.deepEqual(name2.trim(), 'Root', 'second user name');
  });
});

test('it renders logged-in users labels count', function(assert) {
  setState(linux);
  this.render(hbs`
    {{host-detail/overview/summary/logged-in-users}}
  `);

  return wait().then(() => {
    const labelCounts = this.$('.host-content__user-details > .user-details vbox h4.label').length;
    assert.deepEqual(labelCounts, 4, 'total number of labels');
  });
});

test('it renders logged-in users label names', function(assert) {
  setState(linux);
  this.render(hbs`
    {{host-detail/overview/summary/logged-in-users}}
  `);

  return wait().then(() => {
    const labels = this.$('.host-content__user-details:first-child > .user-details vbox h4.label');
    assert.deepEqual(labels[0].innerText.trim(), 'HOST', 'Host label');
    assert.deepEqual(labels[1].innerText.trim(), 'DEVICE NAME', 'Device Name label');
  });
});