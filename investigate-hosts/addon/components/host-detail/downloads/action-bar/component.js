import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { toggleHostDetailsFilter } from 'investigate-hosts/actions/ui-state-creators';
import { connect } from 'ember-redux';

const dispatchToActions = {
  toggleHostDetailsFilter
};

const ActionBarComponent = Component.extend({
  tagName: 'section',
  classNames: ['downloads-action-bar'],
  accessControl: service(),
  actions: {
    showFilterPanel(openFilterPanel) {
      openFilterPanel();
      this.send('toggleHostDetailsFilter', true);
    }
  }
});

export default connect(undefined, dispatchToActions)(ActionBarComponent);
