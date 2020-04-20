import Component from '@ember/component';
import computed, { match, alias } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { areAllEventsSelected, getDownloadOptions } from 'investigate-events/reducers/investigate/event-results/selectors';
import { didDownloadFiles, extractFiles } from 'investigate-events/actions/notification-creators';

const stateToComputed = (state) => ({
  areAllEventsSelected: areAllEventsSelected(state),
  selectedEventIds: state.investigate.eventResults.selectedEventIds,
  downloadOptions: getDownloadOptions(state),
  extractLink: state.investigate.files.fileExtractLink,
  status: state.investigate.files.fileExtractStatus,
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

  @match('status', /init|wait/)
  isDownloading: false,

  // dropDown will be disabled when no event is selected or a download is already in progress
  @computed('selectedEventIds', 'isDownloading')
  isDisabled(selectedEventIds, isDownloading) {
    if (!selectedEventIds) {
      return true;
    }
    const ids = Object.keys(selectedEventIds);
    if (((ids && ids.length)) && !isDownloading) {
      return false;
    }
    return true;
  },

  @computed('areAllEventsSelected', 'isDownloading', 'i18n')
  downloadTitle(areAllEventsSelected, isDownloading, i18n) {
    if (isDownloading) {
      return { name: i18n.t('investigate.events.download.isDownloading') };
    } else {
      return { name: i18n.t(`investigate.events.download.${areAllEventsSelected ? 'all' : 'selected'}`) };
    }
  },

  actions: {
    downloadFiles(option) {
      const areAllEventsSelected = this.get('areAllEventsSelected');
      const { eventDownloadType, fileType, sessionIds } = option;
      this.send('extractFiles', eventDownloadType, fileType, sessionIds, areAllEventsSelected);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DownloadDropdown);
