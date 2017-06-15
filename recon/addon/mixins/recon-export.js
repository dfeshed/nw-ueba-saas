import Mixin from 'ember-metal/mixin';
import observer from 'ember-metal/observer';
import service from 'ember-service/inject';
import { isEmpty } from 'ember-utils';
import computed, { match } from 'ember-computed-decorators';

export default Mixin.create({
  lastExtractLink: null,

  flashMessages: service(),

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

  willDestroyElement() {
    const status = this.get('status');
    if (status === 'init' || status === 'wait') {
      const { flashMessages, i18n } = this.getProperties('flashMessages', 'i18n');
      if (flashMessages && flashMessages.info) {
        const url = `${window.location.origin}/profile#jobs`;
        flashMessages.info(i18n.t('recon.extractWarning', { url }), { sticky: true });
      }
    }
    this._super(...arguments);
  },

  actions: {
    extract() {
      this.send('extractFiles');
    }
  }
});
