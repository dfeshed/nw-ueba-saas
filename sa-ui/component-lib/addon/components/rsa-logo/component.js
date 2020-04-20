/**
 * @file RSA Logo component.
 * An HTML element with two children: an RSA logo in SVG (a 'brick' with the letters 'RSA' inside),
 * and an (optional) title text in HTML DOM.
 * The root DOM node can be resized via CSS; the SVG will automatically scale.
 * @public
 */
import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  /*
   Wraps the content in a containing element. If all the markup was in SVG, this root node could
   just be the SVG element, but alas, sometimes the markup includes a text title, which is NOT done
   in SVG, so we need this container.
   @public
   */
  tagName: 'div',

  // Base class prefix for all instances.
  classNameBindings: ['displayEula:rsa-eula-logo:rsa-logo'],
  displayEula: null,
  legacyRsaLogo: true
});
