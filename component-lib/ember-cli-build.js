/* eslint-env node */
const EmberAddon = require('ember-cli/lib/broccoli/ember-addon');

const { commonBuildOptions } = require('../common');

module.exports = function(defaults) {
  const options = {
    ...defaults,
    ...commonBuildOptions(__dirname),
    flatpickr: {
      locales: ['ar', 'at', 'be', 'bg', 'bn', 'cat', 'cs', 'cy', 'da', 'de', 'eo', 'es', 'et', 'fa', 'fi', 'fr', 'gr', 'he', 'hi', 'hr', 'hu', 'id', 'it', 'ja', 'ko', 'lt', 'lv', 'mk', 'mn', 'ms', 'my', 'nl', 'no', 'pa', 'pl', 'pt', 'ro', 'ru', 'si', 'sk', 'sl', 'sq', 'sr', 'sv', 'th', 'tr', 'uk', 'vn', 'zh']
    }
  };

  const app = new EmberAddon(options);
  return app.toTree();
};