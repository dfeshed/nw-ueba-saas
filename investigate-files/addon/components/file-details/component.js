import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { selectedTabComponent } from 'investigate-files/reducers/visuals/selectors';

const stateToComputed = (state) => ({
  selectedTabComponent: selectedTabComponent(state)
});

@classic
@tagName('page')
@classNames('rsa-investigate-files')
class DetailComponent extends Component {}

export default connect(stateToComputed)(DetailComponent);
