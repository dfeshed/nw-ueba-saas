import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import { isReconFullSize } from 'investigate-events/reducers/investigate/data-selectors';
import { setReconPanelSize } from 'investigate-events/actions/interaction-creators';

const stateToComputed = (state) => ({
  isReconOpen: state.investigate.data.isReconOpen,
  isReconFullSize: isReconFullSize(state),
  reconSize: state.investigate.data.reconSize
});

const dispatchToActions = {
  setReconPanelSize
};

const ReconSizeToggle = Component.extend({
  tagName: '',

  // when we go full size, we want to keep track
  // of the previous size so that when the size
  // is toggled again, it can go back to that size
  lastSize: null,

  @computed('isReconFullSize', 'reconSize')
  reconToggleGoToSize(isReconFullSize, reconSize) {
    if (isReconFullSize) {
      return this.get('lastSize');
    } else {
      this.set('lastSize', reconSize);
      return RECON_PANEL_SIZES.FULL;
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ReconSizeToggle);
