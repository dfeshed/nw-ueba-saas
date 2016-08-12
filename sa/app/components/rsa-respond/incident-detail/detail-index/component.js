import Ember from 'ember';

const {
  Component
} = Ember;

export default Component.extend({
  entity: null,
  showJournal: true,
  info: null,

  actions: {
    showEntity(entity, info) {
      this.setProperties({
        entity, info
      });
    },
    journalAction() {
      this.set('showJournal', true);
    }
  }
});
