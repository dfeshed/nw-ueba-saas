import Ember from 'ember';
import Application from '../../app';
import config from '../../config/environment';
import registerPowerSelectHelpers from '../../tests/helpers/ember-power-select';
import 'ember-wait-for-test-helper/wait-for';

const { assign, run } = Ember;

registerPowerSelectHelpers();

export default function startApp(attrs) {
  let application;

  // use defaults, but you can override
  const attributes = assign({}, config.APP, attrs);

  run(() => {
    application = Application.create(attributes);
    application.setupForTesting();
    application.injectTestHelpers();
  });

  return application;
}
