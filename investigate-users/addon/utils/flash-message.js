import { lookup } from 'ember-dependency-lookup';

export const flashErrorMessage = (message) => {
  const flashMessagesService = lookup('service:flashMessages');
  const i18n = lookup('service:i18n');
  flashMessagesService.error(i18n.t(message));
};