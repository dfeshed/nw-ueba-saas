import LinkComponent from '@ember/routing/link-component';
import { getOwner } from '@ember/application';
import { set, get } from '@ember/object';

/**
 * This component is similar to Ember.LinkComponent, except that:
 *
 * 1. It does not swallow the `click` DOM event.
 *
 * 2. It uses `window.open(..)` to navigate to a URL, rather than relying on
 *    Ember route transitions.
 *
 * This component is useful for when you want a hyperlink to open another
 * browser tab/window without swallowing the `click` event.
 *
 * @class LinkToWindow component
 * @constructor
 * @public
 */
export default LinkComponent.extend({

  // Overwrite inherited bindings from LinkComponent (which includes `href`).
  // Now, the DOM's `href` won't be set; instead it's data-href will be.
  attributeBindings: ['href:data-href', 'title', 'rel', 'tabindex', 'target'],

  // Prevent some browsers from rendering an empty `href` by default.
  didInsertElement() {
    this._super(...arguments);
    this.$().removeAttr('href');
  },

  // Clicking on an `<a data-href=..>` will do nothing natively.
  // This handler will manually open a window to the href.
  click() {
    this._super(...arguments);
    window.open(this.get('href'), this.get('target') || '_self');
    return true;
  },

  // handle engine case
  // https://github.com/ember-engines/ember-engines/blob/1b36714653fbc395db23b849058617c31d778fed/addon/-private/link-to-component.js
  didReceiveAttrs() {
    this._super(...arguments);
    const owner = getOwner(this);
    if (owner.mountPoint) {
      const fullRouteName = `${owner.mountPoint}.${get(this, 'targetRouteName')}`;
      set(this, 'targetRouteName', fullRouteName);
    }
  }
});
