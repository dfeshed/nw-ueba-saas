/**
 * @file HBS (HTMLBars) Snippet component.
 * Displays a given snippet of HBS code as rich text, with syntax highlighting.
 * Leverages HighlightJS library for the highlighting (see: https://highlightjs.org/).
 * @public
 */
import Ember from 'ember';
/* global hljs */

const {
  Component,
  run,
  $
} = Ember;

export default Component.extend({

  tagName: 'pre',

  classNames: 'hbs-snippet hljs',

  /**
   * The handlebars snippet (e.g., {{#my-comp attr1=val1}}..{{/my-comp}}).
   * @type String
   * @public
   */
  code: '',

  /**
   * Wires up Copy To Clipboard functionality and kicks off highlighting.
   * Clipboard functionality requires handles to DOM nodes for the trigger (button) and target (code text).
   * Highlighting requires handle to the target (text). So we must wait for this component to render first.
   * @public
   */
  didInsertElement() {
    if (!this.get('code')) {
      return;
    }

    let me = this;

    // @workaround Use Ember.run.next() to avoid Ember warning about manipulating DOM on didInsertElement.
    run.next(function() {

      // Kickoff highlighting of target text; detects language from target.className.
      hljs.highlightBlock($(me.element).find('.js-highlight-target')[0]);
    });
  }
});
