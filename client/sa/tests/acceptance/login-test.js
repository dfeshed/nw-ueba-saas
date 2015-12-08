import { test } from "qunit";
import moduleForAcceptance from "sa/tests/helpers/module-for-acceptance";
import { currentSession, invalidateSession } from 'sa/tests/helpers/ember-simple-auth';

moduleForAcceptance('Acceptance | login');

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
    invalidateSession(this.application);
    visit("/login");
    andThen(function() {
        fillIn(".js-test-username-input", "admin");
        fillIn(".js-test-password-input", "netwitness");
        click(".js-test-login-btn");
    });
});

test("invalidate session and check protected route (explore) not accessible", function(assert) {
    assert.expect(1);
    invalidateSession(this.application);
    visit("/do/explore");

    andThen(function() {
        assert.notEqual(currentPath(), "protected.explore");
    });
});

test("authenticate session and check protected route (explore) is accessible", function(assert) {
    assert.expect(1);
    authenticateSession(this.application);
    currentSession(this.application).set("currentProject", "some test project");
    visit("/do/explore");

    andThen(function() {
        assert.equal(currentPath(), "protected.explore");
    });
});
