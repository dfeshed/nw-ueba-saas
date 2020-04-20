import Controller from '@ember/controller';

export default Controller.extend({

  editableText: 'Editable Text',

  actions: {
    persistTextChanges(value) {
      this.set('persistedText', value);
    }
  }

});
