import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { run } from '@ember/runloop';
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

@classic
@tagName('hbox')
@classNames('host-explore')
class Explore extends Component {
  @service
  eventBus;

  willDestroyElement() {
    super.willDestroyElement(...arguments);
    this.get('eventBus').off('rsa-application-click', this, 'onApplicationClick');
  }

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
  }

  didInsertElement() {
    super.didInsertElement(...arguments);
    this.get('eventBus').on('rsa-application-click', this, 'onApplicationClick');
  }
}

export default connect(stateToComputed, dispatchToActions)(Explore);
