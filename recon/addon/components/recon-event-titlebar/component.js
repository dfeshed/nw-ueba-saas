import Ember from 'ember';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

import layout from './template';
import { TYPES, TYPES_BY_NAME } from '../../utils/reconstruction-types';
import * as VisualActions from '../../actions/visual-creators';
import * as DataActions from '../../actions/data-creators';

const { Component } = Ember;

const stateToComputed = ({ visuals, data }) => ({
  isHeaderOpen: visuals.isHeaderOpen,
  isRequestShown: visuals.isRequestShown,
  isResponseShown: visuals.isResponseShown,
  isMetaShown: visuals.isMetaShown,
  isReconExpanded: visuals.isReconExpanded,
  currentReconView: data.currentReconView
});

const dispatchToActions = (dispatch) => ({
  toggleHeader: () => dispatch(VisualActions.toggleReconHeader()),
  toggleRequest: () => dispatch(VisualActions.toggleRequestData()),
  toggleResponse: () => dispatch(VisualActions.toggleResponseData()),
  toggleMeta: () => dispatch(VisualActions.toggleMetaData()),
  toggleExpanded: () => dispatch(VisualActions.toggleReconExpanded()),
  closeRecon: () => dispatch(VisualActions.closeRecon()),
  updateReconstructionView: (newView) => dispatch(DataActions.changeReconView(newView))
});

const TitlebarComponent = Component.extend({
  layout,
  tagName: 'hbox',
  classNameBindings: [':recon-event-titlebar'],

  // INPUTS
  index: undefined,
  total: undefined,
  // END INPUTS

  /**
  * Determines if we should disable packet related icons
  * @return {boolean}  Whether icons should be disabled
  * @public
  */
  @computed('currentReconView')
  disablePacketIcons({ code }) {
    return code !== TYPES_BY_NAME.PACKET.code;
  },

  @computed('currentReconView')
  reconViewsConfig({ code }) {
    return TYPES.map((c) => {
      return { ...c, selected: c.code === code };
    });
  },

  /**
   * Dynamically builds the recon type selection prompt.
   * Adds 1 to index as it is 0 based.
   *
   * @type {string} The title to display
   * @public
   */
  @computed('currentReconView', 'index', 'total')
  displayTitle: ({ label }, index, total) => {
    if (index !== undefined) {
      label = `${label} (${index + 1} of ${total})`;
    }
    return label;
  },

  @computed('isReconExpanded')
  arrowDirection: (isReconExpanded) => (isReconExpanded) ? 'right' : 'left',

  actions: {
    findNewReconstructionView([code]) {
      // codes are int, comes in as string from form-select
      const newView = TYPES.findBy('code', parseInt(code, 10));
      if (newView) {
        this.send('updateReconstructionView', newView);
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(TitlebarComponent);