import $ from 'jquery';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { later } from '@ember/runloop';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import { setColumnGroup } from 'investigate-events/actions/interaction-creators';
import {
  getSelectedColumnGroup,
  getColumnGroups
} from 'investigate-events/reducers/investigate/data-selectors';


const stateToComputed = (state) => ({
  reconSize: state.investigate.data.reconSize,
  isReconOpen: state.investigate.data.isReconOpen,
  columnGroups: getColumnGroups(state),
  selectedColumnGroup: getSelectedColumnGroup(state)
});

const dispatchToActions = {
  setColumnGroup
};

const EventsTable = Component.extend({
  classNames: 'rsa-investigate-events-table__header',
  tagName: 'hbox',

  @computed('reconSize')
  toggleEvents(size) {
    const isSizeNotMax = size !== RECON_PANEL_SIZES.MAX;
    return {
      class: isSizeNotMax ? 'shrink-diagonal-2' : 'expand-diagonal-4',
      title: isSizeNotMax ? 'investigate.events.shrink' : 'investigate.events.expand'
    };
  },

  actions: {
    attachTooltip() {
      later(() => {
        const customGroup = $('.ember-power-select-group-name').first();
        if (customGroup) {
          customGroup.attr('title', this.get('i18n').t('investigate.events.columnGroups.customTitle'));
        }
      }, 200);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(EventsTable);
