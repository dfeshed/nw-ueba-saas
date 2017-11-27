import Component from 'ember-component';
import securityConfig from './config';
import computed from 'ember-computed-decorators';

export default Component.extend({

  tagName: 'hbox',

  classNames: ['security-configuration'],

  securityConfig,

  osType: null,

  config: null,

  arrangeBy: 'alphabetical',

  @computed('securityConfig', 'osType', 'config', 'arrangeBy')
  osTypeSecurityConfig(securityConfig, osType, config, arrangeBy) {
    const compareFunction = function(a, b) {
      if (arrangeBy === 'alphabetical') {
        if (b.value.toLowerCase() > a.value.toLowerCase()) {
          return -1;
        } else if (b.value.toLowerCase() < a.value.toLowerCase()) {
          return 1;
        } else {
          return 0;
        }
      }
      if (arrangeBy === 'status') {
        return b.disabled - a.disabled;
      }
    };
    const updateSecurityConfig = securityConfig[osType].map((sc) => {
      const disabled = config.some((c) => c.includes(sc.keyword));
      const label = disabled ? sc.label.red : sc.label.green;
      return { ...sc, label, disabled };
    });

    return updateSecurityConfig.sort(compareFunction);
  },

  /**
   * set to true if all the config values are having same status
   * @public
   */
  @computed('osTypeSecurityConfig')
  sameConfigStatus(osTypeSecurityConfig) {
    return osTypeSecurityConfig.every((c) => c.disabled) || osTypeSecurityConfig.every((c) => !c.disabled);
  }
});
