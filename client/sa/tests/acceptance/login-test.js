import Ember from "ember";
import { module, test } from "qunit";
import startApp from "sa/tests/helpers/start-app";

var application;

module("Acceptance | login", {
  beforeEach: function() {
    application = startApp();
  },

  afterEach: function() {
    Ember.run(application, "destroy");
  }
});

test("click logout link and check protected route not accessible", function(assert) {
    assert.expect(2);
    visit("/");

    andThen(function() {
        var content = find(".app-header__logout");
        assert.ok(content.length, "Could not find the logout DOM node.");
        content.trigger("click");
    });
    visit("/incidents");
    andThen(function() {
        assert.notEqual(currentRouteName(), "incidents");
    });
});

test("login form submit", function(assert) {
    assert.expect(0);
    invalidateSession();
    visit("/login");
    andThen(function() {
        fillIn(".login-screen__uid", "admin");
        fillIn(".login-screen__pwd", "netwitness");
        click(".login-screen__ok-btn");
    });
});

test("logout and check  protected route not accessible", function(assert) {
    assert.expect(1);
    invalidateSession();
    visit("/incidents");

    andThen(function() {
        assert.notEqual(currentRouteName(), "incidents");
    });
});

test("login and check protected route is accessible", function(assert) {
    assert.expect(1);
    authenticateSession();
    currentSession().set("currentProject", "some test project");
    visit("/explorer");

    andThen(function() {
        var content = find(".app-body .liquid-child");
        assert.equal(content.text().trim(), "Explorer contents go here.");
    });
});
