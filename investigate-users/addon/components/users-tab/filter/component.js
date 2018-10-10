import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updateFilter } from 'investigate-users/actions/user-tab-actions';

const dispatchToActions = {
  updateFilter
};

const UsersTabFliterComponent = Component.extend({
  actions: {
    resetFilters() {
      this.send('updateFilter');
    }
  }
});

export default connect(null, dispatchToActions)(UsersTabFliterComponent);