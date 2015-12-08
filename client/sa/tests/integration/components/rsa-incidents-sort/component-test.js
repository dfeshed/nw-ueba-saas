import Ember from "ember";
import { moduleForComponent, test } from "ember-qunit";
import hbs from "htmlbars-inline-precompile";
import initializer from "sa/instance-initializers/ember-i18n";

moduleForComponent("rsa-incidents-sort", "Integration | Component | rsa incidents sort", {
    integration: true,

    beforeEach: function(){

        // Initializes the locale so that i18n content in the component will work.
        // See: https://github.com/jamesarosen/ember-i18n/wiki/Doc:-Testing
        initializer.initialize(this);
    }
});

test("it renders", function(assert) {
    assert.expect(3);

    // Define a model for the component to talk to.
    this.set("model", Ember.Object.create({

        // sort() should be called when clicking on the Sort DOM options.
        // Fire assert() to confirm it was called.
        sort: function(){
            assert.ok(true, "Clicking on a sort option didn't trigger the model.sort method.");
        }
    }));

    // Render the component.
    this.render(hbs`{{rsa-incidents-sort cube=model}}`);
    assert.ok(this.$(".rsa-incidents-sort").length, "Could not find component DOM element.");

    // Find the DOM for the Sort by ID option and click it.
    var content = this.$(".rsa-incidents-sort .js-test-respond-incs-sort-id-li");
    assert.ok(content.length, "Could not find component's DOM option for sorting by ID.");
    content.trigger("click");
});
