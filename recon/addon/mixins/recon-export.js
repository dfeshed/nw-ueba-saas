import Mixin from 'ember-metal/mixin';
import observer from 'ember-metal/observer';
import { isEmpty } from 'ember-utils';
import computed, { match } from 'ember-computed-decorators';

export default Mixin.create({
  lastExtractLink: null,

  @match('status', /init|wait/)
  isDownloading: false,

  @computed('extractLink')
  iframeSrc(extractLink) {
    const lastExtractLink = this.get('lastExtractLink');
    const reallyDidChange = extractLink !== lastExtractLink;
    let source = null;
    if (reallyDidChange && !isEmpty(extractLink)) {
      this.set('lastExtractLink', extractLink);
      source = extractLink;
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
