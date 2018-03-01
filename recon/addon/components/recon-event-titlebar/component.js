import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import layout from './template';
import { RECON_VIEW_TYPES } from 'recon/utils/reconstruction-types';
import {
  toggleReconHeader,
  toggleRequestData,
  toggleResponseData,
  toggleReconExpanded,
  closeRecon
} from 'recon/actions/visual-creators';
import { toggleMetaData, setNewReconView } from 'recon/actions/data-creators';
import { isLogEvent, isEndpointEvent } from 'recon/reducers/meta/selectors';

const stateToComputed = ({ recon, recon: { visuals } }) => ({
  currentReconView: visuals.currentReconView,
  isHeaderOpen: visuals.isHeaderOpen,
  isRequestShown: visuals.isRequestShown,
  isResponseShown: visuals.isResponseShown,
  isMetaShown: visuals.isMetaShown,
  isReconExpanded: visuals.isReconExpanded,
  isLogEvent: isLogEvent(recon),
  isEndpointEvent: isEndpointEvent(recon),
  isStandalone: recon.data.isStandalone
});

const dispatchToActions = {
  toggleMetaData,
  toggleReconHeader,
  toggleRequestData,
  toggleResponseData,
  toggleReconExpanded,
  closeRecon,
  setNewReconView
};

const TitlebarComponent = Component.extend({
  layout,
  tagName: 'hbox',
  classNames: ['recon-event-titlebar'],

  /**
   * Processes RECON_VIEWS and setings selected flag for
   * the one currently chosen
   * @return {array}  array of objects
   * @public
   */
  @computed('reconViewsConfigFull')
  reconViewsConfig: (viewsConf) => viewsConf.filter((vc) => !vc.selected),

  @computed('isLogEvent', 'isEndpointEvent')
  isLogBased: (isLog, isEndpoint) => isLog || isEndpoint,

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
  toggleEventsClass: (isReconExpanded) => isReconExpanded ? 'shrink-diagonal-2' : 'expand-diagonal-4',

  @computed('isReconExpanded')
  toggleEventsTitle: (isReconExpanded) => isReconExpanded ? 'recon.toggles.shrink' : 'recon.toggles.expand',

  actions: {
    // translates code to the recon view and sends action to update view
    findNewReconstructionView({ code }) {
      const newView = RECON_VIEW_TYPES.findBy('code', parseInt(code, 10));
      if (newView) {
        this.send('setNewReconView', newView);
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(TitlebarComponent);
