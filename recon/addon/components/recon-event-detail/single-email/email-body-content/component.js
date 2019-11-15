import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { run } from '@ember/runloop';
import _ from 'lodash';
import { inject as service } from '@ember/service';

const CHUNK_SIZE = 10000;

export default Component.extend({
  layout,
  eventBus: service(),
  portionsToRender: [],
  chunkToRender: 0,
  percentRendered: null,
  frame: null,
  resizeIframe: null,
  actualLink: null,
  disableContextMenu: null,
  randomNumber: 1,
  emailRenderId: '',

  init() {
    this._super(...arguments);
    const randomNumber = Math.floor(Math.random() * 10000);
    this.set('emailRenderId', `emailId-${randomNumber}`);
  },

  didRender() {
    this.set('frame', document.getElementById(this.get('emailRenderId')));
    this.get('frame').addEventListener('load', this.resizeIframe = () => {
      run.next(() => {
        const iframeDoc = this.get('frame').contentWindow.document;
        const iframeHeight = iframeDoc.body.scrollHeight + 70;
        this.get('frame').style.height = iframeHeight.toString().concat('px');
        const hyperlinks = iframeDoc.getElementsByTagName('a');
        for (const link of hyperlinks) {
          // anchor tags do not show up as hyperlinks if href is not defined
          link.href = 'javascript:void(0);';

          // disable right-click
          link.addEventListener('contextmenu', this.disableContextMenu = (event) => {
            event.preventDefault();
          });

          // As part of reconstruction, investigate MT adds "recon_show_ouri" as the click handler.
          // UI just needs to provide implementation for "recon_show_ouri"
          link.recon_show_ouri = (a) => {
            this.set('actualLink', a.getAttribute('recon-ouri'));
          };
        }
      });
    });
  },

  willDestroyElement() {
    this._super(...arguments);
    const iframeDoc = this.get('frame').contentWindow.document;
    const hyperlinks = iframeDoc.getElementsByTagName('a');
    for (const link of hyperlinks) {
      link.removeEventListener('contextmenu', this.disableContextMenu);
      link.removeEventListener('click', link.recon_show_ouri);
    }
    this.get('frame').removeEventListener('load', this.resizeIframe);
  },

  @computed('portionsToRender', 'email')
  displayShowRemainingButton(portionsToRender, email) {
    const renderedContentLength = portionsToRender.join('').length;
    return email.realBodyContentLength > renderedContentLength;
  },

  @computed('email.bodyContent')
  emailPortions(emailContent) {
    if (emailContent.length <= CHUNK_SIZE) {
      return [emailContent];
    }

    const initialEmailPortion = emailContent.substr(0, CHUNK_SIZE);
    this.set('portionsToRender', [initialEmailPortion]);

    const portions = [initialEmailPortion];
    let remainingText = emailContent.substr(CHUNK_SIZE);
    while (remainingText.length > 0) {
      const chunk = remainingText.slice(0, CHUNK_SIZE);
      remainingText = remainingText.slice(CHUNK_SIZE);
      portions.push(chunk);
    }
    return portions;
  },

  @computed('portionsToRender', 'emailPortions', 'email.bodyContentType')
  renderEmailBodyContent(portionsToRender, emailPortions, contentType) {
    let toRender = '';
    if (emailPortions) {
      if (emailPortions.length > 0 && portionsToRender.length <= 0) {
        this.set('portionsToRender', [emailPortions[0]]);
        toRender = _.unescape(this.get('portionsToRender'));
      } else if (portionsToRender.length > 0) {
        toRender = _.unescape(portionsToRender.join(''));
      }
    }
    return contentType === 'PlainText' ? this.htmlify(toRender) : toRender;
  },

  htmlify(plainText) {
    return plainText
      .replace(/(?:\r\n|\r|\n)/g, '<br>')
      .replace(/\t/g, '&emsp;&emsp;');
  },

  @computed('displayedPercent')
  showPercentMessage(displayedPercent) {
    return displayedPercent !== 100;
  },

  @computed('displayedPercent')
  percentText(displayedPercent) {
    return (displayedPercent === 0) ? '< 1' : displayedPercent;
  },

  @computed('email', 'percentRendered')
  displayedPercent(email, percentRendered) {
    if (percentRendered) {
      return percentRendered;
    }
    if (!(email.realBodyContentLength >= CHUNK_SIZE)) {
      return 100;
    }
    return Math.floor((CHUNK_SIZE / email.realBodyContentLength) * 100);
  },

  @computed('displayedPercent', 'percentRendered')
  remainingEmailContentLabel(displayedPercent, percentRendered) {
    const percentToUse = percentRendered || displayedPercent;
    const percentLabel = {
      remainingPercent: (percentToUse === 0) ? '99+' : 100 - percentToUse
    };
    return this.get('i18n').t('recon.emailView.showRemainingPercent', percentLabel);
  },

  actions: {
    showRemainingEmailContent() {
      const emailPortions = this.get('emailPortions');
      const emailChunk = this.get('chunkToRender') + 1;
      if (emailChunk <= emailPortions.length && emailPortions[emailChunk]) {
        this.set('chunkToRender', emailChunk);
        this.set('portionsToRender', this.get('portionsToRender').concat(emailPortions[emailChunk]));
        const emailContentLength = this.get('email.realBodyContentLength');
        let percentRendered = Math.ceil((this.get('portionsToRender').join('').length / emailContentLength) * 100);
        percentRendered = (percentRendered > 99) ? 100 : percentRendered;
        this.set('percentRendered', percentRendered);
      }
    },

    closeLinkModal() {
      this.set('actualLink', null);
    }
  }
});
