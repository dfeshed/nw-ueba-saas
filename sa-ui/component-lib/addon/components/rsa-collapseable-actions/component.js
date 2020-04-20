import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,

  classNames: ['rsa-collapseable-actions'],
  classNameBindings: [
    'firstTierCollapsed:actions-collapsed:actions-expanded',
    'secondTierCollapsed:actions-fully-collapsed'
  ],

  buttonList: null,
  dropdownList: null,
  toggleList: null,
  displayFind: false,
  firstTierCollapsed: false,
  secondTierCollapsed: false,

  didInsertElement() {
    const firstTierTrigger = document.querySelector('.first-tier-trigger');
    const secondTierTrigger = document.querySelector('.second-tier-trigger');
    const root = document.querySelector('.rsa-collapseable-actions');

    const createObserver = (toCollapse) => {
      return new IntersectionObserver((entry) => {
        if (!this.isDestroyed && !this.isDestroying) {
          this.set(toCollapse, entry[0].rootBounds.right < entry[0].boundingClientRect.left);
        }
      }, {
        rootMargin: '0px -58px 0px 0px',
        root
      });
    };

    const firstTierObserver = createObserver('firstTierCollapsed');
    const secondTierObserver = createObserver('secondTierCollapsed');

    firstTierObserver.observe(firstTierTrigger);
    secondTierObserver.observe(secondTierTrigger);
  }

});
