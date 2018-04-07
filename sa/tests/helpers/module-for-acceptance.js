import { resolve } from 'rsvp';
import { module } from 'qunit';
import startApp from '../helpers/start-app';
import destroyApp from '../helpers/destroy-app';
import { localStorageClear } from '../helpers/wait-for';
import teardownSockets from '../helpers/teardown-sockets';

export default function(name, options = {}) {
  module(name, {
    beforeEach() {
      this.application = startApp();

      if (options.beforeEach) {
        return options.beforeEach.apply(this, arguments);
      }
    },

    afterEach() {
      teardownSockets.apply(this);
      const afterEach = options.afterEach && options.afterEach.apply(this, arguments);
      return resolve(afterEach).then(() => {
        destroyApp(this.application);
        return localStorageClear();
      });
    }
  });
}
