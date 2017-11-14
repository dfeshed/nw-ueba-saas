import Mixin from 'ember-metal/mixin';
import observer from 'ember-metal/observer';
import { isEmpty } from 'ember-utils';
import computed, { match } from 'ember-computed-decorators';
import service from 'ember-service/inject';

export default Mixin.create({
  flashMessages: service(),
  i18n: service(),

  lastExtractLink: null,

  @match('status', /init|wait/)
  isDownloading: false,

  @computed('extractLink')
  iframeSrc(extractLink) {
    const lastExtractLink = this.get('lastExtractLink');
    const reallyDidChange = extractLink !== lastExtractLink;
    let source = null;
    if (reallyDidChange && !isEmpty(extractLink)) {
      // The extracted file is downloaded, only if the autoDownloadExtractedFiles preference
      // is set. Hence check the property before setting the download src to 'iframeSrc'
      if (this.get('isAutoDownloadFile')) {
        this.set('lastExtractLink', extractLink);
        source = extractLink;
      } else {
        this.get('flashMessages').success(this.get('i18n').t('recon.extractedFileReady'));
      }
    }
    return source;
  },

  iframeSrcWatcher: observer('iframeSrc', function() {
    const source = this.get('iframeSrc');
    if (source !== null) {
      this.send('didDownloadFiles');
    }
  }),

  actions: {
    extract() {
      this.send('extractFiles');
    }
  }
});
