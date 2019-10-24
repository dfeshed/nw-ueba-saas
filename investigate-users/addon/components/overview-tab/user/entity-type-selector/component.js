import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedEntityType } from 'investigate-users/reducers/users/selectors';
import { updateEntityType } from 'investigate-users/actions/user-details';

const stateToComputed = (state) => ({
  entityType: selectedEntityType(state)
});

const dispatchToActions = {
  updateEntityType
};

const EntityTypeSelector = Component.extend({
  classNames: 'user-overview-tab_users_entities',
  entityFilter: ['ja3', 'sslSubject'],
  actions: {
    networkClicked() {
      this.set('networkClicked', true);
    },
    updateEntity(entityType) {
      this.set('networkClicked', false);
      this.send('updateEntityType', entityType);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(EntityTypeSelector);
