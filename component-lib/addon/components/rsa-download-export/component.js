import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { next } from '@ember/runloop';
import { isEmpty } from '@ember/utils';

export default Component.extend({

  layout,

  flashMessages: service(),

  i18n: service(),

  lastExtractLink: null,

  iframeClass: null,

  sendMessage: () => {},

  @computed('extractLink')
  iframeSrc(extractLink) {
    const lastExtractLink = this.get('lastExtractLink');
    const reallyDidChange = extractLink !== lastExtractLink;

    let source = null;
    if (reallyDidChange && !isEmpty(extractLink)) {
      // The extracted file is downloaded, only if the autoDownloadExtractedFiles preference
      // is set. Hence check the property before setting the download src to 'iframeSrc'
      if (this.get('isAutoDownloadFile')) {
        source = extractLink;
      } else {
        this.get('flashMessages').success(this.get('i18n').t('recon.extractedFileReady'));
      }
      this.set('lastExtractLink', extractLink);
      next(() => {
        // send out action to clear up files state in the next loop
        this.get('sendMessage')();
      });
    }
    return source;
  }

});
