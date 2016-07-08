/**
 * @file HBS (HTMLBars) Example component.
 * Displays a given snippet of HBS code and a "live" view of the resulting GUI component.
 * Leverages HighlightJS library for the highlighting (see: https://highlightjs.org/).
 * Leverages ClipboardJS library for Copy To Clipboard functionality (see: http://clipboardjs.com/).
 * @public
 */

import Ember from 'ember';
/* global Clipboard */

export default Ember.Component.extend({

  tagName: 'section',

  classNames: 'hbs-example',

  classNameBindings: ['noSnippet'],

  title: '',

  dataType: null,

  /**
   * If true, indicates that the snippet child component should be omitted.
   * @type boolean
   * @default false
   * @public
   */
  noSnippet: (function() {
    return this.get('dataType') === 'typography' || this.get('dataType') === 'demo' || this.get('dataType') === 'demoComp';
  }).property('dataType'),

  /**
   * The handlebars snippet (e.g., {{#my-comp attr1=val1}}..{{/my-comp}}).
   * @type String
   * @public
   */
  code: '',

  /**
   * Wires up the onclick of the Copy To Clipboard button via Clipboard JS.
   * Using ClipboardJS library's API, we specify what text should go into clipboard when trigger is clicked.
   * @public
   */
  didInsertElement() {
    let code = this.get('code');
    if (!code) {
      return;
    }

    this.clipboard = new Clipboard(
      Ember.$(this.element).find('.js-clipboard-trigger')[0],
      {
        text() {
          return code;
        }
      }
    );
  },

  /**
   * Teardown for the onclick listener in Clipboard JS.
   * @public
   */
  willDestroyElement() {
    if (this.clipboard) {
      this.clipboard.destroy();
      this.clipboard = null;
    }
  }
});
