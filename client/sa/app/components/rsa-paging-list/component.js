/**
 * @file Paging List component.
 * List that renders a given a subset of records one page at a time.
 * Use Paging List to display long lists of records in chunks (or "pages") in order to avoid overwhelming the
 * browser's memory consumption with a huge list DOM.
 */
import Ember from "ember";


// Resets the attribute used for data binding in the user input UI to the current page number.
function syncUserInputUI(me) {
    me.set("pageNumberUserInput", me.get("pageNumber"));
}

export default Ember.Component.extend({
    tagName: "section",
    classNames: "rsa-paging-list",

    /**
     * Indicates whether markup should include footer with HTML elements for paging.
     * @type Boolean
     * @default true
     */
    includeFooter: true,

    /**
     * Array of data records to be rendered in the list.
     * @type Object[]
     */
    records: null,

    /**
     * Subset of "records" to be rendered in the current page number.
     * @type Object[]
     */
    results: function() {
        var arr = this.get("records"),
            len = (arr && arr.length) || 0;
        if (!len) {
            return [];
        }
        else {
            var size = this.get("pageSize"),
                start =  (this.get("pageNumber") - 1) * size,
                stop = start + size;
            return arr.slice(start, stop);
        }
    }.property("records", "pageSize", "pageNumber"),

    /**
     * Responds to change in total number of pages by ensuring the current page is within valid bounds.
     */
    pagesDidChange: function(){
        var pages = this.get("pages");
        if (pages && (pages < this.get("pageNumber"))) {
            this.goto(1);
        }
    }.observes("pages"),

    /**
     * Computes the number of pages needed to contain the current "records" array, based on the current "pageSize".
     * @type Integer
     */
    pages: function(){
        var size = parseInt(this.get("pageSize"),10);
        if (!size || isNaN(size)) {
            return 1;
        }
        else {
            var arr = this.get("records");
            return Math.ceil(((arr && arr.length) || 0) / Math.abs(size));
        }
    }.property("records", "pageSize"),

    /**
     * Maximum number of records to display at a time. If zero/undefined, all records will be contained in a single
     * page. Minus signs and fractions are discarded.
     * @type Number
     * @default 100
     */
    pageSize: 100,

    /**
     * Number (1-based) of the currently displayed page.  This is a read-only property. To set the current
     * page, call methods such as goto(), first(), prev(), next() and last().
     * @read-only
     * @type Number
     * @default 1
     */
    pageNumber: 1,

    /**
     * Stores the current user input for page number navigation. This is used for data binding to an input textbox
     * UI, in conjunction with the "updatePageNumberUserInput" action, which reads this value.
     * @readonly
     * @type text
     */
    pageNumberUserInput: 1,

    /**
     * Synchronizes the input textbox UI with the current pageNumber.
     */
    pageNumberDidChange: function(){
        syncUserInputUI(this);
    }.observes("pageNumber"),

    // @todo Avoid these actions stubs using Ember.compute or Ember2.0 syntax
    actions: {
        goto: function(num){
            Ember.run(this, "goto", num);
        },
        first: function(){
            Ember.run(this, "first");
        },
        prev: function(){
            Ember.run(this, "prev");
        },
        next: function(){
            Ember.run(this, "next");
        },
        last: function(){
            Ember.run(this, "last");
        },
        updatePageNumberUserInput: function(){
            var me = this;
            Ember.run(function(){
                me.goto(me.get("pageNumberUserInput"));
                syncUserInputUI(me);
            });
        }
    },

    /**
     * Changes the current pageNumber to the given number. Validates input and enforces min/max limits.
     * @param {Number} num The page number (1-based) to navigate to.
     * @returns {Boolean} true if successful, false otherwise
     */
    goto: function(num){
        num = Math.abs(parseInt(num, 10));
        if (num && !isNaN(num)) {
            this.set("pageNumber", Math.min(num, this.get("pages") || 1));
        }
    },

    /**
     * Resets the current page number to 1.
     */
    first: function() {
        this.set("pageNumber", 1);
    },

    /**
     * Decrements the current page number by 1, if possible.
     */
    prev: function() {
        this.set("pageNumber", this.get("pageNumber") - 1);
    },

    /**
     * Increments the current page number by 1, if possible.
     */
    next: function(){
        this.set("pageNumber", this.get("pageNumber") + 1);
    },

    /**
     * Resets the current page number to the last page, as computed by the current "records" and "pageSize".
     */
    last: function(){
        this.set("pageNumber", this.get("pages"));
    }
});
