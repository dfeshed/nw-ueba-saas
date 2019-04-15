import Component from '@ember/component';
import computed, { match, alias } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { getDownloadOptions } from 'investigate-events/reducers/investigate/event-results/selectors';
import { didDownloadFiles, extractFiles } from 'investigate-events/actions/notification-creators';

const stateToComputed = (state) => ({
  isAllEventsSelected: state.investigate.eventResults.allEventsSelected,
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
  classNameBindings: ['isDisabled', 'extractWarningClass'],

  @alias('accessControl.hasInvestigateContentExportAccess') permissionAllowsDownload: true,

  @match('status', /init|wait/)
  isDownloading: false,

  // dropDown will be disabled when no event is selected or a download is already in progress
  @computed('selectedEventIds', 'isAllEventsSelected', 'isDownloading')
  isDisabled(selectedEventIds, isAllEventsSelected, isDownloading) {
    if (((selectedEventIds && selectedEventIds.length) || isAllEventsSelected) && !isDownloading) {
      return false;
    }
    return true;
  },

  // binds class extract-warned to component when file extraction is queued
  // due to navigating away in the middle of download
  @computed('status')
  extractWarningClass(status) {

    if (status === 'queued') {
      const { flashMessages, i18n } = this.getProperties('flashMessages', 'i18n');
      if (flashMessages && flashMessages.info) {
        const url = `${window.location.origin}/profile#jobs`;
        flashMessages.info(i18n.t('recon.extractWarning', { url }), { sticky: true });
        return 'extract-warned';
      }
    }
  },

  @computed('isAllEventsSelected', 'isDownloading', 'i18n')
  downloadTitle(isAllEventsSelected, isDownloading, i18n) {
    if (isDownloading) {
      return { name: i18n.t('investigate.events.download.isDownloading') };
    } else {
      return { name: i18n.t(`investigate.events.download.${isAllEventsSelected ? 'all' : 'selected'}`) };
    }
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
