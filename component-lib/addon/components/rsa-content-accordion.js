import Ember from 'ember';
import layout from '../templates/components/rsa-content-accordion';

const { Component } = Ember;

export default Component.extend({

  layout,

  classNames: ['rsa-content-accordion'],

  classNameBindings: ['isCollapsed'],

  label: null,

  isCollapsed: false,

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