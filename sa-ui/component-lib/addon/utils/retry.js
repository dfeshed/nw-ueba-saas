import { later } from '@ember/runloop';
import { Promise } from 'rsvp';

export const retry = function(isFinished, timeout) {
  let attempts = 0;
  return new Promise((resolve, reject) => {
    function loop() {
      attempts = attempts + 1;
      later(next, attempts * timeout);
    }

    function next() {
      Promise.resolve(isFinished()).then((finished) => {
        if (finished) {
          reject();
        } else if (attempts > 5) {
          resolve();
        } else {
          loop();
        }
      });
    }

    loop();
  });
};
