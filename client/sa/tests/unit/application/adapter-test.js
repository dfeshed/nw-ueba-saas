import { moduleFor, test } from "ember-qunit";
import config from "sa/config/environment";

moduleFor("adapter:application", "Unit | Adapter | application", {
  // Specify the other units that are required for this test.
  // needs: ["serializer:foo"]
});

// Replace this with your real tests.
test("it exists", function(assert) {
    let adapter = this.subject();
    assert.equal(adapter.authorizer, config["ember-simple-auth"].authorizer, "SA Authorizer not set in adapter");
    assert.ok(adapter);
});
