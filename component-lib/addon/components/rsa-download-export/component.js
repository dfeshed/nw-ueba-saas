import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { debounce, later } from '@ember/runloop';
import { isEmpty } from '@ember/utils';

export default Component.extend({

  layout,

  flashMessages: service(),

  i18n: service(),

  lastExtractLink: null,

  iframeClass: null,

  sendMessage: () => {},

  /**
  * @public
  * Need this wrap this action in debouce as Firefox
  * has some issues with `next` loop. It fires the
  * action immediately, which cause the template
  * to render null as iframeSrc. Delay is added
  * to ensure that action is not fired immediately
  * for other browsers too.
  */
  debouncedAction() {
    later(() => {
      this.get('sendMessage')();
    }, 500);
  },

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
        const url = `${window.location.origin}/profile#jobs`;
        this.get('flashMessages').success(this.get('i18n').t('fileExtract.ready', { url }));
      }
      this.set('lastExtractLink', extractLink);
      debounce(this, this.debouncedAction, 200);
    }
    return source;
  }

});
