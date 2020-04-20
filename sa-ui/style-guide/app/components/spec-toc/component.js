import Component from '@ember/component';

export default Component.extend({
  classNames: ['spec-toc'],

  /*
   * The offset to add to the scrollTo
   */
  offset: undefined,

  actions: {
    scrollTo(selector) {
      this.scrollTo(selector, this.get('offset'));
    }
  }
});
