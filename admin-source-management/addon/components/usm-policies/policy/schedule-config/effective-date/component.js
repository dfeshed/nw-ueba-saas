import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  startDate
} from 'admin-source-management/reducers/usm/policy-selectors';
import {
  updatePolicyProperty,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-creators';

import { isEmpty } from '@ember/utils';

const stateToComputed = (state) => ({
  startDate: startDate(state)
});

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const EffectiveDate = Component.extend({
  tagName: 'box',

  classNames: 'effective-date',

  actions: {
    onDateChange(selectedDates) {
      if (!isEmpty(selectedDates[0])) {
        this.send('updatePolicyProperty', 'scanStartDate', selectedDates[0].getTime());
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(EffectiveDate);
