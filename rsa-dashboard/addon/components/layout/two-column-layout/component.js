import classic from 'ember-classic-decorator';
import { classNames, tagName, layout as templateLayout } from '@ember-decorators/component';
import Component from '@ember/component';
import layout from './template';

@classic
@templateLayout(layout)
@classNames('two-column-layout')
@tagName('hbox')
export default class TwoColumnLayout extends Component {
  config = null;
}
