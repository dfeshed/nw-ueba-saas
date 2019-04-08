import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { getDownloadOptions } from 'investigate-events/reducers/investigate/event-results/selectors';
import { didDownloadFiles, extractFiles } from 'investigate-events/actions/notification-creators';

const stateToComputed = (state) => ({
  isAllEventsSelected: state.investigate.eventResults.allEventsSelected,
  selectedEventIds: state.investigate.eventResults.selectedEventIds,
  downloadOptions: getDownloadOptions(state),
  extractLink: state.investigate.files.fileExtractLink,
  isAutoDownloadFile: state.investigate.files.isAutoDownloadFile
});

const dispatchToActions = {
  didDownloadFiles,
  extractFiles
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
    return { name: this.get('i18n').t(`investigate.events.download.${isAllEventsSelected ? 'all' : 'selected'}`) };
  },

  actions: {
    downloadFiles(option) {
      const isAllEventsSelected = this.get('isAllEventsSelected');
      const { eventDownloadType, fileType, sessionIds } = option;
      this.send('extractFiles', eventDownloadType, fileType, sessionIds, isAllEventsSelected);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DownloadDropdown);
