/**
 * @file HBS (HTMLBars) Preview component.
 * Given a snippet of HBS code, renders a "live" instance of the resulting component(s) in the DOM.
 * @public
 */
import Ember from 'ember';

const {
  getOwner,
  Component,
  run,
  HTMLBars,
  $
} = Ember;

export default Component.extend({

  tagName: 'section',

  classNames: 'hbs-preview',

  /**
   * The handlebars snippet (e.g., {{#my-comp attr1=val1}}..{{/my-comp}}).
   * @type String
   * @public
   */
  code: '',

  /**
   * Kicks off the evaluation and rendering of the HBS code snippet.
   * First instantiates a new component using the HBS code as its layout, then appends it document.body
   * to trigger its render, then uses jQuery to move its DOM into this component's inner DOM.
   * @public
   */
  didInsertElement() {
    let me = this,
      code = me.get('code');
    if (!code) {
      return;
    }

    // @workaround Use Ember.run.next() to avoid Ember warning about manipulating DOM on didInsertElement.
    run.next(function() {

      // Instantiate a new component using the HBS snippet.
      let childComp = Component.create({
        layout: HTMLBars.compile(code),

        // @workaround This CSS class allows us to hide the new child while it is outside this component's DOM.
        classNames: 'for-hbs-preview-only',

        // Container is necessary in order to support composite (nested) components.
        container: getOwner(me),

        // Once component is rendered into document.body, move its DOM into our DOM.
        didInsertElement() {

          // @workaround Use Ember.run.next() to avoid Ember warning about manipulating DOM on didInsertElement.
          run.next(function() {
            $(childComp.element).appendTo(me.element);
          });
        }
      });

      // Trigger the child component's render by asking it to append itself to DOM.
      // @workaround If we use .appendTo(ourDom), Ember throws an error, saying that you Views cannot render inside
      // other Views, only inside ContainerViews. So instead we use .append(), which appends to document.body,
      // and then we let our didInsertElement() handler above move the DOM into our view.
      childComp.append();
    });
  }
});
