import Component from '@ember/component';

export default Component.extend({
  classNames: 'rsa-investigate-free-form-query-bar',

  actions: {
    keyDown(e) {
      if (e.keyCode === 13) {
        this.executeQuery(this.get('freeFormText').trim());
      }
    }
  }

});