import Component from '@ember/component';
import { run } from '@ember/runloop';

export default Component.extend({
  radius: 20,

  didReceiveAttrs() {
    this._super(...arguments);
    run.schedule('afterRender', this, this.afterRender);
  },

  afterRender() {
    const percentage = this.get('percentage');
    const perc = parseInt(percentage, 10);
    const CIRCUMFERENCE = 2 * Math.PI * this.get('radius');
    const progress = perc / 100;
    const dashoffset = CIRCUMFERENCE * (1 - progress);
    this.$('.rsa-semi-circle_value').css({
      strokeDasharray: CIRCUMFERENCE,
      strokeDashoffset: dashoffset
    });
  }
});