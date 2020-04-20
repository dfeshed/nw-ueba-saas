import Component from '@ember/component';
import { initiateUser } from 'investigate-users/actions/user-details';
import { columnsDataForIndicatorTable } from 'investigate-users/utils/column-config';
import { connect } from 'ember-redux';

const dispatchToActions = {
  initiateUser
};

const IndicatorTableComponent = Component.extend({
  columnsData: columnsDataForIndicatorTable,
  actions: {
    selectUser(alertDetails, { id }) {
      this.send('initiateUser', { ...alertDetails, indicatorId: id });
    }
  }
});

export default connect(null, dispatchToActions)(IndicatorTableComponent);
