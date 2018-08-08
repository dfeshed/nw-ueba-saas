import Controller from '@ember/controller';

export default Controller.extend({

  value: 0,
  display: null,

  init() {
    this.setRandom();
  },

  setRandom() {
    const random = Math.random();
    this.set('value', random);
    this.set('display', random.toFixed(3));
    setTimeout(() => {
      this.setRandom();
    }, 3000);
  }

});
