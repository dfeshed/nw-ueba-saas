import Component from '@ember/component';
import { connect } from 'ember-redux';
import moment from 'moment';
import {
  startDate,
  startDateValidator
} from 'admin-source-management/reducers/usm/policy-wizard-selectors';
import {
  updatePolicyProperty,
  removeFromSelectedSettings
} from 'admin-source-management/actions/creators/policy-wizard-creators';

import { isEmpty } from '@ember/utils';

const stateToComputed = (state) => ({
  startDate: startDate(state),
  startDateValidator: startDateValidator(state)
});

const dispatchToActions = {
  updatePolicyProperty,
  removeFromSelectedSettings
};

const EffectiveDate = Component.extend({
  tagName: 'box',

  classNames: 'scan-start-date',

  selectedSettingId: null,

  actions: {
    onDateChange(selectedDates) {
      const value = isEmpty(selectedDates[0]) ? '' : moment(selectedDates[0]).format('YYYY-MM-DD');
      this.send('updatePolicyProperty', 'scanStartDate', value);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(EffectiveDate);
