import Component from '@ember/component';
import layout from './template';
import { computed } from '@ember/object';
import { run } from '@ember/runloop';

export default Component.extend({
  layout,

  classNames: ['rsa-collapseable-nav'],
  classNameBindings: ['navCollapsed:nav-collapsed:nav-expanded'],
  navCollapsed: false,
  contentSections: null,
  activeTab: null,
  onTabClick: null,
  panelId: `collapsed-nav-options-${(Math.random() * 100000).toFixed().toString()}`,

  decoratedContentSections: computed('contentSections', 'activeTab', function() {
    if (!this.get('activeTab')) {
      return this.get('contentSections');
    } else {
      return this.get('contentSections').map((section) => {
        if (!this.isDestroying && !this.isDestroyed) {
          section.set('isActive', section.name === this.get('activeTab'));
        }

        return section;
      });
    }
  }),

  didInsertElement() {
    new IntersectionObserver((observations) => {
      run(() => {
        if (!this.isDestroying && !this.isDestroyed) {
          this.set('navCollapsed', !observations[0].isIntersecting);
        }
      });
    }, {
      root: this.element
    }).observe(this.element.querySelector('.intersection-trigger'));
  },

  actions: {
    onTabClick(section) {
      this.get('onTabClick')(section);
    }
  }

});
