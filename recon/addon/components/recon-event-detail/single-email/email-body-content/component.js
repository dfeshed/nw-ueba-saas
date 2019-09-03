import Component from '@ember/component';
import layout from './template';
import { inject } from '@ember/service';
import computed from 'ember-computed-decorators';

const CHUNK_SIZE = 10000;

export default Component.extend({
  layout,
  portionsToRender: [],
  chunkToRender: 0,
  redux: inject(),

  @computed('portionsToRender', 'renderedAll', 'email')
  displayShowMoreButton(portionsToRender, renderedAll, email) {
    let renderedContentLength = 0;
    const portionLength = portionsToRender.length;
    const allEmails = this.get('redux').getState().recon.emails.emails;
    if (portionLength >= 1) {
      renderedContentLength = (portionLength - 1) * CHUNK_SIZE + portionsToRender[portionLength - 1].length;
    }
    if ((renderedAll && renderedContentLength) || (!renderedAll && allEmails[allEmails.length - 1].messageId !== email.messageId)) {
      return !(email.bodyContent.length <= renderedContentLength);
    } else if (allEmails[allEmails.length - 1].messageId === email.messageId) {
      return !(email.bodyContent.length <= renderedContentLength) && !renderedAll;
    }
  },

  @computed('email.bodyContent')
  emailPortions(emailContent) {
    if (!emailContent.length > CHUNK_SIZE) {
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

  @computed('portionsToRender', 'emailPortions')
  renderEmailBodyContent(portionsToRender, emailPortions) {
    if (emailPortions.length > 0 && portionsToRender.length <= 0) {
      this.set('portionsToRender', [emailPortions[0]]);
      return this.get('portionsToRender');
    } else if (portionsToRender.length > 0) {
      return portionsToRender.join('');
    } else {
      return [];
    }
  },

  actions: {
    showRemainingEmailContent() {
      const emailPortions = this.get('emailPortions');
      const emailChunk = this.get('chunkToRender') + 1;
      if (emailChunk <= emailPortions.length && emailPortions[emailChunk]) {
        this.set('chunkToRender', emailChunk);
        this.set('portionsToRender', this.get('portionsToRender').concat(emailPortions[emailChunk]));
      }
    }
  }
});

