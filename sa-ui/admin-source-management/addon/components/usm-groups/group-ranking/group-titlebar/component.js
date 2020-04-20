import Component from '@ember/component';

const GroupWizardTitlebar = Component.extend({
  tagName: 'vbox',
  classNames: ['group-wizard-titlebar'],
  // step object required to be passed in
  step: null
});

export default GroupWizardTitlebar;
