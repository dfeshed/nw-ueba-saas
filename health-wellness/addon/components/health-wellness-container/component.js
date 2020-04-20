import classic from 'ember-classic-decorator';
import layout from './template';
import Component from '@ember/component';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import { connect } from 'ember-redux';
import { getMonitorList } from 'health-wellness/actions/data-creators';
import { next } from '@ember/runloop';

const dispatchToActions = {
  getMonitorList
};

const stateToComputed = ({ hw }) => ({
  isError: hw.isError,
  isDataLoading: hw.isMonitorLoading
});

@classic
@classNames('hw-container')
@templateLayout(layout)
class HWContainer extends Component {

  init() {
    super.init(...arguments);
    next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.send('getMonitorList');
      }
    });
  }

}

export default connect(stateToComputed, dispatchToActions)(HWContainer);


