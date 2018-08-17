import $ from 'jquery';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { later } from '@ember/runloop';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import { setColumnGroup } from 'investigate-events/actions/interaction-creators';
import { getSelectedColumnGroup } from 'investigate-events/reducers/investigate/data-selectors';

const stateToComputed = (state) => ({
  reconSize: state.investigate.data.reconSize,
  isReconOpen: state.investigate.data.isReconOpen,
  columnGroups: state.investigate.data.columnGroups,
  selectedColumnGroup: getSelectedColumnGroup(state)
});

const dispatchToActions = {
  setColumnGroup
};

const EventsTable = Component.extend({
  classNames: 'rsa-investigate-events-table__header',
  tagName: 'hbox',
  i18n: service(),
  toggleReconSize: () => {},
  toggleSlaveFullScreen: () => {},

  @computed('columnGroups', 'i18n.locale')
  localizedColumnGroups(columnGroups) {
    if (columnGroups) {
      return [
        { groupName: this.get('i18n').t('investigate.events.columnGroups.custom'), options: columnGroups.filter((column) => !column.ootb) },
        { groupName: this.get('i18n').t('investigate.events.columnGroups.default'), options: columnGroups.filter((column) => column.ootb) }
      ];
    }
  },

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
