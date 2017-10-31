import Component from 'ember-component';
import layout from './template';

export default Component.extend({
  layout,

  classNames: ['rsa-content-accordion'],

  classNameBindings: ['isCollapsed'],

  label: null,

  isCollapsed: false,

  animate: false,

  actions: {
    /**
    * Toggle visibility of content block
    * Updates isCollapsed
    * @public
    */
    toggleContent() {
      this.toggleProperty('isCollapsed');
    }
  }
});
