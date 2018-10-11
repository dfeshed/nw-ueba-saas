import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { getDownloadOptions } from 'investigate-events/reducers/investigate/event-results/selectors';

const stateToComputed = (state) => ({
  isAllEventsSelected: state.investigate.eventResults.allEventsSelected,
  selectedEventIds: state.investigate.eventResults.selectedEventIds,
  downloadOptions: getDownloadOptions(state)
});

const dispatchToActions = {
  // TODO for download implementation
  downloadByPreference: () => {}
};

const DownloadDropdown = Component.extend({
  i18n: service(),
  accessControl: service(),
  classNames: ['rsa-investigate-events-table__header__downloadEvents'],
  classNameBindings: ['isDisabled'],

  @alias('accessControl.hasInvestigateContentExportAccess') permissionAllowsDownload: true,

  @computed('selectedEventIds', 'isAllEventsSelected')
  isDisabled(selectedEventIds, isAllEventsSelected) {
    if ((selectedEventIds && selectedEventIds.length) || isAllEventsSelected) {
      return false;
    }
    return true;
  },

  @computed('isAllEventsSelected')
  downloadTitle(isAllEventsSelected) {
    return { name: this.get('i18n').t(`investigate.events.download.${isAllEventsSelected ? 'all' : 'selected'}`) }; // TODO disable false
  }
});

export default connect(stateToComputed, dispatchToActions)(DownloadDropdown);
