import Ember from 'ember';
import computed, { or } from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

import layout from './template';
import { RECON_VIEW_TYPES } from 'recon/utils/reconstruction-types';
import * as VisualActions from 'recon/actions/visual-creators';
import * as DataActions from 'recon/actions/data-creators';
import { isLogEvent } from 'recon/selectors/event-type-selectors';
import { lacksPackets } from 'recon/selectors/type-selectors';

const { Component } = Ember;

const stateToComputed = ({ recon, recon: { visuals, data } }) => ({
  currentReconView: data.currentReconView,
  isHeaderOpen: visuals.isHeaderOpen,
  isRequestShown: visuals.isRequestShown,
  isResponseShown: visuals.isResponseShown,
  isMetaShown: visuals.isMetaShown,
  isReconExpanded: visuals.isReconExpanded,
  isLogEvent: isLogEvent(recon),
  lacksPackets: lacksPackets(recon)
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
  classNameBindings: ['isReconExpanded:recon-is-expanded'],
  classNames: ['recon-event-titlebar'],

  /**
   * Determines if we should disable the request/response toggles
   * @param {boolean} lacksPackets Whether or not the event does not have packets
   *   If no packets, then on request/response
   * @param {boolean} isLogEvent Whether the event is a log or not, log events
   *   do not have request/response
   * @returns {boolean} Whether icons should be disabled
   * @public
   */
  @or('lacksPackets', 'isLogEvent')
  disableRequestResponseToggles: null,

  /**
  * Processes RECON_VIEWS and setings selected flag for
  * the one currently chosen
  *
  * @return {array}  array of objects
  * @public
  */
  @computed('reconViewsConfigFull')
  reconViewsConfig: (viewsConf) => viewsConf.filter((vc) => !vc.selected),

  @computed('currentReconView')
  reconViewsConfigFull({ code }) {
    return RECON_VIEW_TYPES.map((viewType) => {
      return {
        ...viewType,
        selected: viewType.code === code
      };
    });
  },

  @computed('isReconExpanded')
  arrowDirection: (isReconExpanded) => (isReconExpanded) ? 'right' : 'left',

  actions: {
    // translates code to the recon view and sends action to update view
    findNewReconstructionView({ code }) {
      const newView = RECON_VIEW_TYPES.findBy('code', parseInt(code, 10));
      if (newView) {
        this.send('updateReconstructionView', newView);
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(TitlebarComponent);