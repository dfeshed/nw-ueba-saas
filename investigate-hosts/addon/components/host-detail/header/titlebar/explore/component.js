import Component from '@ember/component';
import { run } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { toggleExploreSearchResults } from 'investigate-hosts/actions/ui-state-creators';

const stateToComputed = (state) => ({
  showResults: state.endpoint.explore.showSearchResults,
  componentName: state.endpoint.explore.componentName,
  fileSearchResults: state.endpoint.explore.fileSearchResults
});

const dispatchToActions = {
  toggleExploreSearchResults
};
const Explore = Component.extend({

  tagName: 'hbox',

  classNames: 'host-explore',

  eventBus: service(),

  willDestroyElement() {
    this._super(...arguments);
    this.get('eventBus').off('rsa-application-click', this, 'onApplicationClick');
  },

  onApplicationClick(target) {
    const result = this.get('fileSearchResults');
    const showSearchResults = target ? !target.closest('.host-explore') && !target.classList.contains('rsa-icon-search') : true;
    if (showSearchResults) {
      run.next(() => {
        if (!this.get('isDestroyed') && !this.get('isDestroying')) {
          this.send('toggleExploreSearchResults', false);
        }
      });
    } else if (result && result.length) {
      this.send('toggleExploreSearchResults', true);
    }
  },

  didInsertElement() {
    this._super(...arguments);
    this.get('eventBus').on('rsa-application-click', this, 'onApplicationClick');
  }
});
export default connect(stateToComputed, dispatchToActions)(Explore);
