import { moduleForComponent, test } from "ember-qunit";
import hbs from "htmlbars-inline-precompile";
import Ember from "ember";
import initializer from "sa/instance-initializers/ember-i18n";
import timeUtil from "sa/utils/time";

moduleForComponent("rsa-incidents-filters", "Integration | Component | rsa incidents filters", {
    integration: true,

    beforeEach: function(){

        // Initializes the locale so that i18n content in the component will work.
        // See: https://github.com/jamesarosen/ember-i18n/wiki/Doc:-Testing
        initializer.initialize(this);
    }
});

test("it renders", function(assert) {
    assert.expect(5);

    this.setProperties({

        // Define a model for the component to try to talk to.
        // The model's filter() should get called when we click on Priority DOM elements.
        // Fire an assert() to confirm it was called.
        "model": Ember.Object.create({
            filter: function() {
                assert.ok(true);
            }
        }),

        // Define an action for the component to try to call when a time range is clicked.
        "onTimeClick": function(){
            assert.ok(true);
        }
    });

    // Render the component.
    this.render(hbs`{{rsa-incidents-filters cube=model timeRangeAction=onTimeClick}}`);
    assert.ok(this.$(".rsa-incidents-filters").length, "Could not find component DOM element.");

    // Click on one of the TimeRange DOM elements ("Last Hour")
    var content = this.$(".rsa-incidents-filters .js-test-respond-incs-filter-hour-btn");
    assert.ok(content.length, "Could not find component's DOM option for hour time range.");
    content.trigger("click");

    // Click on one of the Priority DOM elements ("(All)")
    content = this.$(".rsa-incidents-filters .js-test-respond-incs-filter-li[data-field=\"priority\"][data-group=\"null\"]");
    assert.ok(content.length, "Could not find component's DOM option for (All) Priority.");
    content.trigger("click");
});
