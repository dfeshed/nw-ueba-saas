import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'ul',
  classNames: ['rsa-collapseable-nav-rsa-collapsed-nav-dropdown'],

  decoratedContentSections: null,
  onTabClick: null,

  actions: {
    onTabClick(section) {
      return this.get('onTabClick')(section);
    }
  }

});
