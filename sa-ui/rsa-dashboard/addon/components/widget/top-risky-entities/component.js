import classic from 'ember-classic-decorator';
import { classNames, tagName, layout as templateLayout } from '@ember-decorators/component';
import Component from '@ember/component';
import layout from './template';

@classic
@templateLayout(layout)
@tagName('vbox')
@classNames('top-risk-entity')
export default class TopRiskyEntities extends Component {
  config = null; // Dashlet configuration
}
