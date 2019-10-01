import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  isEmailExpanded: false,

  init() {
    this._super(...arguments);

    if (this.get('emailCount') === 1) {
      this.toggleProperty('isEmailExpanded');
    }
  },

  @computed('isEmailExpanded')
  collapseArrowDirection(isEmailHeadersExpanded) {
    return isEmailHeadersExpanded ? 'subtract' : 'add';
  },

  actions: {
    toggleEmailExpansion() {
      this.toggleProperty('isEmailExpanded');
    }
  }
});
