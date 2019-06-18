import { next } from '@ember/runloop';
import { waitUntil, findAll } from '@ember/test-helpers';
import { Promise } from 'rsvp';

const timeout = 10000;
const CSS_CLASS_ENTITY_HAS_BEEN_VALIDATED = '.entity-has-been-validated';

export const waitForEntityHighlight = async function() {
  await new Promise(async(resolve, reject) => {
    await waitUntil(() => findAll(CSS_CLASS_ENTITY_HAS_BEEN_VALIDATED).length > 0, { timeout })
      .then(() => {
        next(null, resolve);
      }).catch(() => {
        next(null, reject);
      });
  });
};
