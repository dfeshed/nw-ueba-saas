import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import {
  toggleListVisibility
} from 'rsa-list-manager/actions/creators/creators';
import {
  caption,
  titleTooltip
} from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  caption: caption(state, attrs.stateLocation),
  titleTooltip: titleTooltip(state, attrs.stateLocation)
});

const dispatchToActions = {
  toggleListVisibility
};

const ListMenuTrigger = Component.extend({
  layout,
  classNames: ['list-menu-trigger', 'rsa-button-group'],
  stateLocation: undefined,
  isDisabled: false,

  actions: {
    triggerClicked() {
      this.send('toggleListVisibility', this.get('stateLocation'));
      this.get('listOpened')();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ListMenuTrigger);
