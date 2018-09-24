import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';

const stateToComputed = (state) => ({
  isAllEventsSelected: state.investigate.eventResults.allEventsSelected,
  selectedEventIds: state.investigate.eventResults.selectedEventIds
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
    return this.get('i18n').t(`investigate.events.download.${isAllEventsSelected ? 'all' : 'selected'}`);
  },

  @computed()
  selectedDownloadPreference() {
    // TODO for download implementation
    return [
      'Download preference 1'
    ];
  }
});

export default connect(stateToComputed, dispatchToActions)(DownloadDropdown);
