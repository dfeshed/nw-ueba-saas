import { run } from '@ember/runloop';
import Application from '../../app';
import config from '../../config/environment';

import registerFlatpickrHelpers from 'ember-flatpickr/test-support/helpers';

registerFlatpickrHelpers();

export default function startApp(attrs) {
  const attributes = {
    ...config.APP,
    autoboot: true,
    ...attrs
  };

  return run(() => {
    const application = Application.create(attributes);
    application.setupForTesting();
    application.injectTestHelpers();
    return application;
  });
}
