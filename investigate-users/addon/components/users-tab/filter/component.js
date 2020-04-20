import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { updateFilter, saveAsFavorite } from 'investigate-users/actions/user-tab-actions';
import { later } from '@ember/runloop';

const dispatchToActions = {
  updateFilter,
  saveAsFavorite
};

const UsersTabFliterComponent = Component.extend({
  eventBus: service(),
  closeFilter() {
    this.set('isDisabled', false);
    this.set('name', null);
    this.get('eventBus').trigger('rsa-application-modal-close-saveAsFavorites');
  },
  actions: {
    closeFilter() {
      this.closeFilter();
    },
    createFilter() {
      this.set('isDisabled', true);
      this.send('saveAsFavorite', this.get('name'));
      // This need to replace with actual error handling in coming PR.
      later(() => {
        this.closeFilter();
      }, 500);
    }
  }
});

export default connect(null, dispatchToActions)(UsersTabFliterComponent);