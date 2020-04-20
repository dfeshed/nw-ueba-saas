/* global authenticateSession */

import { run } from '@ember/runloop';
import Application from '../../app';
import config from '../../config/environment';
import './authenticate-session';

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
    authenticateSession();
    return application;
  });
}
