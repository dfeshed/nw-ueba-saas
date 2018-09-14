import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  setAlertTab
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  activeAlertTab: state.files.visuals.activeAlertTab
});

const dispatchToActions = {
  setAlertTab
};

const Overview = Component.extend({
  tagName: 'box',

  classNames: ['file-overview']
});

export default connect(stateToComputed, dispatchToActions)(Overview);
