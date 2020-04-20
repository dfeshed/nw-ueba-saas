import { get } from '@ember/object';
import { validatePresence } from 'ember-changeset-validations/validators';
import { maybeValidate } from 'configure/validations/maybe';

export default {
  'enabled': validatePresence(true),
  'zip': validatePresence({ presence: true }),
  'foo.bar.baz': validatePresence({ presence: true }),
  'wat': validatePresence({ presence: true }),
  'optional': maybeValidate(validatePresence({ presence: true }), (changes) => get(changes, 'enabled'))
};
