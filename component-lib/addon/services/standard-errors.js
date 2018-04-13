import Service, { inject as service } from '@ember/service';
import { isEmpty } from '@ember/utils';

export default Service.extend({

  flashMessages: service(),

  i18n: service(),

  display({ messageLocaleKey, errorCode, sendServerMessage, serverMessage }) {
    const i18n = this.get('i18n');

    const codeStr = i18n.t('errorDictionaryMessages.code');
    const errorMessage = sendServerMessage ? serverMessage : i18n.t(messageLocaleKey);

    if (!isEmpty(errorMessage)) {
      const flashMessage = `${errorMessage} (${codeStr} ${errorCode})`;

      this.get('flashMessages').error(flashMessage);

      return flashMessage;
    }
  }

});
