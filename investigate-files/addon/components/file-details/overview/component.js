import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  setDetailAlertTab
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  activeDetailAlertTab: state.files.visuals.activeDetailAlertTab
});

const dispatchToActions = {
  setDetailAlertTab
};

const Overview = Component.extend({
  tagName: 'box',

  classNames: ['file-overview']
});

export default connect(stateToComputed, dispatchToActions)(Overview);
