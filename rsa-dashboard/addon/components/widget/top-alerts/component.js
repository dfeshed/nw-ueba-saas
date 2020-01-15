import classic from 'ember-classic-decorator';
import { classNames, tagName, layout as templateLayout } from '@ember-decorators/component';
import Component from '@ember/component';
import layout from './template';

@classic
@templateLayout(layout)
@tagName('vbox')
@classNames('top-alerts')
export default class TopAlerts extends Component {}
