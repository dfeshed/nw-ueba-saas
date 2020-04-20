import Service, { inject as service } from '@ember/service';
import { isEmpty } from '@ember/utils';

export default Service.extend({

  flashMessages: service(),

  i18n: service(),

  display({ messageLocaleKey, errorCode, type, sendServerMessage, serverMessage }) {
    const i18n = this.get('i18n');
    const errorMessage = sendServerMessage ? serverMessage : i18n.t(messageLocaleKey, { errorCode, type });

    if (!isEmpty(errorMessage)) {
      this.get('flashMessages').error(errorMessage);
      return errorMessage;
    }
  }

});
