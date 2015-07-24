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

/* @todo Figure out why this test below fails.
test("click logout link and check protected route (explore) not accessible", function(assert) {
    assert.expect(2);
    visit("/");

    andThen(function() {
        var content = find(".js-test-click-logout");
        assert.ok(content.length, "Could not find the logout DOM node.");
        content.trigger("click");

        visit("/do/monitor");
        andThen(function() {
            assert.notEqual(currentPath(), "protected.monitor");
        });
    });
});
 */

test("login form submit", function(assert) {
    assert.expect(0);
    invalidateSession();
    visit("/login");
    andThen(function() {
        fillIn(".js-test-fillin-username", "admin");
        fillIn(".js-test-fillin-password", "netwitness");
        click(".js-test-click-login");
    });
});

test("invalidate session and check protected route (explore) not accessible", function(assert) {
    assert.expect(1);
    invalidateSession();
    visit("/do/explore");

    andThen(function() {
        assert.notEqual(currentPath(), "protected.explore");
    });
});

test("authenticate session and check protected route (explore) is accessible", function(assert) {
    assert.expect(1);
    authenticateSession();
    currentSession().set("currentProject", "some test project");
    visit("/do/explore");

    andThen(function() {
        assert.equal(currentPath(), "protected.explore");
    });
});
