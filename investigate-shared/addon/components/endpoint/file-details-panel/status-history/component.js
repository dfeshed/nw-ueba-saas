import Component from '@ember/component';
import layout from './template';

export default Component.extend({
  layout,
  classNames: ['status-history'],
  label: 'More',
  actions: {
    toggleAction() {
      if (this.label == 'More') {
        this.label = 'Less';
      } else {
        this.label = 'More';
      }

    }
  }
});
