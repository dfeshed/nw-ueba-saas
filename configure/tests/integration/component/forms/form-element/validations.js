import { validatePresence } from 'ember-changeset-validations/validators';

export default {
  'zip': validatePresence({ presence: true }),
  'foo.bar.baz': validatePresence({ presence: true })
};
