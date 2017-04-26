import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

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
import { isLogEvent } from 'recon/reducers/meta/selectors';
import { lacksPackets } from 'recon/reducers/visuals/selectors';

const stateToComputed = ({ recon, recon: { visuals } }) => ({
  currentReconView: visuals.currentReconView,
  isHeaderOpen: visuals.isHeaderOpen,
  isRequestShown: visuals.isRequestShown,
  isResponseShown: visuals.isResponseShown,
  isMetaShown: visuals.isMetaShown,
  isReconExpanded: visuals.isReconExpanded,
  isLogEvent: isLogEvent(recon),
  lacksPackets: lacksPackets(recon)
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
  classNameBindings: ['isReconExpanded:recon-is-expanded'],
  classNames: ['recon-event-titlebar'],

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
        this.send('setNewReconView', newView);
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(TitlebarComponent);