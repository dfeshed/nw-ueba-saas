/* globals hljs */
import Ember from 'ember';

/**
 * A component that looks for script tags from the {{code-snippet}} component and creates
 * tabs of the different snippets.
 */
export default Ember.Component.extend({
    displayNames: {
        'html': 'HTML',
        'handlebars': 'HTMLBars',
        'javascript': 'JavaScript'
    },
    snippets: Ember.A(),

    /**
     * When inserted this looks for the script tags and creates a map of the snippets.
     */
    didInsertElement: function() {
        if (this.get('snippets').length === 0) {
            this.$('script').each((i, block) => {
                let b = $(block);
                this.get('snippets').pushObject({
                    display: this.displayNames[b.data('lang')],
                    language: b.data('lang'),
                    code: this._cleanupSnippet(b[0].innerHTML),
                    visible: false
                });
            });
        }

        // Defer this till the next run loop so the blocks will be rendered
        Ember.run.next(() => {
            if (this.get('snippets').length > 0) {
                this.$('pre code').each(function(i, block) {
                    hljs.highlightBlock(block);
                });

                this.send('showSnippet', this.get('snippets').get(0));
            }
        });
    },

    actions: {
        /**
         * Toggles showing a snippet
         */
        showSnippet: function(snippet) {
            this.get('snippets').forEach((s) => {
                Ember.set(s, 'visible', snippet.language === s.language);
            });
        }
    },

    /**
     * Cleans-up the code snippet by trimming any white-space and removing surrounding comments.
     */
    _cleanupSnippet: function(snippet) {
        if (typeof snippet !== 'string') {
            return snippet;
        }

        snippet = snippet.trim();

        // Remove any comment blocks that are needed for some languages (JavaScript)
        if (snippet.startsWith('<!--') && snippet.endsWith('-->')) {
            snippet = snippet.replace(/^<!--/, '');
            snippet = snippet.replace(/-->$/, '');
            snippet = snippet.trim();
        }

        return snippet;
    }
});