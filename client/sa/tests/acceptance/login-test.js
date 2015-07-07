import Ember from 'ember';
import { module, test } from 'qunit';
import startApp from 'sa/tests/helpers/start-app';

var application;

module('Acceptance | login', {
  beforeEach: function() {
    application = startApp();
  },

  afterEach: function() {
    Ember.run(application, 'destroy');
  }
});

test('click logout link and check protected route not accessible', function(assert) {
    assert.expect(1);
    visit('/');

    andThen(function() {
        var content = find('a.logout');
        content.trigger('click');
    });
    visit('/incidents');
    andThen(function() {
        assert.notEqual(currentRouteName(), 'incidents');
    });
});

test('login form submit', function(assert) {
    assert.expect(0);
    invalidateSession();
    visit('/login');
    andThen(function() {
        fillIn('input.login-username', 'admin');
        fillIn('input.login-password', 'netwitness');
        click('.login-submit');
    });
});

test('logout and check  protected route not accessible', function(assert) {
    assert.expect(1);
    invalidateSession();
    visit('/incidents');

    andThen(function() {
        assert.notEqual(currentRouteName(), 'incidents');
    });
});

test('login and check protected route is accessible', function(assert) {
    assert.expect(1);
    authenticateSession();
    currentSession().set('currentProject', 'some test project');
    visit('/explorer');

    andThen(function() {
        var content = find('div.sa-content');
        assert.equal(content.text().trim(), 'Threat details');
    });
});
