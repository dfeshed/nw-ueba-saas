import $ from 'jquery';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { later } from '@ember/runloop';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import { setColumnGroup } from 'investigate-events/actions/interaction-creators';
import { getSelectedColumnGroup } from 'investigate-events/reducers/investigate/data-selectors';
import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';
import { shouldStartAtOldest, actualEventCount } from 'investigate-events/reducers/investigate/event-results/selectors';
import { thousandFormat } from 'component-lib/utils/numberFormats';

const stateToComputed = (state) => ({
  reconSize: state.investigate.data.reconSize,
  isReconOpen: state.investigate.data.isReconOpen,
  eventTimeSortOrder: state.investigate.eventResults.eventTimeSortOrder,
  columnGroups: state.investigate.data.columnGroups,
  selectedColumnGroup: getSelectedColumnGroup(state),
  count: thousandFormat(state.investigate.eventCount.data),
  isAtThreshold: resultCountAtThreshold(state),
  shouldStartAtOldest: shouldStartAtOldest(state),
  actualEventCount: thousandFormat(actualEventCount(state))
});

const dispatchToActions = {
  setColumnGroup
};

const HeaderContainer = Component.extend({
  classNames: 'rsa-investigate-events-table__header',
  tagName: 'hbox',
  i18n: service(),
  toggleReconSize: () => {},
  toggleSlaveFullScreen: () => {},

  @computed('shouldStartAtOldest', 'i18n')
  eventResultSetStart(shouldStartAtOldest, i18n) {
    return shouldStartAtOldest ? i18n.t('investigate.events.oldest') : i18n.t('investigate.events.newest');
  },

  @computed('eventTimeSortOrder', 'i18n')
  abbreviatedSortOrder(eventTimeSortOrder, i18n) {
    return i18n.t(`investigate.events.abbr.${eventTimeSortOrder}`);
  },

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

export default connect(stateToComputed, dispatchToActions)(HeaderContainer);
