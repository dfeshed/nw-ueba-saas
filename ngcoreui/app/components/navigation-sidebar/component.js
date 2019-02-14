import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { isArchiver, isBroker, isConcentrator, isDecoder, isLogDecoder } from 'ngcoreui/reducers/selectors';

const stateToComputed = (state) => ({
  isArchiver: isArchiver(state),
  isBroker: isBroker(state),
  isConcentrator: isConcentrator(state),
  isDecoder: isDecoder(state),
  isLogDecoder: isLogDecoder(state)
});

const navigationSidebar = Component.extend({
  host: window.location.host,

  router: service(),

  @computed('router.currentRouteName')
  treeActive: (currentRouteName) => currentRouteName === 'tree',

  tagName: 'vbox',
  classNames: ['navigation-sidebar-outer', 'border-line-right', 'border-line-top', 'flexi-fit', 'max-height']
});

export default connect(stateToComputed)(navigationSidebar);
