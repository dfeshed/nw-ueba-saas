import { skip } from "qunit";
import moduleForAcceptance from "sa/tests/helpers/module-for-acceptance";

moduleForAcceptance('Acceptance | respond');

skip("visiting /do/respond and check DOM", function(assert) {
    assert.expect(2);

    visit("/do/respond");

    andThen(function() {
        assert.equal(currentPath(), "protected.respond");
        assert.equal(find(".js-test-respond-root").length, 1, "Could not find the route DOM.");
    });

});
