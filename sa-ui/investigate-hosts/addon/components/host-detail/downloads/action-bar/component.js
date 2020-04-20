import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, tagName } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { toggleHostDetailsFilter } from 'investigate-hosts/actions/ui-state-creators';
import { connect } from 'ember-redux';

const dispatchToActions = {
  toggleHostDetailsFilter
};

@classic
@tagName('section')
@classNames('downloads-action-bar')
class ActionBarComponent extends Component {
  @service
  accessControl;

  @action
  showFilterPanel(openFilterPanel) {
    openFilterPanel();
    this.send('toggleHostDetailsFilter', true);
  }
}

export default connect(undefined, dispatchToActions)(ActionBarComponent);
