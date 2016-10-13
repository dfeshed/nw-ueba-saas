import Ember from 'ember';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

import layout from './template';
import { RECON_VIEW_TYPES, RECON_VIEW_TYPES_BY_NAME } from '../../utils/reconstruction-types';
import * as VisualActions from '../../actions/visual-creators';
import * as DataActions from '../../actions/data-creators';

const { Component } = Ember;

const stateToComputed = ({ recon: { visuals, data } }) => ({
  isHeaderOpen: visuals.isHeaderOpen,
  isRequestShown: visuals.isRequestShown,
  isResponseShown: visuals.isResponseShown,
  isMetaShown: visuals.isMetaShown,
  isReconExpanded: visuals.isReconExpanded,
  currentReconView: data.currentReconView,
  index: data.index,
  total: data.total
});

const dispatchToActions = (dispatch) => ({
  toggleHeader: () => dispatch(VisualActions.toggleReconHeader()),
  toggleRequest: () => dispatch(VisualActions.toggleRequestData()),
  toggleResponse: () => dispatch(VisualActions.toggleResponseData()),
  toggleMeta: () => dispatch(DataActions.toggleMetaData()),
  toggleExpanded: () => dispatch(VisualActions.toggleReconExpanded()),
  closeRecon: () => dispatch(VisualActions.closeRecon()),
  updateReconstructionView: (newView) => dispatch(DataActions.setNewReconView(newView))
});

const TitlebarComponent = Component.extend({
  layout,
  tagName: 'hbox',
  classNameBindings: [':recon-event-titlebar'],

  /**
  * Determines if we should disable packet related icons
  * @return {boolean}  Whether icons should be disabled
  * @public
  */
  @computed('currentReconView')
  disablePacketIcons({ code }) {
    return code !== RECON_VIEW_TYPES_BY_NAME.PACKET.code;
  },

  /**
  * Processes RECON_VIEWS and setings selected flag for
  * the one currently chosen
  *
  * @return {boolean}  Whether icons should be disabled
  * @public
  */
  @computed('currentReconView')
  reconViewsConfig({ code }) {
    return RECON_VIEW_TYPES.map((c) => {
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
    if (index != null) {
      label = `${label} (${index + 1} of ${total})`;
    }
    return label;
  },

  @computed('isReconExpanded')
  arrowDirection: (isReconExpanded) => (isReconExpanded) ? 'right' : 'left',

  actions: {
    // translates code to the recon view and sends action to update view
    findNewReconstructionView([code]) {
      const newView = RECON_VIEW_TYPES.findBy('code', parseInt(code, 10));
      if (newView) {
        this.send('updateReconstructionView', newView);
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(TitlebarComponent);