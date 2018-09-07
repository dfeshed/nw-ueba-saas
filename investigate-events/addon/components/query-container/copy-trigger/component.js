import Component from '@ember/component';
import { connect } from 'ember-redux';
import { isEmpty } from '@ember/utils';
import computed from 'ember-computed-decorators';
import { run } from '@ember/runloop';
import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';

const stateToComputed = (state) => ({
  isDisabled: isEmpty(state.investigate.queryNode.pillsData),
  copyable: encodeMetaFilterConditions(state.investigate.queryNode.pillsData)
});

const CopyTrigger = Component.extend({
  classNames: ['copy-trigger'],
  classNameBindings: [
    'isDisabled'
  ],

  @computed('isDisabled', 'copyable', 'i18n')
  title: (isDisabled, copyable, i18n) => {
    const label = i18n.t(`investigate.queryStats.${isDisabled ? 'disabledCopy' : 'copy'}`);
    return `${label} ${copyable}`;
  },

  didReceiveAttrs() {
    this._super(...arguments);

    run.schedule('afterRender', () => {
      if (window.Clipboard) {
        if (this.clipboard) {
          // clear the old clipboard object
          this.clipboard.destroy();
        }

        this.clipboard = new window.Clipboard('.copy-trigger i', {
          text: () => this.get('copyable')
        });
      }
    });
  },

  willDestroyElement() {
    this._super(...arguments);

    if (this.clipboard) {
      this.clipboard.destroy();
      this.clipboard = null;
    }
  }

});

export default connect(stateToComputed)(CopyTrigger);
