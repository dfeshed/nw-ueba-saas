import Controller from '@ember/controller';

export default Controller.extend({
  actions: {
    controllerReconClose() {
      this.send('reconClose');
    }
  }
});
